package com.as.eventalertandroid.ui.common.event;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class CommentDialog extends Dialog {

    @BindView(R.id.dialogCommentEditText)
    EditText commentEditText;

    public CommentDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment);
        ButterKnife.bind(this);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @OnClick(R.id.dialogCommentValidateButton)
    void onValidateClicked() {
        String comment = commentEditText.getText().toString();
        if (comment.isEmpty()) {
            Toast.makeText(getContext(), R.string.message_comment_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.length() > Constants.MAX_COMMENT_LENGTH) {
            String message = String.format(getContext().getString(R.string.message_comment_length), Constants.MAX_COMMENT_LENGTH);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        onValidateClicked(commentEditText.getText().toString());
    }

    public abstract void onValidateClicked(String comment);

}
