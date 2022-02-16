package com.as.eventalertandroid.ui.main.notifications;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.data.model.SubscriptionEntity;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.service.EventService;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.as.eventalertandroid.ui.common.event.EventDetailsFragment;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.as.eventalertandroid.ui.main.notifications.settings.NotificationsSettingsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NotificationsFragment extends Fragment implements NotificationsAdapter.ClickListener {

    @BindView(R.id.notificationsRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.notificationsInfoTextView)
    TextView infoTextView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private NotificationsAdapter adapter;
    private SubscriptionService subscriptionService = RetrofitClient.getRetrofitInstance().create(SubscriptionService.class);
    private EventService eventService = RetrofitClient.getRetrofitInstance().create(EventService.class);
    private CounterListener counterListener;
    private long notificationsNotReadCount;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            counterListener = (CounterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        adapter = new NotificationsAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter.setOnClickListener(this);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        CompletableFuture.
                supplyAsync(() -> LocalDatabase.getInstance(getContext())
                        .eventNotificationDao()
                        .findByUserId(Session.getInstance().getUser().id))
                .thenAccept(eventsNotifications -> {
                    notificationsNotReadCount = eventsNotifications.stream().filter(en -> !en.getViewed()).count();
                    infoTextView.setText(
                            String.format(getString(R.string.notifications_with_new),
                                    eventsNotifications.size(),
                                    notificationsNotReadCount));

                    counterListener.onNotificationsCounterChange(this, notificationsNotReadCount);

                    adapter.setEventsNotifications(eventsNotifications);
                    adapter.notifyDataSetChanged();
                });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.notificationsSettingsButton)
    void onSettingsClicked() {
        NotificationsSettingsFragment notificationsSettingsFragment = new NotificationsSettingsFragment();
        LocalDatabase localDatabase = LocalDatabase.getInstance(requireContext());
        SubscriptionEntity subscription = localDatabase.subscriptionDao().findByUserId(Session.getInstance().getUser().id);

        if (subscription == null || subscription.getDeviceToken() == null) {
            ((MainActivity) requireActivity()).setFragment(notificationsSettingsFragment);
        } else {
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.show();

            subscriptionService.getByDeviceToken(subscription.getDeviceToken())
                    .thenAccept(s -> {
                        progressDialog.dismiss();
                        notificationsSettingsFragment.setSubscription(s);
                        ((MainActivity) requireActivity()).setFragment(notificationsSettingsFragment);
                    })
                    .exceptionally(throwable -> {
                        progressDialog.dismiss();
                        ErrorHandler.showMessage(requireActivity(), throwable);
                        return null;
                    });
        }
    }

    @Override
    public void onItemClicked(NotificationsAdapter source, EventNotificationEntity eventNotification) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        eventService.getById(eventNotification.getEventId())
                .thenAccept(event -> {
                    progressDialog.dismiss();

                    if (!eventNotification.getViewed()) {
                        eventNotification.setViewed(true);

                        counterListener.onNotificationsCounterChange(this, --notificationsNotReadCount);

                        CompletableFuture.runAsync(() -> {
                            LocalDatabase localDatabase = LocalDatabase.getInstance(requireContext());
                            localDatabase.eventNotificationDao().update(eventNotification);
                        });
                    }

                    EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                    eventDetailsFragment.setEvent(event);
                    ((MainActivity) requireActivity()).setFragment(eventDetailsFragment);
                })
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventNotification(EventNotificationEntity eventNotification) {
        adapter.addEventNotification(eventNotification);
        recyclerView.scrollToPosition(0);
        counterListener.onNotificationsCounterChange(this, ++notificationsNotReadCount);
        updateCounters();
    }

    public void updateCounters() {
        CompletableFuture.
                supplyAsync(() -> LocalDatabase.getInstance(getContext())
                        .eventNotificationDao()
                        .findByUserId(Session.getInstance().getUser().id))
                .thenAccept(eventsNotifications -> {
                    notificationsNotReadCount = eventsNotifications.stream().filter(en -> !en.getViewed()).count();
                    infoTextView.setText(
                            String.format(getString(R.string.notifications_with_new),
                                    eventsNotifications.size(),
                                    notificationsNotReadCount));
                });
    }

    public interface CounterListener {
        void onNotificationsCounterChange(NotificationsFragment fragment, long value);
    }

}
