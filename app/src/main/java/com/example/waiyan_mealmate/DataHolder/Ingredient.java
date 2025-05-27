package com.example.waiyan_mealmate.DataHolder;

public class Ingredient {
    String name;
    String amount;
    int id, status;

    public Ingredient(String name, String amount, int id, int status) {
        this.name = name;
        this.amount = amount;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public  int getStatus(){
        return  status;
    }

}
