package com.example.waiyan_mealmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.waiyan_mealmate.Database.DBHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateMeal extends AppCompatActivity {

    MealMate mealMate;
    DBHelper dbHelper;
    ImageButton back;
    EditText Name;
    ImageView Photo;
    Button AddIngredient, AddPreparation, AddMeal;
    LinearLayout ingredientcontainer, preparationcontainer;
    TextView Name_error, Photo_error;
    byte[] photo;
    private static final int PICK_IMAGE = 100;
    int count = 1;
    int userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meal);

        //retrieve UserID from MealMate
        mealMate = (MealMate) getApplication();
        userID = mealMate.getUserId();

        dbHelper = new DBHelper(this);
        back = findViewById(R.id.back);
        Name = findViewById(R.id.MealName);
        Photo = findViewById(R.id.MealPhoto);
        Name_error = findViewById(R.id.nameError);
        Photo_error = findViewById(R.id.photoError);
        AddIngredient = findViewById(R.id.add_ingredient);
        AddPreparation = findViewById(R.id.add_preparation);
        AddMeal = findViewById(R.id.confirm_button);
        ingredientcontainer = findViewById(R.id.ingredient_container);
        preparationcontainer = findViewById(R.id.preparation_container);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateMeal.this, ChooseMeal.class));
                finish();
            }
        });

        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateMeal.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,PICK_IMAGE);
                }
            }
        });

        AddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIngredientDialog();
            }
        });

        AddPreparation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPreparationDialog();
            }
        });

        AddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Name.getText().toString().trim();
                ArrayList<String[]> ingredients = getIngredientData();
                ArrayList<String> preparations = getPreparationData();
                Name_error.setVisibility(View.INVISIBLE);
                Photo_error.setVisibility(View.INVISIBLE);
                Name_error.setText(null);
                Photo_error.setText(null);
                Name.setBackgroundResource(R.drawable.edittext_design);
                if(name.isEmpty()){
                    Name.setBackgroundResource(R.drawable.edittext_error);
                    Name_error.setText("Please enter meal name");
                    Name_error.setVisibility(View.VISIBLE);
                } else if (photo == null) {
                    Photo_error.setText("Please select meal picture");
                    Photo_error.setVisibility(View.VISIBLE);
                } else if (ingredients.isEmpty()) {
                    showErrorToast("Please add at least one ingredient.");
                } else if (preparations.isEmpty()) {
                    showErrorToast("Please add at least one preparation.");
                } else {
                    long insert_meal = dbHelper.insertMealData(name,photo,"User",userID);
                    int mealID = Integer.parseInt(String.valueOf(insert_meal));
                    if (insert_meal != -1){
                        for (String[] ingredient : ingredients) {
                            String ingredientName = ingredient[0];
                            String ingredientAmount = ingredient[1];
                            boolean insert_ingredient = dbHelper.insertIngredientData(ingredientName, ingredientAmount, "Other", mealID);
                            if(!insert_ingredient){
                                showErrorToast("Failed to insert new meal!");
                                break;
                            }
                        }

                        for (String preparation : preparations) {
                            String preparationName = preparation;
                            boolean insert_preparationDetail = dbHelper.insertPreparationData(preparationName, mealID);
                            if(!insert_preparationDetail){
                                showErrorToast("Failed to insert new meal!");
                                break;
                            }
                        }
                        showCompleteToast(name + " completely added");
                        Name.setText(null);
                        Photo.setImageResource(R.drawable.select_image_icon);
                        ingredientcontainer.removeAllViews();
                        preparationcontainer.removeAllViews();
                    }
                }
            }
        });
    }

    //Show Dialog to get ingredient name and amount
    private void showAddIngredientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Ingredient");
        View view = getLayoutInflater().inflate(R.layout.add_ingredient, null);
        builder.setView(view);
        EditText name = view.findViewById(R.id.ingredient_name);
        EditText amount = view.findViewById(R.id.ingredient_amount);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String Ingredient_Name = name.getText().toString().trim();
            String Ingredient_Amount = amount.getText().toString().trim();
            if (!Ingredient_Name.isEmpty() && !Ingredient_Amount.isEmpty()) {
                addIngredientToContainer(Ingredient_Name, Ingredient_Amount);
            } else {
                showErrorToast("Please enter both ingredient name and amount!");
            }
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //add data from dialog to linearlayout as new view
    private void addIngredientToContainer(String name, String amount) {
        View ingredientCard = getLayoutInflater().inflate(R.layout.show_ingredient, null);
        TextView ingredientName = ingredientCard.findViewById(R.id.IngredientName);
        TextView ingredientAmount = ingredientCard.findViewById(R.id.IngredientAmount);
        ingredientName.setText(name);
        ingredientAmount.setText(amount);

        ingredientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateMeal.this);
                builder.setTitle("Edit Ingredient");
                View view = getLayoutInflater().inflate(R.layout.add_ingredient, null);
                builder.setView(view);
                EditText editName = view.findViewById(R.id.ingredient_name);
                EditText editAmount = view.findViewById(R.id.ingredient_amount);
                editName.setText(ingredientName.getText().toString());
                editAmount.setText(ingredientAmount.getText().toString());
                builder.setPositiveButton("Save", (dialog, which) -> {
                    String updatedName = editName.getText().toString().trim();
                    String updatedAmount = editAmount.getText().toString().trim();

                    if (!updatedName.isEmpty() && !updatedAmount.isEmpty()) {
                        ingredientName.setText(updatedName);
                        ingredientAmount.setText(updatedAmount);
                    } else {
                        showErrorToast("Please enter both ingredient name and amount!");
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ingredientCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateMeal.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ingredientcontainer.removeView(ingredientCard);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });

        ingredientcontainer.addView(ingredientCard);
    }

    //Show Dialog to get preparation
    private void showAddPreparationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Preparation");
        View view = getLayoutInflater().inflate(R.layout.add_preparation, null);
        builder.setView(view);
        EditText preparation = view.findViewById(R.id.preparation);
        builder.setPositiveButton("Add", (dialog, which) -> {
                    String Preparation_Name = preparation.getText().toString().trim();
                    if (!Preparation_Name.isEmpty()) {
                        addPreparationToContainer(Preparation_Name, count);
                        count += 1;
                    } else {
                        showErrorToast("Please enter preparation step!");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //add data from dialog to linearlayout as new view
    private void addPreparationToContainer(String preparation, int step) {
        View preparationCard = getLayoutInflater().inflate(R.layout.show_preparation, null);
        TextView preparationStep = preparationCard.findViewById(R.id.Step);
        TextView preparationName = preparationCard.findViewById(R.id.Preparation);
        preparationStep.setText(String.valueOf(step));
        preparationName.setText(preparation);

        preparationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateMeal.this);
                builder.setTitle("Edit Preparation");
                View view = getLayoutInflater().inflate(R.layout.add_preparation, null);
                builder.setView(view);
                EditText EditPreparation = view.findViewById(R.id.preparation);
                EditPreparation.setText(preparationName.getText().toString().trim());
                builder.setPositiveButton("Save", (dialog, which) -> {
                    String updatedPreparation = EditPreparation.getText().toString().trim();
                    if (!updatedPreparation.isEmpty()) {
                        preparationName.setText(updatedPreparation); // Update the TextView with new text
                    } else {
                        showErrorToast("Please enter preparation step!");
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        preparationCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateMeal.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                preparationcontainer.removeView(preparationCard);
                                count --;
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        preparationcontainer.addView(preparationCard);
    }

    //return ingredient data
    private ArrayList<String[]> getIngredientData() {
        ArrayList<String[]> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientcontainer.getChildCount(); i++) {
            View ingredientView = ingredientcontainer.getChildAt(i);
            TextView ingredientName = ingredientView.findViewById(R.id.IngredientName);
            TextView ingredientAmount = ingredientView.findViewById(R.id.IngredientAmount);

            if (ingredientName != null && ingredientAmount != null) {
                String name = ingredientName.getText().toString().trim();
                String amount = ingredientAmount.getText().toString().trim();
                ingredients.add(new String[]{name, amount});
            }
        }
        return ingredients;
    }

    //get Preparation data
    private ArrayList<String> getPreparationData(){
        ArrayList<String> preparation = new ArrayList<>();
        for (int i = 0; i < preparationcontainer.getChildCount(); i++) {
            View preparationView = preparationcontainer.getChildAt(i);
            TextView preparationDetail = preparationView.findViewById(R.id.Preparation);

            if (preparationDetail != null) {
                String name = preparationDetail.getText().toString().trim();
                preparation.add(name);
            }
        }
        return preparation;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == CreateMeal.this.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(CreateMeal.this.getContentResolver(), selectedImageUri);
                Photo.setImageBitmap(bitmap);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                photo = outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showErrorToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.error_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.error_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(CreateMeal.this);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void showCompleteToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.complete_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.complete_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(CreateMeal.this);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
