package com.as.eventalertandroid.ui.common.filter.severity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.SeverityDTO;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SeveritiesSelectorAdapter extends RecyclerView.Adapter<SeveritiesSelectorAdapter.SeverityViewHolder> {

    private List<SeverityDTO> severities;
    private List<SeverityDTO> selectedSeverities;
    private ClickListener clickListener;

    @NonNull
    @Override
    public SeverityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_severity_selector, parent, false);
        return new SeverityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeverityViewHolder holder, int position) {
        SeverityDTO severity = severities.get(position);
        holder.label.setText(severity.label);
        if (selectedSeverities != null) {
            holder.checkBox.setChecked(selectedSeverities.contains(severity));
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.color.setCardBackgroundColor(Color.parseColor(severity.color));
        holder.itemView.setOnClickListener(v -> onItemClicked(holder, severity));
    }

    @Override
    public int getItemCount() {
        return severities == null ? 0 : severities.size();
    }

    public List<SeverityDTO> getSeverities() {
        return severities;
    }

    public void setSeverities(List<SeverityDTO> severities) {
        this.severities = severities;
    }

    public List<SeverityDTO> getSelectedSeverities() {
        return selectedSeverities;
    }

    public void setSelectedSeverities(List<SeverityDTO> selectedSeverities) {
        if (selectedSeverities == null) {
            this.selectedSeverities = new ArrayList<>();
        } else {
            this.selectedSeverities = selectedSeverities;
        }
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public boolean isAllChecked() {
        return severities.size() == selectedSeverities.size();
    }

    public interface ClickListener {
        void onItemClicked(SeveritiesSelectorAdapter source);
    }

    private void onItemClicked(SeverityViewHolder holder, SeverityDTO severity) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (isChecked) {
            selectedSeverities.remove(severity);
        } else {
            selectedSeverities.add(severity);
        }
        clickListener.onItemClicked(this);
    }

    static class SeverityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemSeveritySelectorColorCardView)
        CardView color;
        @BindView(R.id.itemSeveritySelectorTextView)
        TextView label;
        @BindView(R.id.itemSeveritySelectorCheckBox)
        CheckBox checkBox;

        public SeverityViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
