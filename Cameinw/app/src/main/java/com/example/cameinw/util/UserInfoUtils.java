package com.example.cameinw.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoUtils {

    private static final String USER_INFO_PREFERENCES = "userInfo";

    public static String getUserToken(Context context) {
        SharedPreferences userInfoPreferences = context.getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        return userInfoPreferences.getString("token", "");
    }

    public static Integer getUserId(Context context) {
        SharedPreferences userInfoPreferences = context.getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        Integer userIdString = userInfoPreferences.getInt("id", 0);
        if (!userIdString.equals(0)) {
            return userIdString;
        }
        return null; // Return null if the user ID is not available or invalid
    }

    public static void saveUserData(Context context, String userToken, Integer userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", userToken);
        editor.putInt("id", userId);
        editor.apply();
    }

    public static void clearUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_INFO_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", "");
        editor.putString("id", "");

        editor.apply();
    }
}

