package com.example.waiyan_mealmate.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waiyan_mealmate.Adapter.UserMealAdapter;
import com.example.waiyan_mealmate.ChooseMeal;
import com.example.waiyan_mealmate.DataHolder.UserMealData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MealFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private Button startPlan, nextPlan;
    private RecyclerView UserMeal;
    private DBHelper dbHelper;
    private ArrayList<UserMealData> userMealData = new ArrayList<>();
    private UserMealAdapter userMealAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meal_fragment,container,false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        dbHelper = new DBHelper(getContext());
        UserMeal = view.findViewById(R.id.recyclerview);

        startPlan = view.findViewById(R.id.startplan);
        startPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChooseMeal.class));
                if(getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        nextPlan = view.findViewById(R.id.startnextplan);
        nextPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChooseMeal.class));
                if(getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        startPlan.setVisibility(View.VISIBLE);
        nextPlan.setVisibility(View.INVISIBLE);

        userMealData.clear();
        setUserMealData();
        setUserMealView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        userMealData.clear();
        setUserMealData();
        setUserMealView();
    }

    //add UserMeal Data to UserMeal Array List
    private void setUserMealData(){
        Cursor userMeals = dbHelper.selectAllUserMealData(userId);
        if(userMeals != null && userMeals.moveToFirst()){
            startPlan.setVisibility(View.INVISIBLE);
            nextPlan.setVisibility(View.VISIBLE);
            do {
                int MealID = userMeals.getInt(userMeals.getColumnIndexOrThrow("MealID"));
                int Status = userMeals.getInt(userMeals.getColumnIndexOrThrow("Status"));
                Cursor meals = dbHelper.selectMealData(MealID);
                if(meals != null && meals.moveToFirst()) {
                    do{
                        String userID = meals.getString(meals.getColumnIndexOrThrow("UserID"));
                        String MealName = meals.getString(meals.getColumnIndexOrThrow("MealName"));
                        if (userID != null){
                            byte[] photo = meals.getBlob(meals.getColumnIndexOrThrow("Photo"));
                            userMealData.add(new UserMealData(MealID, MealName, photo, Status));
                        } else {
                            int Photo = meals.getInt(meals.getColumnIndexOrThrow("PhotoPos"));
                            userMealData.add(new UserMealData(MealID, MealName,Photo, Status));
                        }
                    }while (meals.moveToNext());
                    meals.close();
                }
            } while (userMeals.moveToNext());
            userMeals.close();
        } else {
            startPlan.setVisibility(View.VISIBLE);
            nextPlan.setVisibility(View.INVISIBLE);
        }
    }

    //set UserMeal Adapter
    private void setUserMealView() {
        userMealAdapter = new UserMealAdapter(getContext(), userMealData, userId);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        UserMeal.setLayoutManager(gridLayoutManager);
        UserMeal.setAdapter(userMealAdapter);
    }
}
