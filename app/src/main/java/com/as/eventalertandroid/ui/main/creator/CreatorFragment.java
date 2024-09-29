package com.as.eventalertandroid.ui.main.creator;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.Event;
import com.as.eventalertandroid.net.service.EventService;
import com.as.eventalertandroid.ui.common.event.EventDetailsFragment;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.as.eventalertandroid.ui.main.home.list.EventAdapter;

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

public class CreatorFragment extends Fragment implements
        EventAdapter.ClickListener,
        NewEventFragment.CreationListener {

    @BindView(R.id.creatorInfoEventsTextView)
    TextView infoEventsTextView;
    @BindView(R.id.creatorProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.creatorNoResultsTextView)
    TextView noResultsTextView;
    @BindView(R.id.creatorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private EventAdapter adapter;
    private final EventService eventService = RetrofitClient.getInstance().create(EventService.class);
    private final Session session = Session.getInstance();
    private boolean isInitialSync = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EventAdapter(getContext());
        adapter.setShowImage(false);
        adapter.setShowDistance(false);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creator, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        updateCounter();
        if (!isInitialSync && adapter.getItemCount() == 0) {
            noResultsTextView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(EventAdapter source, Event event) {
        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
        eventDetailsFragment.setEvent(event);
        ((MainActivity) requireActivity()).setFragment(eventDetailsFragment);
    }

    @Override
    public void onNewEventCreated(NewEventFragment source, Event event) {
        session.increaseUserReportsNumber();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            adapter.addEvent(event);
            recyclerView.scrollToPosition(0);
            updateCounter();
            if (noResultsTextView.getVisibility() == View.VISIBLE) {
                noResultsTextView.setVisibility(View.GONE);
            }
        }, 400);
    }

    @OnClick(R.id.creatorNewEventButton)
    void onNewEventClicked() {
        if (!session.isUserLocationSet()) {
            Toast.makeText(requireContext(), getString(R.string.message_location_not_set), Toast.LENGTH_SHORT).show();
            return;
        }
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        NewEventFragment newEventFragment = new NewEventFragment();
        newEventFragment.setOnCreationListener(this);
        ((MainActivity) requireActivity()).setFragment(newEventFragment);
    }

    void syncUserEvents() {
        if (!isVisible()) {
            return;
        }
        if (isInitialSync) {
            noResultsTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        eventService.getByUserId(session.getUserId())
                .thenAccept(events ->
                        requireActivity().runOnUiThread(() -> {
                            if (isInitialSync) {
                                isInitialSync = false;
                                progressBar.setVisibility(View.GONE);
                            }
                            if (!events.isEmpty()) {
                                noResultsTextView.setVisibility(View.GONE);
                                adapter.setEvents(events);
                            } else if (adapter.getItemCount() == 0) {
                                noResultsTextView.setVisibility(View.VISIBLE);
                            }
                            updateCounter();
                        }))
                .exceptionally(throwable -> {
                    requireActivity().runOnUiThread(() -> {
                                if (isInitialSync) {
                                    isInitialSync = false;
                                    progressBar.setVisibility(View.GONE);
                                }
                                if (adapter.getItemCount() == 0) {
                                    noResultsTextView.setVisibility(View.VISIBLE);
                                }
                                updateCounter();
                            }
                    );
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    private void updateCounter() {
        infoEventsTextView.setText(String.format(getString(R.string.info_item_events), adapter.getItemCount()));
    }

}
