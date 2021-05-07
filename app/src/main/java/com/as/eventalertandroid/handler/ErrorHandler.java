package com.as.eventalertandroid.handler;

import android.content.Context;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.ApiError;
import com.as.eventalertandroid.net.model.FailureDefaultResponse;
import com.as.eventalertandroid.net.model.FailureResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.stream.Stream;

import retrofit2.HttpException;
import retrofit2.Response;

public class ErrorHandler {

    private static Gson gson = new Gson();

    public static String getMessage(Context context, Throwable throwable) {
        if (throwable.getCause() instanceof HttpException) {
            Response<?> response = ((HttpException) throwable.getCause()).response();
            if (response == null || response.errorBody() == null) {
                return context.getString(R.string.message_default_error);
            }
            try {
                String stringErrors = response.errorBody().string();
                ApiError[] apiErrors = gson.fromJson(stringErrors, FailureResponse.class).errors;
                if (apiErrors == null || apiErrors.length == 0) {
                    String message = gson.fromJson(stringErrors, FailureDefaultResponse.class).message;
                    if (message == null || message.isEmpty()) {
                        return context.getString(R.string.message_default_error);
                    }
                    return message;
                } else {
                    StringBuilder builder = new StringBuilder();
                    Stream.of(apiErrors).forEach(apiError -> {
                        builder.append(apiError.message);
                        builder.append("\n");
                    });
                    String result = builder.toString();
                    return result.substring(0, result.length() - 1);
                }
            } catch (IOException e) {
                return context.getString(R.string.message_default_error);
            }
        }
        return context.getString(R.string.message_default_error);
    }

}
