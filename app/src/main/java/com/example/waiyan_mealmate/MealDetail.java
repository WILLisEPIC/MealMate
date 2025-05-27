package com.example.waiyan_mealmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.Adapter.IngredientAdapter;
import com.example.waiyan_mealmate.Adapter.PreparationAdapter;
import com.example.waiyan_mealmate.DataHolder.IngredientData;
import com.example.waiyan_mealmate.Database.DBHelper;

import java.util.ArrayList;

public class MealDetail extends AppCompatActivity {

    MealMate mealMate;
    DBHelper dbHelper;
    ImageView mealPhoto;
    ImageButton back,add;
    TextView mealName;
    Button delete;
    int MealID, MealPhoto, userID;
    RecyclerView ingredient,preparation;
    IngredientAdapter ingredientAdapter;
    PreparationAdapter preparationAdapter;
    ArrayList<IngredientData> ingredientData = new ArrayList<>();
    ArrayList<String> preparationData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_detail);

        dbHelper = new DBHelper(this);

        //retrieve UserID from MealMate
        mealMate = (MealMate) getApplication();
        userID = mealMate.getUserId();

        mealPhoto = findViewById(R.id.MealPhoto);
        back = findViewById(R.id.back);
        add = findViewById(R.id.addmeal);
        mealName = findViewById(R.id.MealName);
        ingredient = findViewById(R.id.Ingredient);
        preparation = findViewById(R.id.Preparation);
        delete = findViewById(R.id.delete);

        Intent intent = getIntent();
        MealID = intent.getIntExtra("MealID", 0);

        setIngredientData();
        setIngredientView();

        setPreparationData();
        setPreparationView();

        if(MealID != 0){
            Cursor cursor = dbHelper.selectMealData(MealID);
            if (cursor != null && cursor.moveToNext()){
                int ID = cursor.getInt(cursor.getColumnIndexOrThrow("UserID"));
                if(ID == userID){
                    mealName.setText(cursor.getString(cursor.getColumnIndexOrThrow("MealName")));
                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow("Photo"));
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    mealPhoto.setImageBitmap(bitmap);
                    delete.setVisibility(View.VISIBLE);
                } else {
                    mealName.setText(cursor.getString(cursor.getColumnIndexOrThrow("MealName")));
                    MealPhoto = cursor.getInt(cursor.getColumnIndexOrThrow("Photo"));
                    mealPhoto.setImageResource(MealPhoto);
                }
                Cursor cursor1 = dbHelper.selectUserMealData(MealID, userID);
                if (cursor1 != null && cursor1.moveToFirst()){
                    add.setImageResource(R.drawable.remove_big);
                    mealPhoto.setImageAlpha(80);
                }else {
                    add.setImageResource(R.drawable.add_big);
                    mealPhoto.setImageAlpha(255);
                }
            }
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MealDetail.this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete "+ mealName.getText().toString())
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
                                                    showCompleteToast(mealName.getText().toString() + " completely deleted");
                                                    finish();
                                                } else {
                                                    showErrorToast("Error!");
                                                    finish();
                                                }
                                            } else {
                                                showErrorToast("Error!");
                                                finish();
                                            }
                                        } else {
                                            showErrorToast("Error!");
                                            finish();
                                        }
                                    } else {
                                        if (dbHelper.deleteMeal(MealID, userID)) {
                                            showCompleteToast(mealName.getText().toString() + " completely deleted");
                                            finish();
                                        } else {
                                            showErrorToast("Error!");
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
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
                                boolean check2 = dbHelper.deleteUserIngredient(id,userID);
                                if (!check2){
                                    showErrorToast("Error!");
                                    break;
                                }
                            }while (ingredient.moveToNext());
                        }
                        add.setImageResource(R.drawable.add_big);
                        mealPhoto.setImageAlpha(255);
                    }else {
                        showErrorToast("Error!");
                    }
                } else {
                    if(dbHelper.insertUserMeal(MealID,userID)){
                        Cursor ingredient = dbHelper.selectMealIngredient(MealID);
                        if (ingredient != null && ingredient.moveToFirst()){
                            do{
                                int id = ingredient.getInt(ingredient.getColumnIndexOrThrow("IngredientID"));
                                boolean check = dbHelper.insertUserIngredient(id,userID);
                                if (!check){
                                    showErrorToast("Meal Selection Failed!");
                                    break;
                                }
                            }while (ingredient.moveToNext());
                        }
                        add.setImageResource(R.drawable.remove_big);
                        mealPhoto.setImageAlpha(80);
                    } else {
                        showErrorToast("Meal Selection Failed!");
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
                preparationData.add(preparation);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Preparation Adapter
    private void setPreparationView() {
        preparationAdapter = new PreparationAdapter(this, preparationData);
        preparation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        preparation.setAdapter(preparationAdapter);
    }

    //add Ingredient Data to Ingredient Array List
    private void setIngredientData(){
        Cursor cursor = dbHelper.selectMealIngredient(MealID);
        if(cursor != null && cursor.moveToFirst()){
            do {
                String IngredientName = cursor.getString(cursor.getColumnIndexOrThrow("IngredientName"));
                String Amount = cursor.getString(cursor.getColumnIndexOrThrow("Amount"));
                ingredientData.add(new IngredientData(IngredientName, Amount,0));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    //set Ingredient Adapter
    private void setIngredientView() {
        ingredientAdapter = new IngredientAdapter(this, ingredientData);
        ingredient.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ingredient.setAdapter(ingredientAdapter);
    }

    private void showErrorToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.error_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.error_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(this);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void showCompleteToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.complete_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.complete_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(this);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
