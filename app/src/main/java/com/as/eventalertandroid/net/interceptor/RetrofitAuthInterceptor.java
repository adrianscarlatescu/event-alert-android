package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.net.Session;
import com.auth0.android.jwt.JWT;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetrofitAuthInterceptor implements Interceptor {

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

        Session session = Session.getInstance();
        switch (auth) {
            case "Refresh Token":
                Request refreshRequest = mainRequest.newBuilder()
                        .header("Authorization", "Bearer " + session.getRefreshToken())
                        .build();
                return chain.proceed(refreshRequest);
            case "Access Token":
                JWT jwt = new JWT(session.getAccessToken());
                if (jwt.isExpired(1)) {
                    CompletableFuture<?> cf = session.refreshToken();
                    try {
                        cf.get();
                        Request accessRequest = mainRequest.newBuilder()
                                .header("Authorization", "Bearer " + session.getAccessToken())
                                .build();
                        return chain.proceed(accessRequest);
                    } catch (ExecutionException | InterruptedException e) {
                        // Nothing to do
                    }
                } else {
                    Request accessRequest = mainRequest.newBuilder()
                            .header("Authorization", "Bearer " + session.getAccessToken())
                            .build();
                    return chain.proceed(accessRequest);
                }
        }

        return chain.proceed(mainRequest);
    }

}

