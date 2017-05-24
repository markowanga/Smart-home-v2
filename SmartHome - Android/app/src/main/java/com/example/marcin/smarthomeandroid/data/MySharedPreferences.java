package com.example.marcin.smarthomeandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcin on 04.05.2017.
 */

public class MySharedPreferences {
    private static final String PREFERENCES = "com.example.marcin.smarthomeandroid.PREFERENCES";
    private static final String LOGIN_EMAIL = "com.example.marcin.smarthomeandroid.LOGIN_EMAIL";
    private static final String LOGIN_PASSWORD = "com.example.marcin.smarthomeandroid.LOGIN_PASSWORD";
    private static final String LOGIN_AUTOMATICALLY = "com.example.marcin.smarthomeandroid.LOGIN_AUTOMATICALLY";
    private static final String LOCATION_SERVICE_IS_RUNNING = "com.example.marcin.smarthomeandroid.LOCATION_SERVICE_IS_RUNNING";

    public static String getLoginEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(LOGIN_EMAIL, null);
    }

    public static void setLoginEmail(Context context, String email) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_EMAIL, email);
        editor.apply();
    }

    public static String getLoginPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getString(LOGIN_PASSWORD, null);
    }

    public static void setLoginPassword(Context context, String password) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LOGIN_PASSWORD, password);
        editor.apply();
    }

    public static boolean getLoginAutomatically(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(LOGIN_AUTOMATICALLY, false);
    }

    public static void setLoginAutomatically(Context context, boolean automatically) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGIN_AUTOMATICALLY, automatically);
        editor.apply();
    }

    public static void rememberCorrectData(Context context, String email, String password) {
        setLoginAutomatically(context, true);
        setLoginEmail(context, email);
        setLoginPassword(context, password);
    }

    public static void rememberIncorrectData(Context context) {
        setLoginAutomatically(context, false);
        setLoginEmail(context, null);
        setLoginPassword(context, null);
    }

    public static void setLocationAnalise(boolean isRunning, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOCATION_SERVICE_IS_RUNNING, isRunning);
        editor.apply();
    }

    public static boolean isLocationServiceRunning(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(LOCATION_SERVICE_IS_RUNNING, false);
    }
}