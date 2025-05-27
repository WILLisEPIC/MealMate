package com.example.waiyan_mealmate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waiyan_mealmate.R;

import java.util.ArrayList;

public class PreparationAdapter extends RecyclerView.Adapter<PreparationAdapter.ViewHolder> {

    Context context;
    ArrayList<String> preparationArrayList;

    public PreparationAdapter(Context context , ArrayList<String> ingredientArrayList) {
        this.context = context;
        this.preparationArrayList = ingredientArrayList;
    }


    @NonNull
    @Override
    public PreparationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_preparation, parent, false);
        return new PreparationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreparationAdapter.ViewHolder holder, int position) {
        holder.step.setText(position + 1 +".");
        holder.preparation.setText(preparationArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return preparationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView step, preparation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            step = itemView.findViewById(R.id.Step);
            preparation = itemView.findViewById(R.id.Preparation);
        }
    }
}
