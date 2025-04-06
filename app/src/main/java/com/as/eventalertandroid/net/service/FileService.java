package com.as.eventalertandroid.net.service;

import android.graphics.Bitmap;

import com.as.eventalertandroid.enums.ImageType;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileService {

    @Multipart
    @POST("/api/images")
    CompletableFuture<String> postImage(@Query("type") ImageType imageType,
                                        @Part MultipartBody.Part image);

    static MultipartBody.Part getPartFromBitmap(Bitmap bitmap, String filename) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), byteArray);
        return MultipartBody.Part.createFormData("image", filename, requestFile);
    }

}
