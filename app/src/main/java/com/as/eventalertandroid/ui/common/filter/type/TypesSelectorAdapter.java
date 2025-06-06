package com.as.eventalertandroid.ui.common.filter.type;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.TypeDTO;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TypesSelectorAdapter extends RecyclerView.Adapter<TypesSelectorAdapter.TypeViewHolder> {

    private List<TypeDTO> types;
    private List<TypeDTO> selectedTypes;
    private ClickListener clickListener;

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_selector, parent, false);
        return new TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        TypeDTO type = types.get(position);
        holder.typeLabel.setText(type.label);
        holder.categoryLabel.setText(type.category.label);
        if (selectedTypes != null) {
            holder.checkBox.setChecked(selectedTypes.contains(type));
        } else {
            holder.checkBox.setChecked(false);
        }
        ImageHandler.loadImage(holder.thumbnail, type.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        holder.itemView.setOnClickListener(v -> onItemClicked(holder, type));
    }

    @Override
    public int getItemCount() {
        return types == null ? 0 : types.size();
    }

    public List<TypeDTO> getTypes() {
        return types;
    }

    public void setTypes(List<TypeDTO> types) {
        this.types = types;
    }

    public List<TypeDTO> getSelectedTypes() {
        return selectedTypes;
    }

    public void setSelectedTypes(List<TypeDTO> selectedTypes) {
        if (selectedTypes == null) {
            this.selectedTypes = new ArrayList<>();
        } else {
            this.selectedTypes = selectedTypes;
        }
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public boolean isAllChecked() {
        return selectedTypes != null && types.size() == selectedTypes.size();
    }

    public interface ClickListener {
        void onItemClicked(TypesSelectorAdapter source);
    }

    private void onItemClicked(TypeViewHolder holder, TypeDTO type) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (isChecked) {
            selectedTypes.remove(type);
        } else {
            selectedTypes.add(type);
        }
        clickListener.onItemClicked(this);
    }

    static class TypeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemTypeSelectorImageView)
        ImageView thumbnail;
        @BindView(R.id.itemTypeSelectorTypeTextView)
        TextView typeLabel;

        @BindView(R.id.itemTypeSelectorCategoryTextView)
        TextView categoryLabel;
        @BindView(R.id.itemTypeSelectorCheckBox)
        CheckBox checkBox;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
