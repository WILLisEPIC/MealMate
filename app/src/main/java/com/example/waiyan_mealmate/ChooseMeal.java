package com.example.waiyan_mealmate;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.Adapter.AddMealAdapter;
import com.example.waiyan_mealmate.Adapter.MealAdapter;
import com.example.waiyan_mealmate.DataHolder.MealData;
import com.example.waiyan_mealmate.DataHolder.NewMeal;
import com.example.waiyan_mealmate.Database.DBHelper;

import java.sql.Blob;
import java.util.ArrayList;

public class ChooseMeal extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView MainCourseRecyclerView, SoupRecyclerView, SaladRecyclerView, DessertRecyclerView, UserMealRecyclerView;
    ArrayList<MealData> MainCourseArrayList= new ArrayList<>();
    ArrayList<MealData> SoupArrayList = new ArrayList<>();
    ArrayList<MealData> DessertArrayList = new ArrayList<>();
    ArrayList<MealData> SaladArrayList = new ArrayList<>();
    ArrayList<NewMeal> UserMealArrayList = new ArrayList<>();
    MealAdapter mainCourseAdapter, soupAdapter, dessertAdapter, saladAdapter;
    AddMealAdapter userCreatedMealAdapter;
    TextView user;
    ImageButton back,user_add_meal;
    MealMate mealMate;
    int userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_meal);

        //retrieve UserID from MealMate
        mealMate = (MealMate) getApplication();
        userID = mealMate.getUserId();

        dbHelper = new DBHelper(this);
        MainCourseRecyclerView = findViewById(R.id.MainCourseRecyclerView);
        SoupRecyclerView = findViewById(R.id.SoupRecyclerView);
        SaladRecyclerView = findViewById(R.id.SaladRecyclerView);
        DessertRecyclerView = findViewById(R.id.DessertRecyclerView);
        UserMealRecyclerView = findViewById(R.id.UserMealRecyclerView);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseMeal.this,MainActivity.class));
                finish();
            }
        });

        user_add_meal = findViewById(R.id.user_addmeal);
        user_add_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseMeal.this, CreateMeal.class));
                finish();
            }
        });

        user = findViewById(R.id.UserMeal);

        setMainCourseData();
        setMainCourseRecyclerView();
        setSoupData();
        setSoupRecyclerView();
        setSaladData();
        setSaladRecyclerView();
        setDessertData();
        setDessertRecyclerView();
        setUserMealData();
        setUserMealRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //clear array data
        MainCourseArrayList.clear();
        SoupArrayList.clear();
        SaladArrayList.clear();
        DessertArrayList.clear();
        UserMealArrayList.clear();

        //refresh data
        setMainCourseData();
        setMainCourseRecyclerView();
        setSoupData();
        setSoupRecyclerView();
        setSaladData();
        setSaladRecyclerView();
        setDessertData();
        setDessertRecyclerView();
        setUserMealData();
        setUserMealRecyclerView();
    }

    //Add Main Course Data to Main Course Array List
    private void setMainCourseData(){
        Cursor cursor = dbHelper.selectMainCourseData();
        if(cursor != null && cursor.moveToFirst()){
            do {
                int MealID = cursor.getInt(cursor.getColumnIndexOrThrow("MealID"));
                String MealName = cursor.getString(cursor.getColumnIndexOrThrow("MealName"));
                int MealPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("Photo"));

                MainCourseArrayList.add(new MealData(MealID, MealName, MealPhoto));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Main Course RecyclerView Adapter
    private void setMainCourseRecyclerView() {
        mainCourseAdapter = new MealAdapter(this, MainCourseArrayList, userID);
        MainCourseRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MainCourseRecyclerView.setAdapter(mainCourseAdapter);
    }

    //Add Soup Data to Soup Array List
    private void setSoupData(){
        Cursor cursor = dbHelper.selectSoupData();
        if(cursor != null && cursor.moveToFirst()){
            do {
                int MealID = cursor.getInt(cursor.getColumnIndexOrThrow("MealID"));
                String MealName = cursor.getString(cursor.getColumnIndexOrThrow("MealName"));
                int MealPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("Photo"));

                SoupArrayList.add(new MealData(MealID, MealName, MealPhoto));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Soup RecyclerView Adapter
    private void setSoupRecyclerView() {
        soupAdapter = new MealAdapter(this, SoupArrayList, userID);
        SoupRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SoupRecyclerView.setAdapter(soupAdapter);
    }

    //Add Salad Data to Salad Array List
    private void setSaladData(){
        Cursor cursor = dbHelper.selectSaladData();
        if(cursor != null && cursor.moveToFirst()){
            do {
                int MealID = cursor.getInt(cursor.getColumnIndexOrThrow("MealID"));
                String MealName = cursor.getString(cursor.getColumnIndexOrThrow("MealName"));
                int MealPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("Photo"));

                SaladArrayList.add(new MealData(MealID, MealName, MealPhoto));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Salad RecyclerView Adapter
    private void setSaladRecyclerView() {
        saladAdapter = new MealAdapter(this, SaladArrayList, userID);
        SaladRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SaladRecyclerView.setAdapter(saladAdapter);
    }

    //Add Dessert Data to Dessert Array List
    private void setDessertData(){
        Cursor cursor = dbHelper.selectDessertData();
        if(cursor != null && cursor.moveToFirst()){
            do {
                int MealID = cursor.getInt(cursor.getColumnIndexOrThrow("MealID"));
                String MealName = cursor.getString(cursor.getColumnIndexOrThrow("MealName"));
                int MealPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("Photo"));

                DessertArrayList.add(new MealData(MealID, MealName, MealPhoto));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Dessert RecyclerView Adapter
    private void setDessertRecyclerView() {
        dessertAdapter = new MealAdapter(this, DessertArrayList, userID);
        DessertRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DessertRecyclerView.setAdapter(dessertAdapter);
    }

    //Add UserMeal Data to UserMeal Array List
    private void setUserMealData(){
        Cursor cursor = dbHelper.selectUserCreatedMeal(userID);
        if(cursor != null && cursor.moveToFirst()){
            do {
                int MealID = cursor.getInt(cursor.getColumnIndexOrThrow("MealID"));
                String MealName = cursor.getString(cursor.getColumnIndexOrThrow("MealName"));
                byte[] MealPhoto = cursor.getBlob(cursor.getColumnIndexOrThrow("Photo"));

                UserMealArrayList.add(new NewMeal(MealID, MealName, MealPhoto));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            user.setVisibility(View.INVISIBLE);
        }
    }

    //set UserMeal RecyclerView Adapter
    private void setUserMealRecyclerView() {
        userCreatedMealAdapter = new AddMealAdapter(this, UserMealArrayList, userID);
        UserMealRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        UserMealRecyclerView.setAdapter(userCreatedMealAdapter);
    }
}
