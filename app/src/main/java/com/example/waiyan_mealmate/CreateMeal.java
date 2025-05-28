package com.example.waiyan_mealmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.waiyan_mealmate.Database.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CreateMeal extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DBHelper dbHelper;
    private ImageButton ibBack;
    private EditText etMealName;
    private ImageView Photo;
    private Button btnAddIngredient, btnAddPreparation, btnAddMeal;
    private LinearLayout linearLayoutIngredient, linearLayoutPreparation;
    private TextView tvNameError, tvPhotoError;
    private byte[] photo = null;
    private ActivityResultLauncher<Intent> activity;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_meal);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();
        }
        dbHelper = new DBHelper(this);

        ibBack = findViewById(R.id.back);
        etMealName = findViewById(R.id.MealName);
        Photo = findViewById(R.id.MealPhoto);
        tvNameError = findViewById(R.id.nameError);
        tvPhotoError = findViewById(R.id.photoError);
        btnAddIngredient = findViewById(R.id.add_ingredient);
        btnAddPreparation = findViewById(R.id.add_preparation);
        btnAddMeal = findViewById(R.id.confirm_button);
        linearLayoutIngredient = findViewById(R.id.ingredient_container);
        linearLayoutPreparation = findViewById(R.id.preparation_container);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateMeal.this, ChooseMeal.class));
                finish();
            }
        });

        activity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                task -> {
                    if (task.getData() == null){
                        new Message("No photo was selected", this).showErrorToast();
                    } else if (task.getResultCode() == RESULT_OK){
                        Uri image = task.getData().getData();
                        photo = new Photo(this).stringToByteArray(image);
                        if (photo != null) {
                            Photo.setImageURI(image);
                        }else {
                            tvPhotoError.setText("Failed to upload image");
                            tvPhotoError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvPhotoError.setText("Failed to upload image");
                        tvPhotoError.setVisibility(View.VISIBLE);
                    }
                });

        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                        ? android.Manifest.permission.READ_MEDIA_IMAGES
                        : android.Manifest.permission.READ_EXTERNAL_STORAGE;

                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateMeal.this, new String[]{permission}, R.string.read_storage_request_code);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.launch(intent);
                }
            }
        });

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddIngredientDialog();
            }
        });

        btnAddPreparation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPreparationDialog();
            }
        });

        btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etMealName.getText().toString().trim();
                ArrayList<String[]> ingredients = getIngredientData();
                ArrayList<String> preparations = getPreparationData();
                tvNameError.setVisibility(View.INVISIBLE);
                tvPhotoError.setVisibility(View.INVISIBLE);
                tvNameError.setText(null);
                tvPhotoError.setText(null);
                etMealName.setBackgroundResource(R.drawable.edittext_design);
                if(name.isEmpty()){
                    etMealName.setBackgroundResource(R.drawable.edittext_error);
                    tvNameError.setText("Please enter meal name");
                    tvNameError.setVisibility(View.VISIBLE);
                } else if (ingredients.isEmpty()) {
                    new Message("Please add at least one ingredient!", getApplicationContext()).showErrorToast();
                } else if (preparations.isEmpty()) {
                    new Message("Please add at least one preparation!", getApplicationContext()).showErrorToast();
                } else {
                    long insert_meal = dbHelper.insertMealData(name,photo,"User",userID);
                    int mealID = Integer.parseInt(String.valueOf(insert_meal));
                    if (insert_meal != -1){
                        for (String[] ingredient : ingredients) {
                            String ingredientName = ingredient[0];
                            String ingredientAmount = ingredient[1];
                            boolean insert_ingredient = dbHelper.insertIngredientData(ingredientName, ingredientAmount, "Other", mealID);
                            if(!insert_ingredient){
                                new Message("Failed to insert new meal!", getApplicationContext()).showErrorToast();
                                break;
                            }
                        }

                        for (String preparation : preparations) {
                            boolean insert_preparationDetail = dbHelper.insertPreparationData(preparation, mealID);
                            if(!insert_preparationDetail){
                                new Message("Failed to insert new meal!", getApplicationContext()).showErrorToast();
                                break;
                            }
                        }
                        new Message(name + " completely added", getApplicationContext()).showCompleteToast();
                        etMealName.setText(null);
                        Photo.setImageResource(R.drawable.select_image_icon);
                        linearLayoutIngredient.removeAllViews();
                        linearLayoutPreparation.removeAllViews();
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
                new Message("Please enter all the fields!", getApplicationContext()).showErrorToast();
            }
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //add data from dialog to linearlayout as new view
    private void addIngredientToContainer(String name, String amount) {
        View ingredientCard = getLayoutInflater().inflate(R.layout.show_new_ingredient, linearLayoutIngredient, false);
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
                        new Message("Please enter both ingredient name and amount!", getApplicationContext()).showErrorToast();
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
                                linearLayoutIngredient.removeView(ingredientCard);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });

        linearLayoutIngredient.addView(ingredientCard);
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
                        addPreparationToContainer(Preparation_Name);
                    } else {
                        new Message("Please enter preparation step!", getApplicationContext()).showErrorToast();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //add data from dialog to linearlayout as new view
    private void addPreparationToContainer(String preparation) {
        View preparationCard = getLayoutInflater().inflate(R.layout.show_new_preparation, linearLayoutPreparation, false);
        TextView preparationName = preparationCard.findViewById(R.id.Preparation);
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
                        new Message("Please enter preparation step!", getApplicationContext()).showErrorToast();
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
                                linearLayoutPreparation.removeView(preparationCard);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        linearLayoutPreparation.addView(preparationCard);
    }

    //return ingredient data
    private ArrayList<String[]> getIngredientData() {
        ArrayList<String[]> ingredients = new ArrayList<>();
        for (int i = 0; i < linearLayoutIngredient.getChildCount(); i++) {
            View ingredientView = linearLayoutIngredient.getChildAt(i);
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
        for (int i = 0; i < linearLayoutPreparation.getChildCount(); i++) {
            View preparationView = linearLayoutPreparation.getChildAt(i);
            TextView preparationDetail = preparationView.findViewById(R.id.Preparation);

            if (preparationDetail != null) {
                String name = preparationDetail.getText().toString().trim();
                preparation.add(name);
            }
        }
        return preparation;
    }
}
