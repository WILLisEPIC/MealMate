package com.example.waiyan_mealmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.waiyan_mealmate.DataHolder.MealData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MealDetail;
import com.example.waiyan_mealmate.Message;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MealData> MealDataArrayList;
    private DBHelper dbHelper;
    private String userID;

    public MealAdapter(Context context, ArrayList<MealData> MealDataArrayList, String userID) {
        this.context = context;
        this.MealDataArrayList = MealDataArrayList;
        this.userID = userID;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public MealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.meal_layout, parent, false);
        return new MealAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealAdapter.ViewHolder holder, int position) {
        holder.MealID.setText(String.valueOf(MealDataArrayList.get(position).getMealId()));
        if (MealDataArrayList.get(position).getMealPhotoId() != 0){
            holder.mealPhoto.setImageResource(MealDataArrayList.get(position).getMealPhotoId());
        } else {
            byte[] photo = MealDataArrayList.get(position).getMealPhotoByte();
            if (photo == null){
                holder.mealPhoto.setImageResource(R.drawable.login_logo);
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(photo)
                        .placeholder(R.drawable.login_logo)
                        .error(R.drawable.login_logo)
                        .into(holder.mealPhoto);
            }
        }
        holder.MealName.setText(MealDataArrayList.get(position).getMealName());

        Cursor cursor = dbHelper.selectUserMealData(MealDataArrayList.get(position).getMealId(), userID);
        if (cursor != null && cursor.moveToFirst()){
            holder.addORremove.setImageResource(R.drawable.remove_circle);
            holder.mealPhoto.setImageAlpha(80);
        }else {
            holder.addORremove.setImageResource(R.drawable.add_circle);
            holder.mealPhoto.setImageAlpha(255);
        }
    }

    @Override
    public int getItemCount() {
        return MealDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView MealID, MealName;
        ImageButton addORremove;
        ImageView mealPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MealID = itemView.findViewById(R.id.MealID);
            MealName = itemView.findViewById(R.id.meal_name);
            addORremove = itemView.findViewById(R.id.addmeal);
            mealPhoto = itemView.findViewById(R.id.meal_photo);
            itemView.setOnClickListener(this);
            addORremove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MealData clickedMeal = MealDataArrayList.get(position);
                int mealId = clickedMeal.getMealId();
                if (v.getId() == R.id.addmeal) {
                    Cursor cursor = dbHelper.selectUserMealData(mealId, userID);
                    if (cursor != null && cursor.moveToFirst()){
                        boolean check = dbHelper.deleteUserMeal(mealId, userID);
                        if (check) {
                            Cursor ingredient = dbHelper.selectMealIngredient(mealId);
                            if (ingredient != null && ingredient.moveToFirst()){
                                do{
                                    int id = ingredient.getInt(ingredient.getColumnIndexOrThrow("IngredientID"));
                                    boolean check2 = dbHelper.deleteGrocery(id, userID);
                                    if (!check2){
                                        new Message("Error!", context).showErrorToast();
                                        break;
                                    }
                                }while (ingredient.moveToNext());
                            }
                            addORremove.setImageResource(R.drawable.add_circle);
                            mealPhoto.setImageAlpha(255);
                        }else {
                            new Message("Error!", context).showErrorToast();
                        }
                    } else {
                        if(dbHelper.insertUserMeal(mealId,userID)){
                            Cursor ingredient = dbHelper.selectMealIngredient(mealId);
                            if (ingredient != null && ingredient.moveToFirst()){
                                do{
                                    int id = ingredient.getInt(ingredient.getColumnIndexOrThrow("IngredientID"));
                                    boolean check = dbHelper.insertGrocery(id,userID);
                                    if (!check){
                                        new Message("Meal Selection Failed!", context).showErrorToast();
                                        break;
                                    }
                                }while (ingredient.moveToNext());
                            }
                            addORremove.setImageResource(R.drawable.remove_circle);
                            mealPhoto.setImageAlpha(80);
                        } else {
                            new Message("Meal Selection Failed!", context).showErrorToast();
                        }
                    }
                } else {
                    Intent intent = new Intent(context, MealDetail.class);
                    intent.putExtra("MealID", mealId);
                    context.startActivity(intent);
                }
            }
        }
    }
}
