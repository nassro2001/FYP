package com.example.FinalProject.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagerPt {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    private static final String PREF_NAME = "LOGIN";
    private static final String ID = "IDPT";



    public SessionManagerPt(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void createSession(int idUser){

        editor.putInt(ID, idUser).commit();

    }

    public int getSession(){
        return sharedPreferences.getInt(ID, -1);
    }

    public HashMap<String, Integer> getUserId(){
        HashMap<String,Integer> user = new HashMap<>();
        user.put(ID,sharedPreferences.getInt(ID,0));

        return user;
    }

    public void removeSession(){
        editor.putInt(ID, -1).commit();
    }
}
