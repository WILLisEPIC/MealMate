package com.example.waiyan_mealmate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.DataHolder.IngredientData;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    Context context;
    ArrayList<IngredientData> ingredientArrayList;

    public IngredientAdapter(Context context , ArrayList<IngredientData> ingredientArrayList) {
        this.context = context;
        this.ingredientArrayList = ingredientArrayList;
    }

    @NonNull
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_ingredient, parent, false);
        return new IngredientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.ViewHolder holder, int position) {
        holder.IngredientName.setText(ingredientArrayList.get(position).getIngredientName());
        holder.Amount.setText(ingredientArrayList.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return ingredientArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView IngredientName, Amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            IngredientName = itemView.findViewById(R.id.IngredientName);
            Amount = itemView.findViewById(R.id.IngredientAmount);
        }
    }
}
