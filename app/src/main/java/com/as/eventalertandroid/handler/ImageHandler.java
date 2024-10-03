package com.as.eventalertandroid.handler;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.as.eventalertandroid.defaults.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageHandler {

    public static void loadImage(ImageView image, String path) {
        loadImage(image, path, null, null);
    }

    public static void loadImage(ImageView image, String path, Drawable placeholder) {
        loadImage(image, path, placeholder, null);
    }

    public static void loadImage(ImageView image, String path, Callback callback) {
        loadImage(image, path, null, callback);
    }

    private static void loadImage(ImageView image, String path, Drawable placeholder, Callback callback) {
        if (path == null || path.isEmpty()) {
            image.setImageDrawable(placeholder);
            return;
        }
        String uri = Constants.BASE_URL + "api/image?path=" + path;
        Picasso.get()
                .load(uri)
                .placeholder(placeholder)
                .into(image, callback);
        image.setTag(path);
    }

}
