package com.as.eventalertandroid.ui.common.event.map;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.EventDTO;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private EventDTO event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_map, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.eventMapContent, mapFragment)
                .commit();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style);
        googleMap.setMapStyle(styleOptions);

        // Add marker
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

        LatLng eventCoordinates = new LatLng(event.latitude, event.longitude);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(eventCoordinates)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()))
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        googleMap.addMarker(markerOptions);

        // Add circle
        if (event.impactRadius != null && !event.impactRadius.equals(BigDecimal.ZERO)) {
            double radius = event.impactRadius.multiply(BigDecimal.valueOf(1000)).doubleValue();
            int severityColor = Color.parseColor(event.severity.color);
            int color = ColorUtils.setAlphaComponent(severityColor, 128); // Half transparent

            CircleOptions circleOptions = new CircleOptions()
                    .center(eventCoordinates)
                    .radius(radius)
                    .fillColor(color)
                    .strokeWidth(10)
                    .strokeColor(requireContext().getColor(R.color.colorMapCircleStroke));
            googleMap.addCircle(circleOptions);
        }

        // Zoom on location
        LatLng ll = new LatLng(event.latitude, event.longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
        googleMap.animateCamera(update);
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

}
