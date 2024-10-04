package com.as.eventalertandroid.net.interceptor;

import com.as.eventalertandroid.app.Session;
import com.as.eventalertandroid.handler.JwtHandler;
import com.as.eventalertandroid.handler.SyncHandler;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.as.eventalertandroid.defaults.Constants.LOGIN_URL_REGEX;
import static com.as.eventalertandroid.defaults.Constants.REFRESH_URL_REGEX;
import static com.as.eventalertandroid.defaults.Constants.REGISTER_URL_REGEX;
import static com.as.eventalertandroid.defaults.Constants.SUBSCRIPTION_TOKEN_URL_REGEX;

public class RetrofitAuthInterceptor implements Interceptor {
    private final Session session = Session.getInstance();

    @Override
    public @NonNull
    Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        String url = request.url().toString();

        boolean isLoginRequest = "POST".equals(method) && url.matches(LOGIN_URL_REGEX);
        boolean isRegisterRequest = "POST".equals(method) && url.matches(REGISTER_URL_REGEX);
        boolean isRefreshTokenRequest = "GET".equals(method) && url.matches(REFRESH_URL_REGEX);
        boolean isSubscriptionTokenRequest = "PATCH".equals(method) && url.matches(SUBSCRIPTION_TOKEN_URL_REGEX);

        if (isLoginRequest || isRegisterRequest || isSubscriptionTokenRequest) {
            return chain.proceed(request);
        }

        if (isRefreshTokenRequest) {
            return chain.proceed(createRequestWithAuthHeader(request, session.getRefreshToken()));
        }

        if (JwtHandler.isExpired(session.getAccessToken())) {
            SyncHandler.refreshToken().join();
        }
        return chain.proceed(createRequestWithAuthHeader(request, session.getAccessToken()));
    }

    private Request createRequestWithAuthHeader(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

}

