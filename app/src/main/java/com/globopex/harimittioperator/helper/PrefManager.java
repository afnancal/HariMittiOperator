package com.globopex.harimittioperator.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Afnan on 13-Apr-17.
 */
public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "HariMitti_Operator";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_LOGIN = "user_login";
    private static final String KEY_PROFILE_IMAGE_NAME = "profile_image_name";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_GCM_REGISTRATION = "gcm_registration";
    private static final String KEY_URL = "url";

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUserID(String userID) {
        editor.putString(KEY_USER_ID, userID);
        editor.commit();
    }

    public String getUserID() {
        return pref.getString(KEY_USER_ID, null);
    }

    public void setUserLogin(boolean userLogin) {
        editor.putBoolean(KEY_USER_LOGIN, userLogin);
        editor.commit();
    }

    public boolean getUserLogin() {
        return pref.getBoolean(KEY_USER_LOGIN, false);
    }

    public void setProfileImageName(String profileImageName) {
        editor.putString(KEY_PROFILE_IMAGE_NAME, profileImageName);
        editor.commit();
    }

    public String getProfileImageName() {
        return pref.getString(KEY_PROFILE_IMAGE_NAME, null);
    }

    public void setUserName(String userName) {
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    public void setUserType(String userType) {
        editor.putString(KEY_USER_TYPE, userType);
        editor.commit();
    }

    public String getUserType() {
        return pref.getString(KEY_USER_TYPE, null);
    }

    public void setGcmRegistration(String gcmRegistration) {
        editor.putString(KEY_GCM_REGISTRATION, gcmRegistration);
        editor.commit();
    }

    public String getGcmRegistration() {
        return pref.getString(KEY_GCM_REGISTRATION, null);
    }

    public void setUrl(String url) {
        editor.putString(KEY_URL, url);
        editor.commit();
    }

    public String getUrl() {
        return pref.getString(KEY_URL, null);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

}
