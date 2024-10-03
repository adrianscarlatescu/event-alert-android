package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.handler.JwtHandler;
import com.as.eventalertandroid.handler.SyncHandler;

import java.io.IOException;

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
        String url = mainRequest.url().toString();

        if (url.matches(Constants.LOGIN_URL_REGEX) ||
                url.matches(Constants.REGISTER_URL_REGEX) ||
                url.matches(Constants.SUBSCRIPTIONS_TOKEN_URL_REGEX)) {
            return chain.proceed(mainRequest);
        }

        if (url.matches(Constants.REFRESH_URL_REGEX)) {
            return chain.proceed(createRequestWithAuthHeader(mainRequest, session.getRefreshToken()));
        }

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

