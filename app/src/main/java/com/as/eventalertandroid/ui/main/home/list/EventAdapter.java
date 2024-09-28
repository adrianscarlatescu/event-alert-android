package com.as.eventalertandroid.ui.main.home.list;

import android.content.Context;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ColorHandler;
import com.as.eventalertandroid.handler.DistanceHandler;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.Event;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private ClickListener clickListener;
    private boolean showDistance;
    private boolean showImage;
    private final Geocoder geocoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);

    public EventAdapter(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
        showDistance = true;
        showImage = true;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view, showImage, showDistance);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        if (showImage) {
            ImageHandler.loadImage(holder.imageView, event.imagePath,
                    holder.itemView.getContext().getDrawable(R.color.colorPlaceholder));
        }
        if (showDistance) {
            String location = DistanceHandler.getDistance(holder.itemView.getContext(), event.distance);
            holder.distanceTextView.setText(location);
        }

        ImageHandler.loadImage(holder.tagImageView, event.tag.imagePath,
                holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        holder.severityCardView.setCardBackgroundColor(ColorHandler.getColorFromHex(event.severity.color, 0.8f));
        holder.tagTextView.setText(event.tag.name);
        holder.severityTextView.setText(event.severity.name);
        holder.dateTimeTextView.setText(event.dateTime.format(formatter));
        holder.addressTextView.setText(DistanceHandler.getAddress(geocoder, event.latitude, event.longitude));

        holder.itemView.setOnClickListener(v -> clickListener.onItemClicked(this, event));
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public void addEvents(List<Event> events) {
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        this.events.add(0, event);
        notifyItemInserted(0);
    }

    public boolean isShowDistance() {
        return showDistance;
    }

    public void setShowDistance(boolean showDistance) {
        this.showDistance = showDistance;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(EventAdapter source, Event event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemEventImageView)
        ImageView imageView;
        @BindView(R.id.itemEventSeverityCardView)
        CardView severityCardView;
        @BindView(R.id.itemEventTagImageView)
        ImageView tagImageView;
        @BindView(R.id.itemEventTagTextView)
        TextView tagTextView;
        @BindView(R.id.itemEventSeverityTextView)
        TextView severityTextView;
        @BindView(R.id.itemEventDateTimeTextView)
        TextView dateTimeTextView;
        @BindView(R.id.itemEventDistanceTextView)
        TextView distanceTextView;
        @BindView(R.id.itemEventAddressTextView)
        TextView addressTextView;

        EventViewHolder(@NonNull View itemView, boolean showImage, boolean showDistance) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (!showImage) {
                imageView.setVisibility(View.GONE);
            }
            if (!showDistance) {
                distanceTextView.setVisibility(View.GONE);
            }
        }

    }

}
