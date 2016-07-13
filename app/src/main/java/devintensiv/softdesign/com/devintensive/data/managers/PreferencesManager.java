package devintensiv.softdesign.com.devintensive.data.managers;


import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import devintensiv.softdesign.com.devintensive.R;
import devintensiv.softdesign.com.devintensive.ui.activities.MainActivity;
import devintensiv.softdesign.com.devintensive.utils.ConstantManager;
import devintensiv.softdesign.com.devintensive.utils.DevIntensiveApplication;

public class PreferencesManager {

    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_MAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_BIO_KEY
    };


    private static final String[] USER_VALUES = {
            ConstantManager.USER_RATING_VALUE,
            ConstantManager.USER_CODE_LINES,
            ConstantManager.USER_PROJECT_VALUE,
           };

    public PreferencesManager() {
        this.mSharedPreferences = DevIntensiveApplication.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> userFields) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_FIELDS.length; i++) {
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData() {
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY,
                DataManager.getInstance().getContext().getResources().getString(R.string.phone)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY,
                DataManager.getInstance().getContext().getResources().getString(R.string.email)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_KEY,
                DataManager.getInstance().getContext().getResources().getString(R.string.vk_profile)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GIT_KEY,
                DataManager.getInstance().getContext().getResources().getString(R.string.repository)));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_BIO_KEY,
                DataManager.getInstance().getContext().getResources().getString(R.string.about_me)));
        return userFields;
    }

    public List<String> loadUserProfileValues() {
        List <String> userValues = new ArrayList<>();
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_RATING_VALUE,"0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_CODE_LINES,"0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_PROJECT_VALUE,"0"));

        return userValues;
    }

    public void saveUserProfileValues(int[] userValues) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_VALUES.length; i++) {
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    public void saveUserPhoto(Uri uri){
        SharedPreferences.Editor editor =  mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserPhoto(){
       return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,  "android.resource://devintensiv.softdesign.com.devintensive/drawable/user_bg"));
    }

    public void saveUserAvatar(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserAvatar() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY, "android.resource://com.softdesign.devintensive/drawable/avatar"));
    }

    public void saveUserName(String firstName, String secondName) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_NAME_KEY, firstName + " " + secondName );
        editor.apply();
    }

    public String loadUserName() {
        return mSharedPreferences.getString(ConstantManager.USER_NAME_KEY, DataManager.getInstance().getContext().getResources().getString(R.string.user_header_name));
    }

    public String loadUserMail() {
        return mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY, DataManager.getInstance().getContext().getResources().getString(R.string.user_header_email));
    }

    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "null");
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY,"null");
    }


}
