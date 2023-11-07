package com.example.cameinw.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.example.cameinw.api.ImageApi;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtils {
    public static void fetchMultipleUsersImageWithRetry(
            List<Integer> userIds,
            String userToken,
            int maxRetries,
            ImageFetchCallback callback) {

        for (int userId : userIds) {
            fetchUserImageWithRetry(userId, userToken, maxRetries, callback);
        }
    }

    public static void fetchUserImageWithRetry(
            final int userId,
            final String userToken,
            final int remainingRetries,
            final ImageFetchCallback callback) {

        if (remainingRetries <= 0) {
            callback.onImageFetchError(userId);
            return;
        }

        ImageApi imageApi = RetrofitUtils.createRequest(ImageApi.class, userToken);
        Call<ResponseBody> call = imageApi.getUserImage(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody>
                    response) {
                if (response.isSuccessful()) {
                    Bitmap imageBitmap =
                            ImageUtils.decodeBitmapFromResponse(response.body());
                    callback.onImageFetchSuccess(userId, imageBitmap);
                } else {
                    // Retry after a delay
                    retryFetchWithDelay(userId, userToken, remainingRetries - 1,
                            callback);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // Retry after a delay
                retryFetchWithDelay(userId, userToken, remainingRetries - 1, callback);
            }
        });
    }



    public static void fetchMultiplePlacesImageWithRetry(
            Integer placeId,
            List<Integer> placeImgIds,
            String userToken,
            int maxRetries,
            ImageFetchCallback callback) {

        for (int placeImgId : placeImgIds) {
            fetchPlaceImageWithRetry(placeId, placeImgId, userToken, maxRetries,
                    callback);
        }
    }

    public static void fetchPlaceImageWithRetry(
            final int placeId,
            final int placeImgId,
            final String userToken,
            final int remainingRetries,
            final ImageFetchCallback callback) {

        if (remainingRetries <= 0) {
            callback.onImageFetchError(placeImgId);
            return;
        }

        ImageApi imageApi = RetrofitUtils.createRequest(ImageApi.class, userToken);
        Call<ResponseBody> call = imageApi.getPlaceImageById(placeId, placeImgId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody>
                    response) {
                if (response.isSuccessful()) {
                    Bitmap imageBitmap =
                            ImageUtils.decodeBitmapFromResponse(response.body());
                    callback.onImageFetchSuccess(placeImgId, imageBitmap);
                } else {
                    // Retry after a delay
                    retryFetchWithDelay(placeImgId, userToken, remainingRetries - 1,
                            callback);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                // Retry after a delay
                retryFetchWithDelay(placeImgId, userToken, remainingRetries - 1,
                        callback);
            }
        });
    }



    private static void retryFetchWithDelay(
            final int id,
            final String userToken,
            final int remainingRetries,
            final ImageFetchCallback callback) {

        // Retry after a delay of, for example, 2 seconds
        final long delayMillis = 2000;

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchUserImageWithRetry(id, userToken, remainingRetries, callback);
            }
        }, delayMillis);
    }

    public static Bitmap decodeBitmapFromResponse(ResponseBody responseBody) {
        try {
            byte[] imageBytes = responseBody.bytes();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface ImageFetchCallback {
        void onImageFetchSuccess(int id, Bitmap imageBitmap);
        void onImageFetchError(int id);
    }
}

