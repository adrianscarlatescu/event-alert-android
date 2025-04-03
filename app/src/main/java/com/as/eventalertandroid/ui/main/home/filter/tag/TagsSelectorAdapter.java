package com.as.eventalertandroid.ui.main.home.filter.tag;

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
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TagsSelectorAdapter extends RecyclerView.Adapter<TagsSelectorAdapter.TagViewHolder> {

    private List<TypeDTO> types;
    private Set<TypeDTO> selectedTypes;
    private ClickListener clickListener;

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_selector, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TypeDTO type = types.get(position);
        holder.name.setText(type.name);
        holder.checkBox.setChecked(selectedTypes.contains(type));
        ImageHandler.loadImage(holder.thumbnail, type.imagePath,
                holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        holder.itemView.setOnClickListener(v -> onItemClicked(holder, type));
    }

    @Override
    public int getItemCount() {
        return types == null ? 0 : types.size();
    }

    public List<TypeDTO> getTags() {
        return types;
    }

    public void setTags(List<TypeDTO> types) {
        this.types = types;
    }

    public Set<TypeDTO> getSelectedTags() {
        return selectedTypes;
    }

    public void setSelectedTags(Set<TypeDTO> selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public boolean isAllChecked() {
        return types.size() == selectedTypes.size();
    }

    public interface ClickListener {
        void onItemClicked(TagsSelectorAdapter source);
    }

    private void onItemClicked(TagViewHolder holder, TypeDTO type) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (isChecked) {
            selectedTypes.remove(type);
        } else {
            selectedTypes.add(type);
        }
        clickListener.onItemClicked(this);
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemTagSelectorImageView)
        ImageView thumbnail;
        @BindView(R.id.itemTagSelectorTextView)
        TextView name;
        @BindView(R.id.itemTagSelectorCheckBox)
        CheckBox checkBox;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
