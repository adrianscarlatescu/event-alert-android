package com.as.eventalertandroid.ui.main.home.map;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.handler.LocationHandler;
import com.as.eventalertandroid.net.model.EventDTO;
import com.as.eventalertandroid.ui.auth.AuthActivity;
import com.as.eventalertandroid.ui.common.event.EventDetailsFragment;
import com.as.eventalertandroid.ui.main.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Callback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeMapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_REQUEST = 1;
    private static final float DEFAULT_ZOOM = 14;
    private static final int LOCATION_REFRESH_INTERVAL = 30_000;

    private Unbinder unbinder;

    private SupportMapFragment mapFragment;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;
    private Location location;
    private Marker userMarker;
    private List<Marker> eventsMarkers;
    private Circle circleAroundUser;
    private Circle circleAroundEvent;
    private List<EventDTO> events;
    private final Session session = Session.getInstance();

    private boolean isStartLocationSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_map, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (isGoogleServiceAvailable()) {
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                mapFragment.getMapAsync(this);
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.homeMapContent, mapFragment)
                    .commit();
        } else {
            Toast.makeText(requireContext(), R.string.message_google_service_not_available, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), AuthActivity.class));
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Custom map style
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style);
        this.googleMap.setMapStyle(styleOptions);

        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setOnMapClickListener(this);

        new GoogleApiClient.Builder(requireContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
                .connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_REFRESH_INTERVAL);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
        } else {
            locationActivateRequest(locationRequest);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(userMarker)) {
            return true;
        }

        int index = eventsMarkers.indexOf(marker);
        if (index < 0) {
            return true;
        }
        EventDTO event = events.get(index);

        int severityColor = Color.parseColor(event.severity.color);

        if (circleAroundEvent != null) {
            circleAroundEvent.remove();
        }
        if (event.impactRadius != null && !event.impactRadius.equals(BigDecimal.ZERO)) {
            LatLng eventCircleCenter = new LatLng(event.latitude, event.longitude);
            double eventCircleRadius = event.impactRadius.multiply(BigDecimal.valueOf(1000)).doubleValue();
            int eventCircleColor = ColorUtils.setAlphaComponent(severityColor, 128); // Half transparent

            circleAroundEvent = drawCircle(eventCircleCenter, eventCircleRadius, eventCircleColor);
        }

        String distance = LocationHandler.getDistance(requireContext(), event.distance);

        View markerWindow = getLayoutInflater().inflate(R.layout.layout_event_marker_info, (ViewGroup) getView(), false);
        ImageView imageView = markerWindow.findViewById(R.id.markerInfoEventImageView);
        TextView typeTextView = markerWindow.findViewById(R.id.markerInfoEventTypeTextView);
        CardView severityColorCardView = markerWindow.findViewById(R.id.markerInfoEventSeverityColorCardView);
        TextView severityTextView = markerWindow.findViewById(R.id.markerInfoEventSeverityTextView);
        CardView statusColorCardView = markerWindow.findViewById(R.id.markerInfoEventStatusColorCardView);
        TextView statusTextView = markerWindow.findViewById(R.id.markerInfoEventStatusTextView);
        TextView createdAtTextView = markerWindow.findViewById(R.id.markerInfoEventCreatedAtTextView);
        TextView impactRadiusTextView = markerWindow.findViewById(R.id.markerInfoEventImpactRadiusTextView);
        TextView distanceTextView = markerWindow.findViewById(R.id.markerInfoEventDistanceTextView);

        if (event.impactRadius != null) {
            impactRadiusTextView.setText(String.format(getString(R.string.impact_radius_km), event.impactRadius.stripTrailingZeros().toPlainString()));
        } else {
            impactRadiusTextView.setVisibility(View.GONE);
        }

        severityColorCardView.setCardBackgroundColor(severityColor);
        statusColorCardView.setCardBackgroundColor(Color.parseColor(event.status.color));
        typeTextView.setText(event.type.label);
        severityTextView.setText(event.severity.label);
        statusTextView.setText(event.status.label);
        createdAtTextView.setText(event.createdAt.format(Constants.defaultDateTimeFormatter));
        distanceTextView.setText(distance);

        ImageHandler.loadImage(imageView, event.imagePath, new Callback() {
            @Override
            public void onSuccess() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(marker::showInfoWindow, 200);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return markerWindow;
            }
        });

        float zoom = googleMap.getCameraPosition().zoom;
        goToLocationZoom(event.latitude + 180 / Math.pow(2, zoom), event.longitude, zoom);
        marker.showInfoWindow();

        googleMap.setOnInfoWindowClickListener(m -> {
            EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
            eventDetailsFragment.setEvent(event);
            ((MainActivity) requireActivity()).setFragment(eventDetailsFragment);
        });

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (circleAroundEvent != null) {
            circleAroundEvent.remove();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationActivateRequest(locationRequest);
            } else {
                Toast.makeText(requireContext(), R.string.message_permission_location, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locate();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    public void updateView(List<EventDTO> events) {
        this.events = events;

        if (circleAroundUser != null) {
            circleAroundUser.remove();
        }
        if (circleAroundEvent != null) {
            circleAroundEvent.remove();
        }

        if (eventsMarkers != null && !eventsMarkers.isEmpty()) {
            eventsMarkers.forEach(Marker::remove);
            eventsMarkers.clear();
        }

        if (this.events.size() == 0) {
            goToLocationZoom(location.getLatitude(), location.getLongitude(), DEFAULT_ZOOM - 2);
            return;
        }

        eventsMarkers = new ArrayList<>(this.events.size());

        double originalMaxDistance = 0;
        for (EventDTO event : this.events) {
            addEventMarker(event);
            if (event.distance > originalMaxDistance) {
                originalMaxDistance = event.distance;
            }
        }
        double maxDistance = originalMaxDistance + originalMaxDistance * 0.05; // 5% error margin

        goToLocationZoom(location.getLatitude(), location.getLongitude(), (float) (14 - Math.log(maxDistance) / Math.log(2)));

        if (maxDistance <= 1000) {
            LatLng userCircleCenter = new LatLng(location.getLatitude(), location.getLongitude());
            int userCircleColor = requireContext().getColor(R.color.colorMapUserCircleFill);

            circleAroundUser = drawCircle(userCircleCenter, maxDistance * 1000, userCircleColor);
        }
    }

    public void focusOnUserLocation() {
        if (location == null) {
            return;
        }
        goToLocationZoom(location.getLatitude(), location.getLongitude(), DEFAULT_ZOOM + 1);
    }

    private void locationActivateRequest(LocationRequest mLocationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(requireContext()).checkLocationSettings(builder.build());
        task.addOnSuccessListener(command -> {
            // All location settings are satisfied, the client can initialize location requests here
            locate();
        });
        task.addOnFailureListener(command -> {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) command;
                startIntentSenderForResult(resolvable.getResolution().getIntentSender(),
                        LOCATION_REQUEST, null, 0, 0, 0, null);
            } catch (IntentSender.SendIntentException e) {
                // Ignore
            }
        });
    }

    private void locate() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(requireActivity());
        client.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (!isAdded()) {
                    return;
                }
                location = locationResult.getLastLocation();
                if (!isStartLocationSet) {
                    isStartLocationSet = true;
                    goToLocationZoom(location.getLatitude(), location.getLongitude(), DEFAULT_ZOOM);
                }
                if (userMarker != null) {
                    userMarker.remove();
                }
                addUserMarker(location.getLatitude(), location.getLongitude());

                session.setUserLatitude(location.getLatitude());
                session.setUserLongitude(location.getLongitude());
            }
        }, Looper.getMainLooper());
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        googleMap.animateCamera(update);
    }

    private void addUserMarker(double lat, double lng) {
        Bitmap bmp = Bitmap.createBitmap(130, 210, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint color = new Paint();
        color.setColor(Color.WHITE);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;

        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_user_location, opt);
        Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 130, 210, true);
        canvas.drawBitmap(resized, 0, 0, color);

        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .position(new LatLng(lat, lng));
        userMarker = googleMap.addMarker(options);
        userMarker.setTitle("userMarker");
    }

    private void addEventMarker(EventDTO event) {
        IconGenerator iconFactory = new IconGenerator(requireContext());
        iconFactory.setColor(Color.parseColor(event.severity.color));

        ImageView markerView = new ImageView(requireContext());
        ImageHandler.loadImage(markerView, event.type.imagePath);
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = 200;
        layoutParams.height = 200;
        markerView.setLayoutParams(layoutParams);
        iconFactory.setContentView(markerView);

        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(event.latitude, event.longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()))
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        eventsMarkers.add(googleMap.addMarker(options));
    }

    private Circle drawCircle(LatLng centerCoordinates, double radius, @ColorInt int color) {
        CircleOptions options = new CircleOptions()
                .center(centerCoordinates)
                .radius(radius)
                .fillColor(color)
                .strokeWidth(10)
                .strokeColor(requireContext().getColor(R.color.colorMapCircleStroke));
        return googleMap.addCircle(options);
    }

    private boolean isGoogleServiceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getContext());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, 0);
            dialog.show();
        }
        return false;
    }

}
