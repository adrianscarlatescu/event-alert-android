package com.as.eventalertandroid.handler;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.ApiFailure;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ErrorHandler {

    private static final Gson gson = new Gson();

    public static String getMessage(Context context, Throwable throwable) {
        if (throwable.getCause() instanceof HttpException) {
            Response<?> response = ((HttpException) throwable.getCause()).response();
            if (response != null && response.code() == 401) {
                return context.getString(R.string.message_authorization_error);
            }
            if (response == null) {
                return context.getString(R.string.message_default_error);
            }

            try (ResponseBody errorBody = response.errorBody()) {
                if (errorBody == null) {
                    return context.getString(R.string.message_default_error);
                }

                ApiFailure apiFailure = gson.fromJson(errorBody.string(), ApiFailure.class);
                if (apiFailure == null || apiFailure.errors == null || apiFailure.errors.length == 0) {
                    return context.getString(R.string.message_default_error);
                } else {
                    StringBuilder builder = new StringBuilder();
                    Stream.of(apiFailure.errors).forEach(apiError -> {
                        builder.append(apiError.message);
                        builder.append("\n");
                    });
                    String result = builder.toString();
                    return result.substring(0, result.length() - 1);
                }
            } catch (IOException e) {
                return context.getString(R.string.message_default_error);
            }
        } else if (throwable instanceof CompletionException &&
                throwable.getCause() instanceof SocketTimeoutException) {
            return context.getString(R.string.message_timeout_error);
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
