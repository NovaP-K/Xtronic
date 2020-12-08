package com.novaapps.xtronic.helpingclass;

import android.content.Context;
import android.content.SharedPreferences;

public class UI_Data {

    private SharedPreferences pref;
    private static final String _PrefName = "UiValues";
    private static final String _IsLoggedIn = "false";
    private static final String _UserName = "$UserName";
    private static final String _UserEmail = "$UserEmail";
    private static final String _UserProfile = "$Url";
    private static final String _UserUid = "$Uid";
    public static final String _IsDeveloperViewEnabled="false";

    public UI_Data(Context context) {
        pref = context.getSharedPreferences(_PrefName , 0) ;
    }

    public void updateUI(boolean IsLogIn , String name , String email , String url , String uid ){

        if(IsLogIn){

            SharedPreferences.Editor editor = pref.edit();

            //Update
            editor.putBoolean(_IsLoggedIn , true);
            editor.putBoolean(_IsDeveloperViewEnabled , true);
            editor.putString(_UserName , name);
            editor.putString(_UserEmail , email);
            editor.putString(_UserProfile , url);
            editor.putString(_UserUid , uid);

            // Applying Changes
            editor.apply();

        }

    }

    public boolean IsLoggedIn(){
        return  pref.getBoolean(_IsLoggedIn , false);
    }

    public  String get_UserName() {
        return pref.getString(_UserName , null);
    }

    public  String get_UserEmail() {
        return pref.getString(_UserEmail , null);
    }

    public  String get_UserProfile() {
        return pref.getString(_UserProfile , null);
    }

    public  String get_UserUid() { return pref.getString(_UserUid , null);    }

    public  boolean IsDeveloperViewEnabled() { return  pref.getBoolean(_IsDeveloperViewEnabled , false);    }

    public void LogOut(){ pref.edit().clear().apply();  }

}
