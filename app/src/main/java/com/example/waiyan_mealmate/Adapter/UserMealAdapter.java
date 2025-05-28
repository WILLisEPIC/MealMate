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
import com.example.waiyan_mealmate.DataHolder.UserMealData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MealDetail;
import com.example.waiyan_mealmate.Message;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class UserMealAdapter extends RecyclerView.Adapter<UserMealAdapter.ViewHolder> {

    Context context;
    ArrayList<UserMealData> userMealData;
    DBHelper dbHelper;
    String userID;

    public UserMealAdapter(Context context, ArrayList<UserMealData> userMealData, String userID) {
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
        holder.tvMealID.setText(String.valueOf(userMealData.get(position).getMealId()));
        if (userMealData.get(position).getMealPhoto() == null && userMealData.get(position).getMealPhotoPos() != 0){
            int photo = userMealData.get(position).getMealPhotoPos();
            holder.ivMealPhoto.setImageResource(photo);
        } else {
            byte[] photo = userMealData.get(position).getMealPhoto();
            if (photo == null){
                holder.ivMealPhoto.setImageResource(R.drawable.login_logo);
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(photo)
                        .placeholder(R.drawable.login_logo)
                        .error(R.drawable.login_logo)
                        .into(holder.ivMealPhoto);
            }
        }
        holder.tvMealName.setText(userMealData.get(position).getMealName());

        if(userMealData.get(position).getStatus() == 0){
            holder.ibSelect.setImageResource(R.drawable.check_white);
            holder.ivMealPhoto.setImageAlpha(255);
        }else {
            holder.ibSelect.setImageResource(R.drawable.check_black);
            holder.ivMealPhoto.setImageAlpha(80);
        }

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (userMealData.size() > 4 && position == userMealData.size() - 1) {
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 320);
        } else {
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        }
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return userMealData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView tvMealID, tvMealName;
        private final ImageButton ibSelect;
        private final ImageView ivMealPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealID = itemView.findViewById(R.id.MealID);
            tvMealName = itemView.findViewById(R.id.meal_name);
            ibSelect = itemView.findViewById(R.id.select);
            ivMealPhoto = itemView.findViewById(R.id.meal_photo);
            itemView.setOnClickListener(this);
            ibSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            dbHelper = new DBHelper(context);
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                UserMealData clickedMeal = userMealData.get(position);
                int mealId = clickedMeal.getMealId();
                if (v.getId() == R.id.select) {
                    Cursor cursor = dbHelper.selectUserMealData(mealId, userID);
                    if (cursor != null && cursor.moveToFirst()){
                        int Status = cursor.getInt(cursor.getColumnIndexOrThrow("Status"));
                        if(Status == 0){
                            boolean update = dbHelper.updateUserMealStatus(mealId, userID, 1);
                            if(update){
                                ibSelect.setImageResource(R.drawable.check_black);
                                ivMealPhoto.setImageAlpha(80);
                            } else {
                                new Message("Error!", context).showErrorToast();
                            }
                        }else {
                            boolean update = dbHelper.updateUserMealStatus(mealId, userID, 0);
                            if(update){
                                ibSelect.setImageResource(R.drawable.check_white);
                                ivMealPhoto.setImageAlpha(255);
                                ivMealPhoto.setColorFilter(null);
                            } else {
                                new Message("Error!", context).showErrorToast();
                            }
                        }
                        cursor.close();
                    } else {
                        new Message("Error!", context).showErrorToast();
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
