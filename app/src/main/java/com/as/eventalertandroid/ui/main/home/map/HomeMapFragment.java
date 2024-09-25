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
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ColorHandler;
import com.as.eventalertandroid.handler.DistanceHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.Session;
import com.as.eventalertandroid.net.model.Event;
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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeMapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_REQUEST = 1;
    private static final float DEFAULT_ZOOM = 14;

    private Unbinder unbinder;

    private SupportMapFragment mapFragment;

    private GoogleMap googleMap;
    private LocationRequest locationRequest;
    private Location location;
    private Marker userMarker;
    private List<Marker> eventsMarkers;
    private Circle areaCircle;
    private List<Event> events;
    private Session session = Session.getInstance();

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
            Toast.makeText(requireContext(), getString(R.string.message_google_service_not_available), Toast.LENGTH_SHORT).show();
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
        locationRequest.setInterval(30_000);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        } else {
            // Permission has been granted, continue
            locationActivateRequest(locationRequest);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO
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
        Event event = events.get(index);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
        String distance = DistanceHandler.getDistance(requireContext(), event.distance);
        String address = DistanceHandler.getAddress(new Geocoder(requireContext(), Locale.getDefault()), event.latitude, event.longitude);

        View markerWindow = getLayoutInflater().inflate(R.layout.layout_event_marker_info, (ViewGroup) getView(), false);
        ImageView imageView = markerWindow.findViewById(R.id.markerInfoEventImageView);
        TextView tagTextView = markerWindow.findViewById(R.id.markerInfoEventTagTextView);
        TextView severityTextView = markerWindow.findViewById(R.id.markerInfoEventSeverityTextView);
        TextView dateTimeTextView = markerWindow.findViewById(R.id.markerInfoEventDateTimeTextView);
        TextView distanceTextView = markerWindow.findViewById(R.id.markerInfoEventDistanceTextView);
        TextView addressTextView = markerWindow.findViewById(R.id.markerInfoEventAddressTextView);

        tagTextView.setText(event.tag.name);
        severityTextView.setText(event.severity.name);
        dateTimeTextView.setText(event.dateTime.format(dateTimeFormatter));
        distanceTextView.setText(distance);
        addressTextView.setText(address);
        ImageHandler.loadImage(imageView, event.imagePath, new Callback() {
            @Override
            public void onSuccess() {
                session.getHandler().postDelayed(marker::showInfoWindow, 200);
            }

            @Override
            public void onError(Exception e) {
                // Nothing to do
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationActivateRequest(locationRequest);
            } else {
                // TODO
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
                        // Nothing to do
                        break;
                }
                break;
        }
    }

    public void updateView(List<Event> events) {
        this.events = events;

        if (areaCircle != null) {
            areaCircle.remove();
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

        double distance = 0;
        for (Event event : this.events) {
            addEventMarker(event);
            if (event.distance > distance) {
                distance = event.distance;
            }
        }

        goToLocationZoom(location.getLatitude(), location.getLongitude(),
                (float) (14 - Math.log(distance) / Math.log(2)));

        int maxRadius = Constants.MAX_RADIUS / 20;
        if (distance < maxRadius) {
            areaCircle = drawCircle(new LatLng(location.getLatitude(), location.getLongitude()), distance * 1000);
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
                // Ignore the error
            }
        });
    }

    private void locate() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void addEventMarker(Event event) {
        IconGenerator iconFactory = new IconGenerator(requireContext());
        int color = ColorHandler.getColorFromHex(event.severity.color, 0.8f);
        iconFactory.setColor(color);

        ImageView markerView = new ImageView(requireContext());
        ImageHandler.loadImage(markerView, event.tag.imagePath);
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

    private Circle drawCircle(LatLng latlng, double marginDistance) {
        CircleOptions options = new CircleOptions()
                .center(latlng)
                .radius(marginDistance)
                .fillColor(requireContext().getColor(R.color.colorMapCircleFill))
                .strokeWidth(25)
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
