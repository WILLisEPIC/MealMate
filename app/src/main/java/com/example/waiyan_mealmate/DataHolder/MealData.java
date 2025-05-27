package com.example.waiyan_mealmate.DataHolder;

public class MealData {
    int MealId, MealPhoto;
    String MealName;

    public MealData(int mealId, String mealName, int mealPhoto) {
        this.MealId = mealId;
        this.MealName = mealName;
        this.MealPhoto = mealPhoto;
    }

    public int getMealId() {
        return MealId;
    }

    public String getMealName() {
        return MealName;
    }

    public int getMealPhoto() {
        return MealPhoto;
    }
}
