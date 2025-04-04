package com.as.eventalertandroid.ui.common.filter.status;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.StatusDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusesSelectorAdapter extends RecyclerView.Adapter<StatusesSelectorAdapter.StatusViewHolder> {

    private List<StatusDTO> statuses;
    private Set<StatusDTO> selectedStatuses;
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
        holder.checkBox.setChecked(selectedStatuses.contains(status));
        holder.color.setCardBackgroundColor(Color.parseColor(status.color));
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

    public Set<StatusDTO> getSelectedStatuses() {
        return selectedStatuses;
    }

    public void setSelectedStatuses(Set<StatusDTO> selectedStatuses) {
        this.selectedStatuses = selectedStatuses;
    }

    public void setSelectedStatuses(StatusDTO status) {
        this.selectedStatuses = new HashSet<>(1);
        this.selectedStatuses.add(status);
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public boolean isAllChecked() {
        return statuses.size() == selectedStatuses.size();
    }

    public interface ClickListener {
        void onItemClicked(StatusesSelectorAdapter source);
    }

    private void onItemClicked(StatusViewHolder holder, StatusDTO status) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (isChecked) {
            selectedStatuses.remove(status);
        } else {
            selectedStatuses.add(status);
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
