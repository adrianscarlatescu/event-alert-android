package com.as.eventalertandroid.app;

import android.app.Application;

import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.net.client.PicassoClient;
import com.as.eventalertandroid.net.client.RetrofitClient;
import com.squareup.picasso.Picasso;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocalDatabase.init(this);
        RetrofitClient.init();
        Picasso.setSingletonInstance(PicassoClient.getPicasso(this));
    }

}
