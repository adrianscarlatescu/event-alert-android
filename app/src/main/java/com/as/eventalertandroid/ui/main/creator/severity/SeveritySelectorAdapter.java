package com.as.eventalertandroid.ui.main.creator.severity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ColorHandler;
import com.as.eventalertandroid.net.model.EventSeverity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SeveritySelectorAdapter extends RecyclerView.Adapter<SeveritySelectorAdapter.SeverityViewHolder> {

    private List<EventSeverity> severities;
    private EventSeverity selectedSeverity;
    private SeverityViewHolder selectedSeverityViewHolder;
    private ClickListener clickListener;

    @NonNull
    @Override
    public SeverityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_severity_selector, parent, false);
        return new SeverityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeverityViewHolder holder, int position) {
        EventSeverity severity = severities.get(position);

        holder.name.setText(severity.name);
        holder.color.setCardBackgroundColor(ColorHandler.getColorFromHex(severity.color));
        if (severity.equals(selectedSeverity)) {
            holder.checkBox.setChecked(true);
            selectedSeverityViewHolder = holder;
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.itemView.setOnClickListener(v -> onItemClicked(holder, severity));
    }

    @Override
    public int getItemCount() {
        return severities == null ? 0 : severities.size();
    }

    public List<EventSeverity> getSeverities() {
        return severities;
    }

    public void setSeverities(List<EventSeverity> severities) {
        this.severities = severities;
    }

    public EventSeverity getSelectedSeverity() {
        return selectedSeverity;
    }

    public void setSelectedSeverity(EventSeverity selectedSeverity) {
        this.selectedSeverity = selectedSeverity;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(SeveritySelectorAdapter source);
    }

    private void onItemClicked(SeverityViewHolder holder, EventSeverity severity) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (selectedSeverityViewHolder != null && selectedSeverityViewHolder != holder) {
            selectedSeverityViewHolder.checkBox.setChecked(false);
        }
        if (isChecked) {
            selectedSeverity = null;
        } else {
            selectedSeverity = severity;
            selectedSeverityViewHolder = holder;
        }
        clickListener.onItemClicked(this);
    }

    class SeverityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemSeveritySelectorColorCardView)
        CardView color;
        @BindView(R.id.itemSeveritySelectorTextView)
        TextView name;
        @BindView(R.id.itemSeveritySelectorCheckBox)
        CheckBox checkBox;

        public SeverityViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
