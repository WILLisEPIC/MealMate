package com.example.waiyan_mealmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.waiyan_mealmate.Adapter.IngredientAdapter;
import com.example.waiyan_mealmate.Adapter.PreparationAdapter;
import com.example.waiyan_mealmate.DataHolder.IngredientData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MealDetail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DBHelper dbHelper;
    private ImageView mealPhoto;
    private ImageButton ibBack, ibAdd;
    private TextView tvMealName;
    private Button btnDelete;
    private int MealID, MealPhoto;
    private String userID;
    private RecyclerView recyclerViewIngredient, recyclerViewPreparation;
    private IngredientAdapter ingredientAdapter;
    private PreparationAdapter preparationAdapter;
    private ArrayList<IngredientData> ingredientDataArrayList = new ArrayList<>();
    private ArrayList<String> preparationDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_detail);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();
        }
        dbHelper = new DBHelper(this);

        mealPhoto = findViewById(R.id.MealPhoto);
        ibBack = findViewById(R.id.back);
        ibAdd = findViewById(R.id.addmeal);
        tvMealName = findViewById(R.id.MealName);
        recyclerViewIngredient = findViewById(R.id.Ingredient);
        recyclerViewPreparation = findViewById(R.id.Preparation);
        btnDelete = findViewById(R.id.delete);

        Intent intent = getIntent();
        MealID = intent.getIntExtra("MealID", 0);

        setIngredientData();
        setIngredientView();

        setPreparationData();
        setPreparationView();

        if(MealID != 0){
            Cursor cursor = dbHelper.selectMealData(MealID);
            if (cursor != null && cursor.moveToNext()){
                String ID = cursor.getString(cursor.getColumnIndexOrThrow("UserID"));
                if(ID != null && ID.equals(userID)){
                    tvMealName.setText(cursor.getString(cursor.getColumnIndexOrThrow("MealName")));
                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow("Photo"));
                    if (photo == null){
                        mealPhoto.setImageResource(R.drawable.login_logo);
                    } else {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(photo)
                                .placeholder(R.drawable.login_logo)
                                .error(R.drawable.login_logo)
                                .into(mealPhoto);
                    }
                    btnDelete.setVisibility(View.VISIBLE);
                } else {
                    tvMealName.setText(cursor.getString(cursor.getColumnIndexOrThrow("MealName")));
                    MealPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("PhotoPos"));
                    mealPhoto.setImageResource(MealPhoto);
                }
                Cursor cursor1 = dbHelper.selectUserMealData(MealID, userID);
                if (cursor1 != null && cursor1.moveToFirst()){
                    ibAdd.setImageResource(R.drawable.remove_big);
                    mealPhoto.setImageAlpha(80);
                }else {
                    ibAdd.setImageResource(R.drawable.add_big);
                    mealPhoto.setImageAlpha(255);
                }
            }
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MealDetail.this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete "+ tvMealName.getText().toString())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor cursor = dbHelper.selectUserCreatedMealData(MealID, userID);
                                if (cursor != null && cursor.moveToFirst()) {
                                    Cursor cursor1 = dbHelper.selectUserMealData(MealID, userID);
                                    if (cursor1 != null && cursor1.moveToFirst()) {
                                        boolean check = dbHelper.deleteUserMeal(MealID, userID);
                                        if (check) {
                                            if (dbHelper.deleteIngredient(MealID) && dbHelper.deletePreparation(MealID)) {
                                                if (dbHelper.deleteMeal(MealID, userID)) {
                                                    new Message(tvMealName.getText().toString() + " completely deleted", MealDetail.this).showCompleteToast();
                                                    finish();
                                                } else {
                                                    new Message("Error!", MealDetail.this).showErrorToast();
                                                    finish();
                                                }
                                            } else {
                                                new Message("Error!", MealDetail.this).showErrorToast();
                                                finish();
                                            }
                                        } else {
                                            new Message("Error!", MealDetail.this).showErrorToast();
                                            finish();
                                        }
                                    } else {
                                        if (dbHelper.deleteMeal(MealID, userID)) {
                                            new Message(tvMealName.getText().toString() + " completely deleted", MealDetail.this).showCompleteToast();
                                            finish();
                                        } else {
                                            new Message("Error!", MealDetail.this).showErrorToast();
                                            finish();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = dbHelper.selectUserMealData(MealID,userID);
                if (cursor != null && cursor.moveToFirst()){
                    boolean check = dbHelper.deleteUserMeal(MealID,userID);
                    if (check) {
                        Cursor ingredient = dbHelper.selectMealIngredient(MealID);
                        if (ingredient != null && ingredient.moveToFirst()){
                            do{
                                int id = ingredient.getInt(ingredient.getColumnIndexOrThrow("IngredientID"));
                                boolean check2 = dbHelper.deleteGrocery(id,userID);
                                if (!check2){
                                    new Message("Error!", MealDetail.this).showErrorToast();
                                    break;
                                }
                            }while (ingredient.moveToNext());
                        }
                        ibAdd.setImageResource(R.drawable.add_big);
                        mealPhoto.setImageAlpha(255);
                    }else {
                        new Message("Error!", MealDetail.this).showErrorToast();
                    }
                } else {
                    if(dbHelper.insertUserMeal(MealID,userID)){
                        Cursor ingredient = dbHelper.selectMealIngredient(MealID);
                        if (ingredient != null && ingredient.moveToFirst()){
                            do{
                                int id = ingredient.getInt(ingredient.getColumnIndexOrThrow("IngredientID"));
                                boolean check = dbHelper.insertGrocery(id,userID);
                                if (!check){
                                    new Message("Meal Selection Failed!", MealDetail.this).showErrorToast();
                                    break;
                                }
                            }while (ingredient.moveToNext());
                        }
                        ibAdd.setImageResource(R.drawable.remove_big);
                        mealPhoto.setImageAlpha(80);
                    } else {
                        new Message("Meal Selection Failed!", MealDetail.this).showErrorToast();
                    }
                }
            }
        });
    }

    //Add Preparation Data to Preparation Array List
    private void setPreparationData(){
        Cursor cursor = dbHelper.selectMealPreparation(MealID);
        if(cursor != null && cursor.moveToFirst()){
            do {
                String preparation = cursor.getString(cursor.getColumnIndexOrThrow("PreparationDetail"));
                preparationDataArrayList.add(preparation);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Preparation Adapter
    private void setPreparationView() {
        preparationAdapter = new PreparationAdapter(this, preparationDataArrayList);
        recyclerViewPreparation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPreparation.setAdapter(preparationAdapter);
    }

    //add Ingredient Data to Ingredient Array List
    private void setIngredientData(){
        Cursor cursor = dbHelper.selectMealIngredient(MealID);
        if(cursor != null && cursor.moveToFirst()){
            do {
                String IngredientName = cursor.getString(cursor.getColumnIndexOrThrow("IngredientName"));
                String Amount = cursor.getString(cursor.getColumnIndexOrThrow("Amount"));
                ingredientDataArrayList.add(new IngredientData(IngredientName, Amount,0));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Ingredient Adapter
    private void setIngredientView() {
        ingredientAdapter = new IngredientAdapter(this, ingredientDataArrayList);
        recyclerViewIngredient.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewIngredient.setAdapter(ingredientAdapter);
    }
}
