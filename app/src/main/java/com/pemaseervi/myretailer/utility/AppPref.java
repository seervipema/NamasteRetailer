package com.pemaseervi.myretailer.utility;

import android.content.Context;

public class AppPref {
    public static final String APP_PREF = "AppPrefTest";
    public static final String USER_TOKEN = "USER_TOKEN";
    public static  final String USER_NAME="USER_NAME";

    public static void saveUserToken(Context context, String token) {
        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(context);
        helper.setPreferenceName(APP_PREF);
        helper.putString(USER_TOKEN, token);
    }
    public static void saveUserName(Context context,String username){
        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(context);
        helper.setPreferenceName(APP_PREF);
        helper.putString(USER_NAME,username);
    }
    public static String getUserName(Context context){
        return SharedPreferenceHelper.getInstance(context).setPreferenceName(APP_PREF).getString(USER_NAME);
    }
    public static String getUserToken(Context context) {
        return SharedPreferenceHelper.getInstance(context).setPreferenceName(APP_PREF).getString(USER_TOKEN);
    }

}
