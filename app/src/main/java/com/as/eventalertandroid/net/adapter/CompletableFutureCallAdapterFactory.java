package com.as.eventalertandroid.net.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

public final class CompletableFutureCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public @EverythingIsNonNull
    CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != CompletableFuture.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("Return type must be parameterized");
        }
        Type innerType = getParameterUpperBound(0, (ParameterizedType) returnType);
        if (getRawType(innerType) != Response.class) {
            return new BodyCallAdapter<>(innerType);
        }
        if (!(innerType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parameterized");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) innerType);
        return new ResponseCallAdapter<>(responseType);
    }

    private static final class BodyCallAdapter<R> implements CallAdapter<R, CompletableFuture<R>> {
        private final Type responseType;

        BodyCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public @NonNull
        Type responseType() {
            return responseType;
        }

        @Override
        public @NonNull
        CompletableFuture<R> adapt(final Call<R> call) {
            final CompletableFuture<R> future = new CompletableFuture<R>() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    if (mayInterruptIfRunning) {
                        call.cancel();
                    }
                    return super.cancel(mayInterruptIfRunning);
                }
            };

            call.enqueue(new Callback<R>() {
                @Override
                public @EverythingIsNonNull
                void onResponse(Call<R> call, Response<R> response) {
                    if (response.isSuccessful()) {
                        future.complete(response.body());
                    } else {
                        future.completeExceptionally(new HttpException(response));
                    }
                }

                @Override
                public @EverythingIsNonNull
                void onFailure(Call<R> call, Throwable t) {
                    future.completeExceptionally(t);
                }
            });

            return future;
        }
    }

    private static final class ResponseCallAdapter<R> implements CallAdapter<R, CompletableFuture<Response<R>>> {
        private final Type responseType;

        ResponseCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public @NonNull
        Type responseType() {
            return responseType;
        }

        @Override
        public @NonNull
        CompletableFuture<Response<R>> adapt(final Call<R> call) {
            final CompletableFuture<Response<R>> future = new CompletableFuture<Response<R>>() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    if (mayInterruptIfRunning) {
                        call.cancel();
                    }
                    return super.cancel(mayInterruptIfRunning);
                }
            };

            call.enqueue(new Callback<R>() {
                @Override
                public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response) {
                    future.complete(response);
                }

                @Override
                public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
                    future.completeExceptionally(t);
                }
            });

            return future;
        }
    }

}