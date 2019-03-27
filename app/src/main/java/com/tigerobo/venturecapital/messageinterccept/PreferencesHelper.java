package com.tigerobo.venturecapital.messageinterccept;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String USER_PREFERENCES = "userPreferences";
    public static final String PREFERENCE_URL = ".url";
    public static final String PREFERENCE_PARAMS = ".params";

    public PreferencesHelper() {
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static String getRequestUrl(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        String url = preferences.getString(PREFERENCE_URL, "");
        return url;
    }

    public static void setRequestUrl(Context context, String url) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCE_URL, url);
        editor.apply();
    }

    public static String getParamsName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        String name = preferences.getString(PREFERENCE_PARAMS, "");
        return name;
    }

    public static void setParamsName(Context context, String name) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCE_PARAMS, name);
        editor.apply();
    }


}
