package com.as.eventalertandroid.ui.common.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ColorHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.Event;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SeeOnMapFragment extends Fragment implements OnMapReadyCallback {

    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_see_on_map, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.seeOnMapContent, mapFragment)
                .commit();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style);
        googleMap.setMapStyle(styleOptions);

        // Add marker
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
        googleMap.addMarker(options);

        // Zoom on location
        LatLng ll = new LatLng(event.latitude, event.longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
        googleMap.animateCamera(update);
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}
