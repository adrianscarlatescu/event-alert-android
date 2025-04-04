package com.as.eventalertandroid.ui.main.notifications;

import android.content.Context;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.data.model.EventNotificationEntity;
import com.as.eventalertandroid.handler.ColorHandler;
import com.as.eventalertandroid.handler.DistanceHandler;
import com.as.eventalertandroid.handler.ImageHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.EventNotificationViewHolder> {

    private List<EventNotificationEntity> eventsNotifications;
    private ClickListener clickListener;
    private final Geocoder geocoder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);

    public NotificationsAdapter(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    @NonNull
    @Override
    public EventNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_notification, parent, false);
        return new EventNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventNotificationViewHolder holder, int position) {
        EventNotificationEntity eventNotificationEntity = eventsNotifications.get(position);

        holder.tagTextView.setText(eventNotificationEntity.getEventTagName());
        ImageHandler.loadImage(holder.tagImageView, eventNotificationEntity.getEventTagImagePath(),
                holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));

        holder.severityTextView.setText(eventNotificationEntity.getEventSeverityName());
        holder.severityCardView.setCardBackgroundColor(ColorHandler.getColorFromHex(eventNotificationEntity.getEventSeverityColor(), 0.8f));

        LocalDateTime dateTime = LocalDateTime.parse(eventNotificationEntity.getEventDateTime());
        holder.dateTimeTextView.setText(dateTime.format(formatter));

        String address = DistanceHandler.getAddress(geocoder, eventNotificationEntity.getEventLatitude(), eventNotificationEntity.getEventLongitude());
        holder.addressTextView.setText(address);

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
        @BindView(R.id.itemEventNotificationTypeImageView)
        ImageView tagImageView;
        @BindView(R.id.itemEventNotificationTypeTextView)
        TextView tagTextView;
        @BindView(R.id.itemEventNotificationSeverityCardView)
        CardView severityCardView;
        @BindView(R.id.itemEventNotificationSeverityTextView)
        TextView severityTextView;
        @BindView(R.id.itemEventNotificationDateTimeTextView)
        TextView dateTimeTextView;
        @BindView(R.id.itemEventNotificationAddressTextView)
        TextView addressTextView;

        public EventNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
