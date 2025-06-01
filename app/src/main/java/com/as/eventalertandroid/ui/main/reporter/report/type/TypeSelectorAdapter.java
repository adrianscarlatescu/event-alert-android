package com.as.eventalertandroid.ui.main.reporter.report.type;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.TypeDTO;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeSelectorAdapter extends RecyclerView.Adapter<TypeSelectorAdapter.TypeViewHolder> {

    private List<TypeDTO> types;
    private TypeDTO selectedType;
    private TypeViewHolder selectedTypeViewHolder;
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
        ImageHandler.loadImage(holder.thumbnail, type.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        if (type.equals(selectedType)) {
            holder.checkBox.setChecked(true);
            selectedTypeViewHolder = holder;
        } else {
            holder.checkBox.setChecked(false);
        }

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

    public TypeDTO getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(TypeDTO selectedType) {
        this.selectedType = selectedType;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(TypeSelectorAdapter source);
    }

    private void onItemClicked(TypeViewHolder holder, TypeDTO type) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        holder.checkBox.setChecked(!isChecked);
        if (selectedTypeViewHolder != null && selectedTypeViewHolder != holder) {
            selectedTypeViewHolder.checkBox.setChecked(false);
        }
        if (isChecked) {
            selectedType = null;
        } else {
            selectedType = type;
            selectedTypeViewHolder = holder;
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

