package com.as.eventalertandroid.ui.main.home.filter.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.EventTag;

import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TagsSelectorAdapter extends RecyclerView.Adapter<TagsSelectorAdapter.TagViewHolder> {

    private List<EventTag> tags;
    private Set<EventTag> selectedTags;
    private ClickListener clickListener;

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_selector, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        EventTag tag = tags.get(position);
        holder.name.setText(tag.name);
        holder.checkBox.setChecked(selectedTags.contains(tag));
        ImageHandler.loadImage(holder.thumbnail, tag.imagePath,
                holder.itemView.getContext().getDrawable(R.drawable.item_placeholder));
        holder.itemView.setOnClickListener(v -> onItemClicked(holder, tag));
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 : tags.size();
    }

    public List<EventTag> getTags() {
        return tags;
    }

    public void setTags(List<EventTag> tags) {
        this.tags = tags;
    }

    public Set<EventTag> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(Set<EventTag> selectedTags) {
        this.selectedTags = selectedTags;
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public boolean isAllChecked() {
        return tags.size() == selectedTags.size();
    }

    public interface ClickListener {
        void onItemClicked(TagsSelectorAdapter source);
    }

    private void onItemClicked(TagViewHolder holder, EventTag tag) {
        boolean isChecked = holder.checkBox.isChecked();
        holder.checkBox.setChecked(!isChecked);
        if (isChecked) {
            selectedTags.remove(tag);
        } else {
            selectedTags.add(tag);
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
