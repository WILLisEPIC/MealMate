package com.example.waiyan_mealmate.DataHolder;

public class UserMealData {

    int MealId, Status, MealPhotoPos;
    String MealName;
    byte[] MealPhoto;

    public UserMealData(int mealId, String mealName, byte[] mealPhoto,int mealPhotoPos, int Status) {
        this.MealId = mealId;
        this.MealName = mealName;
        this.MealPhoto = mealPhoto;
        this.MealPhotoPos = mealPhotoPos;
        this.Status = Status;
    }

    public int getMealId() {
        return MealId;
    }

    public String getMealName() {
        return MealName;
    }

    public byte[] getMealPhoto() {
        return MealPhoto;
    }

    public int getMealPhotoPos() {
        return MealPhotoPos;
    }

    public int getStatus() {
        return Status;
    }
}
