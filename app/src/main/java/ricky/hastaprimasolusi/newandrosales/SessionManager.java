package ricky.hastaprimasolusi.newandrosales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

@SuppressLint("CommitPrefEdits")
public class SessionManager {

        // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndrosalesPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN 	= "IsLoggedIn";
    private static final String IS_SETTING 	= "IsSettingIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME 	= "name";
    public static final String KEY_PASS 	= "pass";
    public static final String KEY_IMEI 	= "imeiID";
    public static final String KEY_IP 		= "ipaddress";
    public static final String KEY_SERVER 	= "server";
    public static final String KEY_NIK      = "NIK";
    public static final String KEY_UUID     = "uuid";
    public static final String KEY_PASSWORD = "password";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String imeiID, String NIK){
        // Storing
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_IMEI, imeiID);
        editor.putString(KEY_NIK, NIK);
        editor.commit();
    }
    public void createSettingSession(String ipaddress, String server){
        // Storing
        editor.putBoolean(IS_SETTING, true);
        editor.putString(KEY_IP, ipaddress);
        editor.putString(KEY_SERVER, server);
        editor.commit();
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
        else
        {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }
    public void checkReg(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        user.put(KEY_IMEI, pref.getString(KEY_IMEI, null));
        user.put(KEY_NIK, pref.getString(KEY_NIK,null));
        user.put(KEY_UUID, pref.getString(KEY_UUID, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        // return user
        return user;
    }
    public HashMap<String, String> getSettingDetails(){
        HashMap<String, String> setting = new HashMap<String, String>();
        // user email id
        setting.put(KEY_IP, pref.getString(KEY_IP, null));
        setting.put(KEY_SERVER, pref.getString(KEY_SERVER, null));
        // return user
        return setting;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
