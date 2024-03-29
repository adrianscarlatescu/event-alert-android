package com.as.eventalertandroid.ui.main.notifications.settings;

import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.data.model.SubscriptionEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.DistanceHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.body.SubscriptionBody;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NotificationsSettingsFragment extends Fragment {

    @BindView(R.id.notificationsSettingsToggleSwitch)
    Switch toggle;
    @BindView(R.id.notificationsSettingsRadiusEditText)
    EditText radiusEditText;
    @BindView(R.id.notificationsSettingsCurrentLocationTextView)
    TextView currentLocationTextButton;
    @BindView(R.id.notificationsSettingsNewLocationTextView)
    TextView newLocationTextButton;

    private static final int DEFAULT_RADIUS = 100;

    private Unbinder unbinder;
    private Geocoder geocoder;
    private Subscription subscription;

    private SubscriptionService subscriptionService = RetrofitClient.getRetrofitInstance().create(SubscriptionService.class);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        String currentAddress;
        if (subscription != null) {
            toggle.setChecked(true);
            radiusEditText.setText(String.valueOf(subscription.radius));
            currentLocationTextButton.setVisibility(View.VISIBLE);
            currentAddress = DistanceHandler.getAddress(geocoder, subscription.latitude, subscription.longitude);
            currentLocationTextButton.setText(String.format(getString(R.string.notifications_settings_current_location), currentAddress));
        } else {
            toggle.setChecked(false);
            currentAddress = null;
            radiusEditText.setText(String.valueOf(DEFAULT_RADIUS));
            currentLocationTextButton.setVisibility(View.GONE);
        }

        if (Session.getInstance().isLocationSet()) {
            String newAddress = DistanceHandler.getAddress(geocoder, Session.getInstance().getLatitude(), Session.getInstance().getLongitude());
            if (currentAddress != null && currentAddress.equals(newAddress)) {
                newLocationTextButton.setVisibility(View.GONE);
            } else {
                newLocationTextButton.setVisibility(View.VISIBLE);
                newLocationTextButton.setText(String.format(getString(R.string.notifications_settings_new_location), newAddress));
            }
        } else {
            newLocationTextButton.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.notificationsSettingsValidateButton)
    void onValidateClicked() {
        if (!toggle.isChecked()) {
            unsubscribe();
            return;
        }

        if (!Session.getInstance().isLocationSet()) {
            Toast.makeText(requireContext(), getString(R.string.message_location_not_set), Toast.LENGTH_SHORT).show();
            return;
        }

        int radiusValue = Integer.valueOf(radiusEditText.getText().toString());
        if (radiusValue <= Constants.MIN_RADIUS) {
            String message = String.format(getString(R.string.message_minimum_radius), Constants.MIN_RADIUS);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (radiusValue > Constants.MAX_RADIUS) {
            String message = String.format(getString(R.string.message_maximum_radius), Constants.MAX_RADIUS);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (toggle.isChecked() && subscription == null) {
            subscribe();
        } else if (toggle.isChecked()) {
            updateSubscription();
        }
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    private void subscribe() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), getString(R.string.message_firebase_token), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String token = task.getResult();

                    SubscriptionBody body = new SubscriptionBody();
                    body.latitude = Session.getInstance().getLatitude();
                    body.longitude = Session.getInstance().getLongitude();
                    body.radius = Integer.valueOf(radiusEditText.getText().toString());
                    body.deviceToken = token;

                    subscriptionService.subscribe(body)
                            .thenAccept(subscription -> {
                                SubscriptionEntity s = new SubscriptionEntity();
                                s.setUserId(Session.getInstance().getUser().id);
                                s.setDeviceToken(subscription.deviceToken);

                                LocalDatabase localDatabase = LocalDatabase.getInstance(requireContext());
                                localDatabase.subscriptionDao().insert(s);

                                progressDialog.dismiss();
                                requireActivity().runOnUiThread(() -> requireActivity().onBackPressed());
                            })
                            .exceptionally(throwable -> {
                                progressDialog.dismiss();
                                ErrorHandler.showMessage(requireActivity(), throwable);
                                return null;
                            });
                });
    }

    private void updateSubscription() {
        SubscriptionBody body = new SubscriptionBody();
        body.latitude = Session.getInstance().getLatitude();
        body.longitude = Session.getInstance().getLongitude();
        body.radius = Integer.valueOf(radiusEditText.getText().toString());
        body.deviceToken = subscription.deviceToken;

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        subscriptionService.update(body)
                .thenAccept(subscription -> {
                    progressDialog.dismiss();
                    requireActivity().runOnUiThread(() -> requireActivity().onBackPressed());
                })
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    private void unsubscribe() {
        if (subscription == null || subscription.deviceToken == null) {
            requireActivity().onBackPressed();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        subscriptionService.unsubscribe(subscription.deviceToken)
                .thenAccept(aVoid -> {
                    LocalDatabase localDatabase = LocalDatabase.getInstance(requireContext());
                    localDatabase.subscriptionDao().deleteByUserId(Session.getInstance().getUser().id);

                    progressDialog.dismiss();
                    requireActivity().runOnUiThread(() -> requireActivity().onBackPressed());
                })
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

}
