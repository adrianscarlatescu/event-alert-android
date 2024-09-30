package com.as.eventalertandroid.ui.main.notifications.settings;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.DeviceHandler;
import com.as.eventalertandroid.handler.DistanceHandler;
import com.as.eventalertandroid.handler.ErrorHandler;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.as.eventalertandroid.net.model.Subscription;
import com.as.eventalertandroid.net.model.request.SubscriptionRequest;
import com.as.eventalertandroid.net.service.SubscriptionService;
import com.as.eventalertandroid.ui.common.ProgressDialog;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
    TextView currentLocationTextView;
    @BindView(R.id.notificationsSettingsNewLocationTextView)
    TextView newLocationTextView;

    private static final int POST_NOTIFICATIONS_REQUEST = 0;

    private Unbinder unbinder;
    private Geocoder geocoder;
    private Subscription subscription;
    private final SubscriptionService subscriptionService = RetrofitClient.getInstance().create(SubscriptionService.class);
    private final Session session = Session.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        subscription = session.getSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        String currentAddress;
        if (subscription != null) {
            currentAddress = DistanceHandler.getAddress(geocoder, subscription.latitude, subscription.longitude);

            toggle.setChecked(true);
            radiusEditText.setText(String.valueOf(subscription.radius));
            radiusEditText.setEnabled(true);
            currentLocationTextView.setVisibility(View.VISIBLE);
            currentLocationTextView.setText(String.format(getString(R.string.notifications_settings_current_location), currentAddress));
        } else {
            currentAddress = null;

            toggle.setChecked(false);
            radiusEditText.setText("");
            radiusEditText.setEnabled(false);
            currentLocationTextView.setVisibility(View.GONE);
        }

        if (session.isUserLocationSet()) {
            String newAddress = DistanceHandler.getAddress(geocoder, session.getUserLatitude(), session.getUserLongitude());
            if (currentAddress != null && currentAddress.equals(newAddress)) {
                newLocationTextView.setVisibility(View.GONE);
            } else {
                newLocationTextView.setVisibility(View.VISIBLE);
                newLocationTextView.setText(String.format(getString(R.string.notifications_settings_new_location), newAddress));
            }
        } else {
            newLocationTextView.setVisibility(View.GONE);
        }

        toggle.setOnCheckedChangeListener(((compoundButton, isChecked) -> {
            if (isChecked) {
                radiusEditText.setEnabled(true);
            } else {
                radiusEditText.setText("");
                radiusEditText.setEnabled(false);
            }
        }));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == POST_NOTIFICATIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeOrUpdate();
            } else {
                Toast.makeText(requireContext(), R.string.message_permission_notifications, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.notificationsSettingsValidateButton)
    void onValidateClicked() {
        if (!toggle.isChecked()) {
            if (subscription == null) {
                requireActivity().onBackPressed();
                return;
            }
            unsubscribe();
            return;
        }

        if (!session.isUserLocationSet()) {
            Toast.makeText(requireContext(), getString(R.string.message_location_not_set), Toast.LENGTH_SHORT).show();
            return;
        }

        String radiusEditTextValue = radiusEditText.getText().toString();
        Integer radiusValue = radiusEditTextValue.length() > 0 ? Integer.parseInt(radiusEditTextValue) : null;
        if (radiusValue == null || radiusValue <= Constants.MIN_RADIUS) {
            String message = String.format(getString(R.string.message_minimum_radius), Constants.MIN_RADIUS);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (radiusValue > Constants.MAX_RADIUS) {
            String message = String.format(getString(R.string.message_maximum_radius), Constants.MAX_RADIUS);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, POST_NOTIFICATIONS_REQUEST);
            return;
        }

        subscribeOrUpdate();
    }

    private void subscribeOrUpdate() {
        if (toggle.isChecked() && subscription == null) {
            subscribe();
        } else if (toggle.isChecked()) {
            updateSubscription();
        }
    }

    private void subscribe() {
        FirebaseMessaging firebaseMessaging;
        try {
            firebaseMessaging = FirebaseMessaging.getInstance();
        } catch (IllegalStateException e) {
            Toast.makeText(requireContext(), R.string.message_firebase_instance, Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        firebaseMessaging.getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), getString(R.string.message_firebase_token), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
                    subscriptionRequest.userId = session.getUserId();
                    subscriptionRequest.latitude = session.getUserLatitude();
                    subscriptionRequest.longitude = session.getUserLongitude();
                    subscriptionRequest.radius = Integer.valueOf(radiusEditText.getText().toString());
                    subscriptionRequest.deviceId = DeviceHandler.getAndroidId(requireContext());
                    subscriptionRequest.firebaseToken = task.getResult();

                    subscriptionService.subscribe(subscriptionRequest)
                            .thenAccept(subscription -> {
                                progressDialog.dismiss();
                                session.setSubscription(subscription);
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), R.string.message_success, Toast.LENGTH_SHORT).show();
                                    requireActivity().onBackPressed();
                                });
                            })
                            .exceptionally(throwable -> {
                                progressDialog.dismiss();
                                ErrorHandler.showMessage(requireActivity(), throwable);
                                return null;
                            });
                });
    }

    private void updateSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.userId = session.getUserId();
        subscriptionRequest.latitude = session.getUserLatitude();
        subscriptionRequest.longitude = session.getUserLongitude();
        subscriptionRequest.radius = Integer.valueOf(radiusEditText.getText().toString());
        subscriptionRequest.deviceId = subscription.deviceId;
        subscriptionRequest.firebaseToken = subscription.firebaseToken;

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        subscriptionService.update(subscriptionRequest)
                .thenAccept(subscription -> {
                    progressDialog.dismiss();
                    session.setSubscription(subscription);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), R.string.message_success, Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    });
                })
                .exceptionally(throwable -> {
                    progressDialog.dismiss();
                    ErrorHandler.showMessage(requireActivity(), throwable);
                    return null;
                });
    }

    private void unsubscribe() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        subscriptionService.unsubscribe(session.getUserId(), subscription.deviceId)
                .thenAccept(aVoid -> {
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
