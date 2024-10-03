package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.handler.JwtHandler;
import com.as.eventalertandroid.handler.SyncHandler;

import java.io.IOException;

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
            SyncHandler.refreshToken().join();
        }

        return chain.proceed(createRequestWithAuthHeader(mainRequest, session.getAccessToken()));
    }

    private Request createRequestWithAuthHeader(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

}
