package com.example.waiyan_mealmate.DataHolder;

public class NewMeal {
    int MealId;
    String MealName;
    byte[] MealPhoto;

    public NewMeal(int mealId, String mealName, byte[] mealPhoto) {
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

    public byte[] getMealPhoto() {
        return MealPhoto;
    }
}
