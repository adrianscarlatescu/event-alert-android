package com.as.eventalertandroid.ui.main.notifications;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ImageHandler;

import java.time.LocalDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.EventNotificationViewHolder> {

    private List<EventNotificationEntity> eventsNotifications;
    private ClickListener clickListener;

    @NonNull
    @Override
    public EventNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_notification, parent, false);
        return new EventNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventNotificationViewHolder holder, int position) {
        EventNotificationEntity eventNotificationEntity = eventsNotifications.get(position);
        int severityColor = Color.parseColor(eventNotificationEntity.getSeverityColor());

        holder.thumbnailSeverityCardView.setCardBackgroundColor(severityColor);
        ImageHandler.loadImage(holder.thumbnailTypeImageView, eventNotificationEntity.getTypeImagePath(), holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));

        holder.typeTextView.setText(eventNotificationEntity.getTypeLabel());
        holder.severityColorCardView.setCardBackgroundColor(severityColor);
        holder.severityTextView.setText(eventNotificationEntity.getSeverityLabel());
        holder.statusColorCardView.setCardBackgroundColor(Color.parseColor(eventNotificationEntity.getStatusColor()));
        holder.statusTextView.setText(eventNotificationEntity.getStatusLabel());

        LocalDateTime createdAt = LocalDateTime.parse(eventNotificationEntity.getCreatedAt());
        holder.createdAtTextView.setText(createdAt.format(Constants.defaultDateTimeFormatter));

        if (!eventNotificationEntity.getImpactRadius().isEmpty()) {
            holder.impactRadiusTextView.setText(String.format(holder.itemView.getContext().getString(R.string.impact_radius_km), eventNotificationEntity.getImpactRadius()));
        } else {
            holder.impactRadiusTextView.setVisibility(View.GONE);
        }

        if (!eventNotificationEntity.getViewed()) {
            holder.layout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.colorNotificationNotViewed));
        } else {
            holder.layout.setBackgroundColor(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClicked(this, eventNotificationEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsNotifications == null ? 0 : eventsNotifications.size();
    }

    public void setEventsNotifications(List<EventNotificationEntity> eventsNotifications) {
        this.eventsNotifications = eventsNotifications;
    }

    public void addEventNotification(EventNotificationEntity eventNotification) {
        this.eventsNotifications.add(0, eventNotification);
        notifyItemInserted(0);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(NotificationsAdapter source, EventNotificationEntity eventNotification);
    }

    static class EventNotificationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemEventNotificationLayout)
        LinearLayout layout;
        @BindView(R.id.itemEventNotificationThumbnailSeverityCardView)
        CardView thumbnailSeverityCardView;
        @BindView(R.id.itemEventNotificationThumbnailTypeImageView)
        ImageView thumbnailTypeImageView;
        @BindView(R.id.itemEventNotificationTypeTextView)
        TextView typeTextView;
        @BindView(R.id.itemEventNotificationSeverityColorCardView)
        CardView severityColorCardView;
        @BindView(R.id.itemEventNotificationSeverityTextView)
        TextView severityTextView;
        @BindView(R.id.itemEventNotificationStatusColorCardView)
        CardView statusColorCardView;
        @BindView(R.id.itemEventNotificationStatusTextView)
        TextView statusTextView;
        @BindView(R.id.itemEventNotificationImpactRadiusTextView)
        TextView impactRadiusTextView;
        @BindView(R.id.itemEventNotificationCreatedAtTextView)
        TextView createdAtTextView;

        public EventNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
