package com.as.eventalertandroid.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.handler.ImageHandler;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageDialog extends Dialog {

    @BindView(R.id.dialogImageView)
    ImageView imageView;

    private String path;

    public ImageDialog(@NonNull Context context, String path) {
        super(context);
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image);
        ButterKnife.bind(this);

        ImageHandler.loadImage(imageView, path);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @OnClick(R.id.dialogImageView)
    void onImageClicked() {
        dismiss();
    }

}
