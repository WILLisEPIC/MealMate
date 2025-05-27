package com.example.waiyan_mealmate.DataHolder;

public class IngredientData {

    String IngredientName, Amount;
    int Status;

    public IngredientData(String IngredientName, String Amount, int Status){
        this.IngredientName = IngredientName;
        this.Amount = Amount;
        this.Status = Status;
    }

    public String getAmount() {
        return Amount;
    }

    public String getIngredientName() {
        return IngredientName;
    }

    public int getStatus() {
        return Status;
    }
}
