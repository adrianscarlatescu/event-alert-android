package com.as.eventalertandroid.net.client;

import android.app.Application;

import com.as.eventalertandroid.net.interceptor.PicassoAuthInterceptor;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class PicassoClient {

    public static Picasso getPicasso(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(application.getCacheDir(), cacheSize);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
        builder.addInterceptor(new PicassoAuthInterceptor());
        OkHttpClient client = builder.build();

        return new Picasso.Builder(application.getApplicationContext())
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

}
