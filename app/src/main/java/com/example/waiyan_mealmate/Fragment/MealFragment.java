package com.example.waiyan_mealmate.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waiyan_mealmate.Adapter.UserMealAdapter;
import com.example.waiyan_mealmate.ChooseMeal;
import com.example.waiyan_mealmate.DataHolder.UserMealData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MealMate;
import com.example.waiyan_mealmate.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MealFragment extends Fragment {

    int userId;
    MealMate mealMate;
    Button startPlan, nextPlan;
    RecyclerView Usermeal;
    DBHelper dbHelper;
    ArrayList<UserMealData> userMealData = new ArrayList<>();
    UserMealAdapter userMealAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meal_fragment,container,false);
        dbHelper = new DBHelper(getContext());
        Usermeal = view.findViewById(R.id.recyclerview);

        //retrieve UserID from MealMate
        mealMate = (MealMate) requireActivity().getApplication();
        userId = mealMate.getUserId();

        startPlan = view.findViewById(R.id.startplan);
        startPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChooseMeal.class));
                getActivity().finish();
            }
        });

        nextPlan = view.findViewById(R.id.startnextplan);
        nextPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChooseMeal.class));
                getActivity().finish();
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
        Cursor cursor = dbHelper.selectAllUserMealData(userId);
        if(cursor != null && cursor.moveToFirst()){
            startPlan.setVisibility(View.INVISIBLE);
            nextPlan.setVisibility(View.VISIBLE);
            do {
                int MealID = cursor.getInt(cursor.getColumnIndexOrThrow("MealID"));
                int Status = cursor.getInt(cursor.getColumnIndexOrThrow("Status"));
                Cursor cursor1 = dbHelper.selectMealData(MealID);
                if(cursor1 != null && cursor1.moveToFirst()) {
                    do{
                        int ID = cursor1.getInt(cursor1.getColumnIndexOrThrow("UserID"));
                        if(ID == userId){
                            String MealName = cursor1.getString(cursor1.getColumnIndexOrThrow("MealName"));
                            byte[] photo = cursor1.getBlob(cursor1.getColumnIndexOrThrow("Photo"));
                            userMealData.add(new UserMealData(MealID, MealName, photo,0, Status));
                        }else {
                            String MealName = cursor1.getString(cursor1.getColumnIndexOrThrow("MealName"));
                            int Photo = cursor1.getInt(cursor1.getColumnIndexOrThrow("Photo"));
                            userMealData.add(new UserMealData(MealID, MealName, null,Photo, Status));
                        }
                    }while (cursor1.moveToNext());
                    cursor1.close();
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            startPlan.setVisibility(View.VISIBLE);
            nextPlan.setVisibility(View.INVISIBLE);
        }
    }

    //set UserMeal Adapter
    private void setUserMealView() {
        userMealAdapter = new UserMealAdapter(getContext(), userMealData, userId);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        Usermeal.setLayoutManager(gridLayoutManager);
        Usermeal.setAdapter(userMealAdapter);
    }
}
