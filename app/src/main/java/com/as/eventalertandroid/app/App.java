package com.as.eventalertandroid.app;

import android.app.Application;
import android.content.Context;

import com.as.eventalertandroid.data.LocalDatabase;
import com.as.eventalertandroid.net.client.PicassoClient;
import com.squareup.picasso.Picasso;

public class App extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        LocalDatabase.initDatabase(this);
        Picasso.setSingletonInstance(PicassoClient.getPicasso(this));

        application = this;
    }

    public static Context getAppContext() {
        return application.getApplicationContext();
    }

}
