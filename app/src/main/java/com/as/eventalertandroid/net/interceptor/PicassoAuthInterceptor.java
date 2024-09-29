package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.handler.JwtHandler;
import com.as.eventalertandroid.net.Session;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PicassoAuthInterceptor implements Interceptor {

    private final Session session = Session.getInstance();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request mainRequest = chain.request();

        if (JwtHandler.isExpired(session.getAccessToken())) {
            CompletableFuture<?> cf = session.refreshToken();
            try {
                cf.get(30, TimeUnit.SECONDS);
                return chain.proceed(getAuthRequest(mainRequest, session.getAccessToken()));
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                return chain.proceed(mainRequest);
            }
        } else {
            return chain.proceed(getAuthRequest(mainRequest, session.getAccessToken()));
        }
    }

    private Request getAuthRequest(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

}
