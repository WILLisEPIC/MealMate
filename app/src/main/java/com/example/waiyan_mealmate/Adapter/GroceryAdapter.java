package com.example.waiyan_mealmate.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.DataHolder.Ingredient;
import com.example.waiyan_mealmate.DataHolder.IngredientData;
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    Context context;
    ArrayList<Ingredient> ingredientArrayList;
    DBHelper dbHelper;

    public GroceryAdapter(Context context , ArrayList<Ingredient> ingredientArrayList) {
        this.context = context;
        this.ingredientArrayList = ingredientArrayList;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public GroceryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grocery_list, parent, false);
        return new GroceryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.ViewHolder holder, int position) {
        holder.IngredientName.setText(ingredientArrayList.get(position).getName());
        holder.Amount.setText(String.valueOf(ingredientArrayList.get(position).getAmount()));
        holder.ID.setText(String.valueOf(ingredientArrayList.get(position).getId()));
        if (ingredientArrayList.get(position).getStatus() == 0){
            holder.checkBox.setChecked(false);
            holder.IngredientName.setPaintFlags(holder.IngredientName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.Amount.setPaintFlags(holder.Amount.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            holder.checkBox.setChecked(true);
            holder.IngredientName.setPaintFlags(holder.IngredientName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.Amount.setPaintFlags(holder.Amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return ingredientArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView IngredientName, Amount, ID;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IngredientName = itemView.findViewById(R.id.IngredientName);
            Amount = itemView.findViewById(R.id.IngredientAmount);
            ID = itemView.findViewById(R.id.ID);
            checkBox = itemView.findViewById(R.id.IngredientStatus);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int IngredientID = Integer.parseInt(ID.getText().toString());
                    boolean update = dbHelper.updateUserIngredientStatus(IngredientID, isChecked ? 1 : 0);
                    if (isChecked && update){
                        IngredientName.setPaintFlags(IngredientName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        Amount.setPaintFlags(Amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        IngredientName.setPaintFlags(IngredientName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        Amount.setPaintFlags(Amount.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });
        }
    }
}
