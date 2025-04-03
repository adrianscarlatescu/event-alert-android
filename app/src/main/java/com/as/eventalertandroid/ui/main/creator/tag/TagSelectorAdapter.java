package com.as.eventalertandroid.ui.main.creator.tag;

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

public class TagSelectorAdapter extends RecyclerView.Adapter<TagSelectorAdapter.TagViewHolder> {

    private List<TypeDTO> types;
    private TypeDTO selectedType;
    private TagViewHolder selectedTagViewHolder;
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
        ImageHandler.loadImage(holder.thumbnail, type.imagePath,
                holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        if (type.equals(selectedType)) {
            holder.checkBox.setChecked(true);
            selectedTagViewHolder = holder;
        } else {
            holder.checkBox.setChecked(false);
        }

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

    public TypeDTO getSelectedTag() {
        return selectedType;
    }

    public void setSelectedTag(TypeDTO selectedType) {
        this.selectedType = selectedType;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(TagSelectorAdapter source);
    }

    private void onItemClicked(TagViewHolder holder, TypeDTO type) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        holder.checkBox.setChecked(!isChecked);
        if (selectedTagViewHolder != null && selectedTagViewHolder != holder) {
            selectedTagViewHolder.checkBox.setChecked(false);
        }
        if (isChecked) {
            selectedType = null;
        } else {
            selectedType = type;
            selectedTagViewHolder = holder;
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

