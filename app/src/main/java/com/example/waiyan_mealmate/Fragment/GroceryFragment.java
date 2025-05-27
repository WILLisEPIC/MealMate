package com.example.waiyan_mealmate.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.Adapter.GroceryAdapter;
import com.example.waiyan_mealmate.Adapter.MealAdapter;
import com.example.waiyan_mealmate.DataHolder.Ingredient;
import com.example.waiyan_mealmate.DataHolder.IngredientData;
import com.example.waiyan_mealmate.DataHolder.MealData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MealMate;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class GroceryFragment extends Fragment {

    DBHelper dbHelper;
    int userId;
    MealMate mealMate;
    ImageButton share;
    TextView none;
    ArrayList<String> TypeArrayList = new ArrayList<>();
    String message;
    private boolean isDialogVisible = false;
    //TextView
    TextView dairyTXT, vegetableTXT, proteinTXT, grainTXT, oilTXT, spiceTXT, stockTXT, sweetenerTXT,
            condimentTXT, herbTXT, nutTXT, fruitTXT, liquidTXT, beverageTXT, otherTXT;

    //RecyclerView
    RecyclerView dairyRecyclerView, vegetableRecyclerView, proteinRecyclerView, grainRecyclerView, oilRecyclerView,
            spiceRecyclerView, stockRecyclerView, sweetenerRecyclerView, condimentRecyclerView, herbRecyclerView,
            nutRecyclerView, fruitRecyclerView, liquidRecyclerView, beverageRecyclerView, otherRecyclerView;

    //ArrayLists
    ArrayList<Ingredient> dairyArray = new ArrayList<>();
    ArrayList<Ingredient> vegetableArray = new ArrayList<>();
    ArrayList<Ingredient> proteinArray = new ArrayList<>();
    ArrayList<Ingredient> grainArray = new ArrayList<>();
    ArrayList<Ingredient> oilArray = new ArrayList<>();
    ArrayList<Ingredient> spiceArray = new ArrayList<>();
    ArrayList<Ingredient> stockArray = new ArrayList<>();
    ArrayList<Ingredient> sweetenerArray = new ArrayList<>();
    ArrayList<Ingredient> condimentArray = new ArrayList<>();
    ArrayList<Ingredient> herbArray = new ArrayList<>();
    ArrayList<Ingredient> nutArray = new ArrayList<>();
    ArrayList<Ingredient> fruitArray = new ArrayList<>();
    ArrayList<Ingredient> liquidArray = new ArrayList<>();
    ArrayList<Ingredient> beverageArray = new ArrayList<>();
    ArrayList<Ingredient> otherArray = new ArrayList<>();

    //Adapter
    GroceryAdapter dairyAdapter, vegetableAdapter, proteinAdapter,
            grainAdapter, oilAdapter, spiceAdapter, stockAdapter, sweetenerAdapter,
            condimentAdapter, herbAdapter, nutAdapter, fruitAdapter, liquidAdapter, beverageAdapter,
            otherAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grocery_fragment,container,false);
        //retrieve UserID from MealMate
        mealMate = (MealMate) requireActivity().getApplication();
        userId = mealMate.getUserId();
        dbHelper = new DBHelper(getContext());

        //TextView initializing
        dairyTXT = view.findViewById(R.id.txtDairy);
        vegetableTXT = view.findViewById(R.id.txtVegetable);
        proteinTXT = view.findViewById(R.id.txtProtein);
        grainTXT = view.findViewById(R.id.txtGrain);
        oilTXT = view.findViewById(R.id.txtOil);
        spiceTXT = view.findViewById(R.id.txtSpice);
        stockTXT = view.findViewById(R.id.txtStock);
        sweetenerTXT = view.findViewById(R.id.txtSweetener);
        condimentTXT = view.findViewById(R.id.txtCondiment);
        herbTXT = view.findViewById(R.id.txtHerb);
        nutTXT = view.findViewById(R.id.txtNut);
        fruitTXT = view.findViewById(R.id.txtFruit);
        liquidTXT = view.findViewById(R.id.txtLiquid);
        beverageTXT = view.findViewById(R.id.txtBeverage);
        otherTXT = view.findViewById(R.id.txtOther);

        //RecyclerView Initializing
        dairyRecyclerView = view.findViewById(R.id.dairy_recycler);
        vegetableRecyclerView = view.findViewById(R.id.vegetable_recycler);
        proteinRecyclerView = view.findViewById(R.id.protein_recycler);
        grainRecyclerView = view.findViewById(R.id.grain_recycler);
        oilRecyclerView = view.findViewById(R.id.oil_recycler);
        spiceRecyclerView = view.findViewById(R.id.spice_recycler);
        stockRecyclerView = view.findViewById(R.id.stock_recycler);
        sweetenerRecyclerView = view.findViewById(R.id.sweetener_recycler);
        condimentRecyclerView = view.findViewById(R.id.condiment_recycler);
        herbRecyclerView = view.findViewById(R.id.herb_recycler);
        nutRecyclerView = view.findViewById(R.id.nut_recycler);
        fruitRecyclerView = view.findViewById(R.id.fruit_recycler);
        liquidRecyclerView = view.findViewById(R.id.liquid_recycler);
        beverageRecyclerView = view.findViewById(R.id.beverage_recycler);
        otherRecyclerView = view.findViewById(R.id.other_recycler);

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

                    //if the user is shaking the phone, pop up dialog will appear
                    if (sum > 14  && !isDialogVisible){
                        if (message == null){
                            showErrorToast("The grocery list is empty!");
                        } else {
                            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 202);
                            } else {
                                isDialogVisible = true;
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                View view = LayoutInflater.from(getContext()).inflate(R.layout.phone_number, null);
                                EditText ph = view.findViewById(R.id.number);
                                builder.setTitle("Send Grocery List")
                                        .setView(view)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                isDialogVisible = false;
                                                String phone = ph.getText().toString();
                                                //Send grocery list to the inputted phone number
                                                Intent sms = new Intent(Intent.ACTION_SENDTO);
                                                sms.setData(Uri.parse("smsto:" + phone));
                                                sms.putExtra("sms_body", message);
                                                Log.d("message", message);
                                                startActivity(sms);
                                                }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                isDialogVisible = false;
                                                dialog.dismiss();
                                            }
                                        });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
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

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 202);
                } else {
                    if (message == null) {
                        showErrorToast("The grocery list is empty!");
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.phone_number, null);
                        EditText ph = view.findViewById(R.id.number);
                        builder.setTitle("Send Grocery List")
                                .setView(view)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String phone = ph.getText().toString();
                                        //Send grocery list to the inputted phone number
                                        Intent sms = new Intent(Intent.ACTION_SENDTO);
                                        sms.setData(Uri.parse("smsto:" + phone));
                                        sms.putExtra("sms_body", message);
                                        startActivity(sms);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });
        clearData();
        SelectIngredientType();
        AddIngredientData();
        SettingRecyclerViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearData();
        SelectIngredientType();
        AddIngredientData();
        SettingRecyclerViews();
    }

    private void SelectIngredientType() {
        Cursor cursor = dbHelper.selectUserIngredient(userId);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int IngredientID = cursor.getInt(cursor.getColumnIndexOrThrow("IngredientID"));
                        Cursor select = dbHelper.selectIngredient(IngredientID);
                        if (select != null) {
                            try {
                                if (select.moveToFirst()) {
                                    String Type = select.getString(select.getColumnIndexOrThrow("Type"));
                                    if (!TypeArrayList.contains(Type)) {
                                        TypeArrayList.add(Type);
                                    }
                                }
                            } finally {
                                select.close();
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void AddIngredientData(){
        if (!TypeArrayList.isEmpty()) {
            message = "Grocery List";
            for (int i = 0; i < TypeArrayList.size(); i++) {
                String type = TypeArrayList.get(i);
                message += "\n\n(" + type + ")";
                Cursor userIngredient = dbHelper.selectUserIngredient(userId);
                if (userIngredient != null) {
                    try {
                        if (userIngredient.moveToFirst()) {
                            do {
                                int ID = userIngredient.getInt(userIngredient.getColumnIndexOrThrow("IngredientID"));
                                int Status = userIngredient.getInt(userIngredient.getColumnIndexOrThrow("Status"));
                                Cursor ingredient = dbHelper.selectIngredientByType(ID, type);
                                if (ingredient != null) {
                                    try {
                                        if (ingredient.moveToFirst()) {
                                            do {
                                                String IngredientName = ingredient.getString(ingredient.getColumnIndexOrThrow("IngredientName"));
                                                String IngredientAmount = ingredient.getString(ingredient.getColumnIndexOrThrow("Amount"));
                                                message += "\n" + IngredientName + "\t -> \t" + IngredientAmount;
                                                if (type.equals("Dairy")) {
                                                    dairyArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Vegetable")) {
                                                    vegetableArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Protein")) {
                                                    proteinArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Grain")) {
                                                    grainArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Oil")) {
                                                    oilArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Spice")) {
                                                    spiceArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Stock")) {
                                                    stockArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Sweetener")) {
                                                    sweetenerArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Condiment")) {
                                                    condimentArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Herb")) {
                                                    herbArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Nut")) {
                                                    nutArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Fruit")) {
                                                    fruitArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Liquid")) {
                                                    liquidArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else if (type.equals("Beverage")) {
                                                    beverageArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                } else {
                                                    otherArray.add(new Ingredient(IngredientName, IngredientAmount, ID, Status));
                                                }
                                            } while (ingredient.moveToNext());
                                        }
                                    } finally {
                                        ingredient.close();
                                    }
                                }
                            } while (userIngredient.moveToNext());
                        }
                    } finally {
                        userIngredient.close();
                    }
                }
            }
        } else {
            none.setVisibility(View.VISIBLE);
        }
    }

    private void clearData() {
        TypeArrayList.clear();
        dairyArray.clear();
        vegetableArray.clear();
        proteinArray.clear();
        grainArray.clear();
        oilArray.clear();
        spiceArray.clear();
        stockArray.clear();
        sweetenerArray.clear();
        condimentArray.clear();
        herbArray.clear();
        nutArray.clear();
        fruitArray.clear();
        liquidArray.clear();
        beverageArray.clear();
        otherArray.clear();
    }

    private void SettingRecyclerViews(){
        if (!dairyArray.isEmpty()) {
            dairyRecyclerView.setVisibility(View.VISIBLE);
            dairyTXT.setVisibility(View.VISIBLE);
            dairyAdapter = new GroceryAdapter(getContext(), dairyArray);
            dairyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            dairyRecyclerView.setAdapter(dairyAdapter);
        }
        if (!vegetableArray.isEmpty()) {
            vegetableRecyclerView.setVisibility(View.VISIBLE);
            vegetableTXT.setVisibility(View.VISIBLE);
            vegetableAdapter = new GroceryAdapter(getContext(), vegetableArray);
            vegetableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            vegetableRecyclerView.setAdapter(vegetableAdapter);
        }
        if (!proteinArray.isEmpty()) {
            proteinRecyclerView.setVisibility(View.VISIBLE);
            proteinTXT.setVisibility(View.VISIBLE);
            proteinAdapter = new GroceryAdapter(getContext(), proteinArray);
            proteinRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            proteinRecyclerView.setAdapter(proteinAdapter);
        }
        if (!grainArray.isEmpty()) {
            grainRecyclerView.setVisibility(View.VISIBLE);
            grainTXT.setVisibility(View.VISIBLE);
            grainAdapter = new GroceryAdapter(getContext(), grainArray);
            grainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            grainRecyclerView.setAdapter(grainAdapter);
        }
        if (!oilArray.isEmpty()) {
            oilRecyclerView.setVisibility(View.VISIBLE);
            oilTXT.setVisibility(View.VISIBLE);
            oilAdapter = new GroceryAdapter(getContext(), oilArray);
            oilRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            oilRecyclerView.setAdapter(oilAdapter);
        }
        if (!spiceArray.isEmpty()) {
            spiceRecyclerView.setVisibility(View.VISIBLE);
            spiceTXT.setVisibility(View.VISIBLE);
            spiceAdapter = new GroceryAdapter(getContext(), spiceArray);
            spiceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            spiceRecyclerView.setAdapter(spiceAdapter);
        }
        if (!stockArray.isEmpty()) {
            stockRecyclerView.setVisibility(View.VISIBLE);
            stockTXT.setVisibility(View.VISIBLE);
            stockAdapter = new GroceryAdapter(getContext(), stockArray);
            stockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            stockRecyclerView.setAdapter(stockAdapter);
        }
        if (!sweetenerArray.isEmpty()) {
            sweetenerRecyclerView.setVisibility(View.VISIBLE);
            sweetenerTXT.setVisibility(View.VISIBLE);
            sweetenerAdapter = new GroceryAdapter(getContext(), sweetenerArray);
            sweetenerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            sweetenerRecyclerView.setAdapter(sweetenerAdapter);
        }
        if (!condimentArray.isEmpty()) {
            condimentRecyclerView.setVisibility(View.VISIBLE);
            condimentTXT.setVisibility(View.VISIBLE);
            condimentAdapter = new GroceryAdapter(getContext(), condimentArray);
            condimentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            condimentRecyclerView.setAdapter(condimentAdapter);
        }
        if (!herbArray.isEmpty()) {
            herbRecyclerView.setVisibility(View.VISIBLE);
            herbTXT.setVisibility(View.VISIBLE);
            herbAdapter = new GroceryAdapter(getContext(), herbArray);
            herbRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            herbRecyclerView.setAdapter(herbAdapter);
        }
        if (!nutArray.isEmpty()) {
            nutRecyclerView.setVisibility(View.VISIBLE);
            nutTXT.setVisibility(View.VISIBLE);
            nutAdapter = new GroceryAdapter(getContext(), nutArray);
            nutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            nutRecyclerView.setAdapter(nutAdapter);
        }
        if (!fruitArray.isEmpty()) {
            fruitRecyclerView.setVisibility(View.VISIBLE);
            fruitTXT.setVisibility(View.VISIBLE);
            fruitAdapter = new GroceryAdapter(getContext(), fruitArray);
            fruitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            fruitRecyclerView.setAdapter(fruitAdapter);
        }
        if (!liquidArray.isEmpty()) {
            liquidRecyclerView.setVisibility(View.VISIBLE);
            liquidTXT.setVisibility(View.VISIBLE);
            liquidAdapter = new GroceryAdapter(getContext(), liquidArray);
            liquidRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            liquidRecyclerView.setAdapter(liquidAdapter);
        }
        if (!beverageArray.isEmpty()) {
            beverageRecyclerView.setVisibility(View.VISIBLE);
            beverageTXT.setVisibility(View.VISIBLE);
            beverageAdapter = new GroceryAdapter(getContext(), beverageArray);
            beverageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            beverageRecyclerView.setAdapter(beverageAdapter);
        }
        if (!otherArray.isEmpty()) {
            otherRecyclerView.setVisibility(View.VISIBLE);
            otherTXT.setVisibility(View.VISIBLE);
            otherAdapter = new GroceryAdapter(getContext(), otherArray);
            otherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            otherRecyclerView.setAdapter(otherAdapter);
        }
    }

    private void showErrorToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.error_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.error_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(getContext());
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void showCompleteToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.complete_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.complete_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(getContext());
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
