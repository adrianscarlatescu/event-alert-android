package com.as.eventalertandroid.ui.common.event.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.handler.LocationHandler;
import com.as.eventalertandroid.net.model.EventDTO;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<EventDTO> events;
    private ClickListener clickListener;
    private final boolean showDistance;
    private final boolean showImage;

    public EventsAdapter(boolean showDistance, boolean showImage) {
        this.showDistance = showDistance;
        this.showImage = showImage;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view, showImage, showDistance);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventDTO event = events.get(position);

        if (showImage) {
            ImageHandler.loadImage(holder.imageView, event.imagePath,
                    holder.itemView.getContext().getDrawable(R.color.colorPlaceholder));
        }
        if (showDistance) {
            String location = LocationHandler.getDistance(holder.itemView.getContext(), event.distance);
            holder.distanceTextView.setText(location);
        }

        if (event.impactRadius != null) {
            holder.impactRadiusTextView.setText(String.format(holder.itemView.getContext().getString(R.string.impact_radius_km), event.impactRadius.stripTrailingZeros().toPlainString()));
        } else {
            holder.impactRadiusTextView.setVisibility(View.GONE);
        }

        ImageHandler.loadImage(holder.typeImageView, event.type.imagePath,
                holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        holder.severityCardView.setCardBackgroundColor(Color.parseColor(event.severity.color));
        holder.typeTextView.setText(event.type.label);
        holder.severityTextView.setText(event.severity.label);
        holder.statusTextView.setText(event.status.label);
        holder.createdAtTextView.setText(event.createdAt.format(Constants.defaultDateTimeFormatter));

        holder.itemView.setOnClickListener(v -> clickListener.onItemClicked(this, event));
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    public void setEvents(List<EventDTO> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public void addEvents(List<EventDTO> events) {
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    public void addEvent(EventDTO event) {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        this.events.add(0, event);
        notifyItemInserted(0);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(EventsAdapter source, EventDTO event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemEventImageView)
        ImageView imageView;
        @BindView(R.id.itemEventSeverityCardView)
        CardView severityCardView;
        @BindView(R.id.itemEventTypeImageView)
        ImageView typeImageView;
        @BindView(R.id.itemEventTypeTextView)
        TextView typeTextView;
        @BindView(R.id.itemEventSeverityTextView)
        TextView severityTextView;
        @BindView(R.id.itemEventStatusTextView)
        TextView statusTextView;
        @BindView(R.id.itemEventCreatedAtTextView)
        TextView createdAtTextView;
        @BindView(R.id.itemEventImpactRadiusTextView)
        TextView impactRadiusTextView;
        @BindView(R.id.itemEventDistanceTextView)
        TextView distanceTextView;

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
