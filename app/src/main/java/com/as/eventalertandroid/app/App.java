package com.as.eventalertandroid.app;

import android.app.Application;

import com.as.eventalertandroid.net.client.PicassoClient;
import com.squareup.picasso.Picasso;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.setSingletonInstance(PicassoClient.getPicasso(this));
    }

}
