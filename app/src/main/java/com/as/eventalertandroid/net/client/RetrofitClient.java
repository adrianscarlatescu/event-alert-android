package com.as.eventalertandroid.net.client;

import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.net.adapter.CompletableFutureCallAdapterFactory;
import com.as.eventalertandroid.net.adapter.GsonLocalDateAdapter;
import com.as.eventalertandroid.net.adapter.GsonLocalDateTimeAdapter;
import com.as.eventalertandroid.net.interceptor.RetrofitAuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final Retrofit instance;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new RetrofitAuthInterceptor());
        OkHttpClient client = builder.build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter());
        Gson gson = gsonBuilder.create();

        instance = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new CompletableFutureCallAdapterFactory())
                .build();
    }

    public static Retrofit getInstance() {
        return instance;
    }

}
