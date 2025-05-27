package com.example.waiyan_mealmate;

import android.app.Application;
import android.content.SharedPreferences;

public class MealMate extends Application {
    private int UserID;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MealMate", MODE_PRIVATE);
        UserID = preferences.getInt("UserID", 0);
    }

    public int getUserId() {
        return UserID;
    }

    public void setUserId(int userId) {
        this.UserID = userId;
        //save userId to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MealMate", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("UserID", userId);
        editor.apply();
    }
}
