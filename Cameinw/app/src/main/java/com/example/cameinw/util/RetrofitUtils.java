package com.example.cameinw.util;

import com.example.cameinw.service.RetrofitService;

import retrofit2.Retrofit;

public class RetrofitUtils {
    public static <T> T createRequest(Class<T> serviceClass, String userToken) {
        RetrofitService retrofitService = new RetrofitService();
        retrofitService.setTokenRetrofit(userToken);
        Retrofit retrofit = retrofitService.getRetrofit();
        return retrofit.create(serviceClass);
    }
}
