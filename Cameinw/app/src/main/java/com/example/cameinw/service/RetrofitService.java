package com.example.cameinw.service;

import com.example.cameinw.util.SslConfigUtil;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Service class for initializing and configuring Retrofit instances.
 */
public class RetrofitService {

    private Retrofit retrofit;
    public static final String BASE_URL = "url";

    /**
     * Initializes a new instance of RetrofitService.
     */
    public RetrofitService() {}

    /**
     * Initializes Retrofit with a provided authentication token.
     *
     * @param token The authentication token to be used for endpoints that require
    authorization.
     */
    // Initialize Retrofit with token (for endpoints that need a token)
    public void setTokenRetrofit(String token) {
        OkHttpClient okHttpClient = buildOkHttpClient(token);
        retrofit = buildRetrofit(okHttpClient);
    }


    /**
     * Initializes Retrofit without an authentication token.
     */
    // Initialize Retrofit without token (for endpoints that don't need a token)
    public void setRetrofit() {
        OkHttpClient okHttpClient = buildOkHttpClient(null);
        retrofit = buildRetrofit(okHttpClient);
    }

    /**
     * Gets the Retrofit instance.
     *
     * @return The Retrofit instance.
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * Builds and configures an OkHttpClient instance with SSL configuration and
     optional token authentication.
     *
     * @param token The authentication token to be used for endpoints that require
    authorization. Pass null if no token is required.
     * @return An OkHttpClient instance configured with SSL and optional token
    authentication.
     */
    private OkHttpClient buildOkHttpClient(final String token) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        SslConfigUtil.configureSsl(builder);
        addTokenInterceptor(builder, token);
        return builder.build();
    }


    /**
     * Adds an authentication header to the OkHttpClient if a token is provided.
     *
     * @param builder The OkHttpClient.Builder to which the interceptor is added.
     * @param token The authentication token to be added as a header.
     */
    // Add authentication header if a token is provided
    private void addTokenInterceptor(OkHttpClient.Builder builder, String token) {
        if (token != null) {
            builder.addInterceptor(chain -> {
                Request request = chain.request();
                Request.Builder newRequest =
                        request.newBuilder().header("Authorization", "Bearer " + token);
                return chain.proceed(newRequest.build());
            });
        }
    }

    /**
     * Builds a Retrofit instance with the provided OkHttpClient instance and base URL.
     *
     * @param okHttpClient The OkHttpClient instance to be used by Retrofit.
     * @return A Retrofit instance configured with the provided OkHttpClient and
    base URL.
     */
    private Retrofit buildRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpClient)
                .build();
    }
}