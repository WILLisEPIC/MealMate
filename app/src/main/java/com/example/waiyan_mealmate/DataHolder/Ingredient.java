package com.example.waiyan_mealmate.DataHolder;

public class Ingredient {

    private String name, amount, type, title;
    private int id, status;

    public Ingredient(String name, String amount, int id, String type, int status) {
        this.name = name;
        this.amount = amount;
        this.id = id;
        this.type = type;
        this.status = status;
    }

    public Ingredient(String type, String title){
        this.type = type;
        this.title = title;
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

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
