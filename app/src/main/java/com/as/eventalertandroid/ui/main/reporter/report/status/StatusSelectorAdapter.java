package com.as.eventalertandroid.ui.main.reporter.report.status;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.StatusDTO;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusSelectorAdapter extends RecyclerView.Adapter<StatusSelectorAdapter.StatusViewHolder> {

    private List<StatusDTO> statuses;
    private StatusDTO selectedStatus;
    private StatusViewHolder selectedStatusViewHolder;
    private ClickListener clickListener;

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_selector, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        StatusDTO status = statuses.get(position);

        holder.label.setText(status.label);
        holder.color.setCardBackgroundColor(Color.parseColor(status.color));
        if (status.equals(selectedStatus)) {
            holder.checkBox.setChecked(true);
            selectedStatusViewHolder = holder;
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.itemView.setOnClickListener(v -> onItemClicked(holder, status));
    }

    @Override
    public int getItemCount() {
        return statuses == null ? 0 : statuses.size();
    }

    public List<StatusDTO> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<StatusDTO> statuses) {
        this.statuses = statuses;
    }

    public StatusDTO getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(StatusDTO selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(StatusSelectorAdapter source);
    }

    private void onItemClicked(StatusViewHolder holder, StatusDTO status) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (selectedStatusViewHolder != null && selectedStatusViewHolder != holder) {
            selectedStatusViewHolder.checkBox.setChecked(false);
        }
        if (isChecked) {
            selectedStatus = null;
        } else {
            selectedStatus = status;
            selectedStatusViewHolder = holder;
        }
        clickListener.onItemClicked(this);
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemStatusSelectorColorCardView)
        CardView color;
        @BindView(R.id.itemStatusSelectorTextView)
        TextView label;
        @BindView(R.id.itemStatusSelectorCheckBox)
        CheckBox checkBox;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
