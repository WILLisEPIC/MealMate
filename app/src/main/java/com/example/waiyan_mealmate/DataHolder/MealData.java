package com.example.waiyan_mealmate.DataHolder;

public class MealData {
    private int mealId, mealPhotoId;
    private String mealName;
    private byte[] mealPhotoByte;

    public MealData(int mealId, String mealName, int mealPhotoId) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealPhotoId = mealPhotoId;
    }

    public MealData(int mealId, String mealName, byte[] mealPhotoByte) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealPhotoByte = mealPhotoByte;
    }

    public int getMealId() {
        return mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public int getMealPhotoId() {
        return mealPhotoId;
    }

    public byte[] getMealPhotoByte() {
        return mealPhotoByte;
    }
}
