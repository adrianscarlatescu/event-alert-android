package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.handler.JwtHandler;
import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.handler.SyncHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetrofitAuthInterceptor implements Interceptor {

    private final Session session = Session.getInstance();

    @Override
    public @NonNull
    Response intercept(@NonNull Chain chain) throws IOException {
        Request mainRequest = chain.request();

        Set<String> authHeader = mainRequest.headers().names();
        if (!authHeader.contains("Authorization")) {
            return chain.proceed(mainRequest);
        }

        String auth = mainRequest.header("Authorization");
        if (auth == null) {
            return chain.proceed(mainRequest);
        }

        switch (auth) {
            case "Refresh Token":
                // TODO: handle token expiration
                return chain.proceed(getAuthRequest(mainRequest, session.getRefreshToken()));
            case "Access Token":
                if (JwtHandler.isExpired(session.getAccessToken())) {
                    CompletableFuture<?> cf = SyncHandler.refreshToken();
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

        return chain.proceed(mainRequest);
    }

    private Request getAuthRequest(Request request, String token) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
    }

}

