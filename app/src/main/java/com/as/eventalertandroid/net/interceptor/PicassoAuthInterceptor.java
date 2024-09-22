package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.net.Session;
import com.auth0.android.jwt.JWT;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PicassoAuthInterceptor implements Interceptor {

    private Session session = Session.getInstance();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request mainRequest = chain.request();
        JWT accessToken = new JWT(session.getAccessToken());

        if (accessToken.isExpired(0)) {
            CompletableFuture<?> cf = session.refreshToken();
            try {
                cf.get();
                Request modifiedRequest = mainRequest.newBuilder()
                        .addHeader("Authorization", "Bearer " + session.getAccessToken())
                        .build();
                return chain.proceed(modifiedRequest);
            } catch (Exception ex) {
                return chain.proceed(mainRequest);
            }
        } else {
            Request modifiedRequest = mainRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + session.getAccessToken())
                    .build();
            return chain.proceed(modifiedRequest);
        }
    }

}
