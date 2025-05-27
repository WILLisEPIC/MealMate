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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.DataHolder.NewMeal;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MealDetail;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class AddMealAdapter extends RecyclerView.Adapter<AddMealAdapter.ViewHolder> {

    Context context;
    ArrayList<NewMeal> MealDataArrayList;
    DBHelper dbHelper;
    int userID;

    public AddMealAdapter(Context context, ArrayList<NewMeal> MealDataArrayList, int userID) {
        this.context = context;
        this.MealDataArrayList = MealDataArrayList;
        this.userID = userID;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public AddMealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.meal_layout, parent, false);
        return new AddMealAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddMealAdapter.ViewHolder holder, int position) {
        holder.MealID.setText(String.valueOf(MealDataArrayList.get(position).getMealId()));
        byte[] photo = MealDataArrayList.get(position).getMealPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        holder.mealPhoto.setImageBitmap(bitmap);
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
                NewMeal clickedMeal = MealDataArrayList.get(position);
                int mealId = clickedMeal.getMealId();
                if (v.getId() == R.id.addmeal) {
                    Cursor cursor = dbHelper.selectUserMealData(mealId,userID);
                    if (cursor != null && cursor.moveToFirst()){
                        boolean check = dbHelper.deleteUserMeal(mealId,userID);
                        if (check) {
                            Cursor ingredient = dbHelper.selectMealIngredient(mealId);
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
                            addORremove.setImageResource(R.drawable.add_circle);
                            mealPhoto.setImageAlpha(255);
                        }else {
                            showErrorToast("Error!");
                        }
                    } else {
                        if(dbHelper.insertUserMeal(mealId,userID)){
                            Cursor ingredient = dbHelper.selectMealIngredient(mealId);
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
                            addORremove.setImageResource(R.drawable.remove_circle);
                            mealPhoto.setImageAlpha(80);
                        } else {
                            showErrorToast("Meal Selection Failed!");
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

    private void showErrorToast(String message) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.error_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.error_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void showCompleteToast(String message) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.complete_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.complete_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
