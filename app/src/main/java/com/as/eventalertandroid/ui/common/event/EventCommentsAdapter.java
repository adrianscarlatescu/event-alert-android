package com.as.eventalertandroid.ui.common.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.ImageHandler;
import com.as.eventalertandroid.net.model.CommentDTO;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventCommentsAdapter extends RecyclerView.Adapter<EventCommentsAdapter.CommentViewHolder> {

    private List<CommentDTO> comments;


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentDTO comment = comments.get(position);
        ImageHandler.loadImage(holder.creatorImageView, comment.user.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
        String creatorName = comment.user.firstName + " " + comment.user.lastName;
        holder.creatorNameTextView.setText(creatorName);
        holder.commentTextView.setText(comment.comment);
        holder.createdAtTextView.setText(comment.createdAt.format(Constants.defaultDateTimeFormatter));
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    public void setComments(List<CommentDTO> commentDTOs) {
        this.comments = commentDTOs;
        notifyDataSetChanged();
    }

    public void addComment(CommentDTO comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(0, comment);
        notifyItemInserted(0);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemCommentCreatorImageView)
        ImageView creatorImageView;
        @BindView(R.id.itemCommentCreatorNameTextView)
        TextView creatorNameTextView;
        @BindView(R.id.itemCommentCreatedAtTextView)
        TextView createdAtTextView;
        @BindView(R.id.itemCommentTextView)
        TextView commentTextView;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
