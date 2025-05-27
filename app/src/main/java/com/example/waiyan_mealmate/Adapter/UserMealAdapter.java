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

import com.example.waiyan_mealmate.DataHolder.UserMealData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MealDetail;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class UserMealAdapter extends RecyclerView.Adapter<UserMealAdapter.ViewHolder> {

    Context context;
    ArrayList<UserMealData> userMealData;
    DBHelper dbHelper;
    int userID;

    public UserMealAdapter(Context context, ArrayList<UserMealData> userMealData, int userID) {
        this.context = context;
        this.userMealData = userMealData;
        this.userID = userID;
    }

    @NonNull
    @Override
    public UserMealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_chosen_meal, parent, false);
        return new UserMealAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMealAdapter.ViewHolder holder, int position) {
        holder.MealID.setText(String.valueOf(userMealData.get(position).getMealId()));
        if (userMealData.get(position).getMealPhoto() == null && userMealData.get(position).getMealPhotoPos() != 0){
            int photo = userMealData.get(position).getMealPhotoPos();
            holder.mealPhoto.setImageResource(photo);
        } else {
            byte[] photo = userMealData.get(position).getMealPhoto();
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            holder.mealPhoto.setImageBitmap(bitmap);
        }
        holder.MealName.setText(userMealData.get(position).getMealName());

        if(userMealData.get(position).getStatus() == 0){
            holder.select.setImageResource(R.drawable.check_white);
            holder.mealPhoto.setImageAlpha(255);
        }else {
            holder.select.setImageResource(R.drawable.check_black);
            holder.mealPhoto.setImageAlpha(80);
        }
    }

    @Override
    public int getItemCount() {
        return userMealData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView MealID, MealName;
        ImageButton select;
        ImageView mealPhoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MealID = itemView.findViewById(R.id.MealID);
            MealName = itemView.findViewById(R.id.meal_name);
            select = itemView.findViewById(R.id.select);
            mealPhoto = itemView.findViewById(R.id.meal_photo);
            itemView.setOnClickListener(this);
            select.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            dbHelper = new DBHelper(context);
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                UserMealData clickedMeal = userMealData.get(position);
                int mealId = clickedMeal.getMealId();
                if (v.getId() == R.id.select) {
                    Cursor cursor = dbHelper.selectUserMealbyMealID(mealId, userID);
                    if (cursor != null && cursor.moveToFirst()){
                        int Status = cursor.getInt(cursor.getColumnIndexOrThrow("Status"));
                        if(Status == 0){
                            boolean update = dbHelper.updateUserMealStatus(mealId, userID, 1);
                            if(update){
                                select.setImageResource(R.drawable.check_black);
                                mealPhoto.setImageAlpha(80);
                            } else {
                                showErrorToast("Error!");
                            }
                        }else {
                            boolean update = dbHelper.updateUserMealStatus(mealId, userID, 0);
                            if(update){
                                select.setImageResource(R.drawable.check_white);
                                mealPhoto.setImageAlpha(255);
                                mealPhoto.setColorFilter(null);
                            } else {
                                showErrorToast("Error!");
                            }
                        }
                        cursor.close();
                    } else {
                        showErrorToast("Error!");
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
