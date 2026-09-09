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
import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Ingredient> ingredientArrayList;
    private ArrayList<String> ingredientType;
    private DBHelper dbHelper;
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;

    public GroceryAdapter(Context context , ArrayList<Ingredient> ingredientArrayList, ArrayList<String> ingredientType) {
        this.context = context;
        this.ingredientArrayList = ingredientArrayList;
        this.ingredientType = ingredientType;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public GroceryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == TYPE_TITLE) {
            view = inflater.inflate(R.layout.grocery_title, parent, false);
        } else {
            view = inflater.inflate(R.layout.grocery_list, parent, false);
        }
        return new GroceryAdapter.ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        Ingredient ingredient = ingredientArrayList.get(position);
        if (ingredientType.contains(ingredient.getTitle())) {
            return TYPE_TITLE;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TITLE) {
            holder.Title.setText(ingredientArrayList.get(position).getType());
        }
        holder.IngredientName.setText(ingredientArrayList.get(position).getName());
        holder.Amount.setText(String.valueOf(ingredientArrayList.get(position).getAmount()));
        holder.ID.setText(String.valueOf(ingredientArrayList.get(position).getId()));
        if (ingredientArrayList.get(position).getStatus() == 0) {
            holder.checkBox.setChecked(false);
            holder.IngredientName.setPaintFlags(holder.IngredientName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.Amount.setPaintFlags(holder.Amount.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            holder.checkBox.setChecked(true);
            holder.IngredientName.setPaintFlags(holder.IngredientName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.Amount.setPaintFlags(holder.Amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (ingredientArrayList.size() > 10 && position == ingredientArrayList.size() - 1) {
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 250);
        } else {
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        }
    }

    @Override
    public int getItemCount() {
        return ingredientArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Title, IngredientName, Amount, ID;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.title);
            IngredientName = itemView.findViewById(R.id.IngredientName);
            Amount = itemView.findViewById(R.id.IngredientAmount);
            ID = itemView.findViewById(R.id.ID);
            checkBox = itemView.findViewById(R.id.IngredientStatus);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!ID.getText().toString().isEmpty()){
                        int IngredientID = Integer.parseInt(ID.getText().toString());
                        boolean update = dbHelper.updateGroceryStatus(IngredientID, isChecked ? 1 : 0);
                        if (isChecked && update) {
                            IngredientName.setPaintFlags(IngredientName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            Amount.setPaintFlags(Amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            IngredientName.setPaintFlags(IngredientName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            Amount.setPaintFlags(Amount.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                    }
                }
            });
        }
    }
}
