package com.as.eventalertandroid.handler;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.ApiError;
import com.as.eventalertandroid.net.model.ApiFailureResponse;
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
                ApiError[] apiErrors = gson.fromJson(stringErrors, ApiFailureResponse.class).errors;
                if (apiErrors == null || apiErrors.length == 0) {
                    return context.getString(R.string.message_default_error);
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

    public static void showMessage(Activity activity, Throwable throwable) {
        activity.runOnUiThread(() ->
                Toast.makeText(
                        activity, ErrorHandler.getMessage(activity, throwable), Toast.LENGTH_SHORT
                ).show());
    }

}
