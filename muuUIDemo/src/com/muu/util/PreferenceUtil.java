package com.muu.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferenceUtil {
    private static final String DEFAULT_PREFERENCE = "default_preference";

    public static void setInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setInt(Context context, String preference, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        return settings.getInt(key, 0);
    }

    public static int getInt(Context context, String preference, String key) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return settings.getInt(key, 0);
    }

    public static void setLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setLong(Context context, String preference, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        return settings.getLong(key, 0);
    }

    public static long getLong(Context context, String preference, String key) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return settings.getLong(key, 0);
    }

    public static void setBoolean(Context context, String key, Boolean value) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setBoolean(Context context, String preference, String key, Boolean value) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String preference, String key) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setString(Context context, String preference, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(DEFAULT_PREFERENCE,
                Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static String getString(Context context, String preference, String key) {
        SharedPreferences settings = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

}
