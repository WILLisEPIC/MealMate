package com.example.waiyan_mealmate.Fragment;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.Adapter.GroceryAdapter;
import com.example.waiyan_mealmate.DataHolder.Ingredient;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.Message;
import com.example.waiyan_mealmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GroceryFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DBHelper dbHelper;
    private ImageButton share;
    private TextView none;
    private ArrayList<String> typeArrayList = new ArrayList<>();
    private String message;
    private String userId;
    private RecyclerView groceryRecyclerView;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private GroceryAdapter groceryAdapter;
    private ActivityResultLauncher<String> activityResultLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grocery_fragment,container,false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null){
            userId = user.getUid();
        }
        dbHelper = new DBHelper(getContext());

        groceryRecyclerView = view.findViewById(R.id.grocery_recycler);
        share = view.findViewById(R.id.share_ingredient);
        none = view.findViewById(R.id.none);
        none.setVisibility(View.INVISIBLE);

        //Shake Event
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorShake = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event != null){
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    float sum = Math.abs(x) + Math.abs(y) + Math.abs(z);

                    //if the user is shaking the phone, the following function will execute
                    if (sum > 14){
                        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            activityResultLauncher.launch(android.Manifest.permission.SEND_SMS);
                        } else {
                            if (message == null){
                                new Message("The grocery list is empty!", getContext()).showErrorToast();
                            } else {
                                sendDataOrCopy(message);
                            }
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, sensorShake, SensorManager.SENSOR_DELAY_NORMAL);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                success ->{
                    if (!success){
                        new Message("SMS access denied", getContext()).showErrorToast();
                    }
                });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(android.Manifest.permission.SEND_SMS);
                } else {
                    if (message == null) {
                        new Message("The grocery list is empty!", getContext()).showErrorToast();
                    } else {
                        sendDataOrCopy(message);
                    }
                }
            }
        });
        SelectIngredient();
        SettingRecyclerViews();

        return view;
    }

    private void SelectIngredient() {
        clearData();
        Cursor grocery = dbHelper.selectGrocery(userId);
        if (grocery != null) {
            try {
                if (grocery.moveToFirst()) {
                    do {
                        int IngredientID = grocery.getInt(grocery.getColumnIndexOrThrow("IngredientID"));
                        int Status = grocery.getInt(grocery.getColumnIndexOrThrow("Status"));
                        Cursor ingredient = dbHelper.selectIngredient(IngredientID);
                        if (ingredient != null) {
                            try {
                                if (ingredient.moveToFirst()) {
                                    do {
                                        String IngredientName = ingredient.getString(ingredient.getColumnIndexOrThrow("IngredientName"));
                                        String IngredientAmount = ingredient.getString(ingredient.getColumnIndexOrThrow("Amount"));
                                        String IngredientType = ingredient.getString(ingredient.getColumnIndexOrThrow("Type"));
                                        if (!typeArrayList.contains(IngredientType)) {
                                            message += IngredientType + "\n";
                                            typeArrayList.add(IngredientType);
                                            ingredients.add(new Ingredient(IngredientType, IngredientType));
                                        }
                                        message += IngredientName + " -> " + IngredientAmount + "\n";
                                        ingredients.add(new Ingredient(IngredientName, IngredientAmount, IngredientID, IngredientType, Status));
                                    }while (ingredient.moveToNext());
                                }
                            } finally {
                                ingredient.close();
                            }
                        }
                    } while (grocery.moveToNext());
                }
            } finally {
                grocery.close();
            }
        } else {
            none.setVisibility(View.VISIBLE);
        }
    }

    private void clearData() {
        message = "";
        typeArrayList.clear();
        ingredients.clear();
    }

    private void SettingRecyclerViews(){
        if (!ingredients.isEmpty()) {
            groceryRecyclerView.setVisibility(View.VISIBLE);
            ingredients.sort(Comparator.comparing(Ingredient::getType));
            groceryAdapter = new GroceryAdapter(getContext(), ingredients, typeArrayList);
            groceryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            groceryRecyclerView.setAdapter(groceryAdapter);
        }
    }

    private void sendDataOrCopy(String data){
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, data);
            String title = "Share grocery lists";
            Intent chooser = Intent.createChooser(intent, title);
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(chooser);
            }
        }catch (ActivityNotFoundException e) {
            ClipData clip = ClipData.newPlainText("Grocery List", data);
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(clip);
            new Message("Grocery List copied to the clipboard", getContext()).showCompleteToast();
        }
    }
}
