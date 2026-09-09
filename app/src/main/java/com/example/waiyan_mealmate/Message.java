package com.example.waiyan_mealmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Message {

    private String message;
    private Context context;

    public Message(String message, Context context){
        this.message = message;
        this.context = context;
    }

    public void showErrorToast() {
        View toastView = LayoutInflater.from(this.context).inflate(R.layout.error_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.error_toast_message);
        toastMessage.setText(this.message);
        Toast toast = new Toast(this.context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public void showCompleteToast() {
        View toastView = LayoutInflater.from(this.context).inflate(R.layout.complete_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.complete_toast_message);
        toastMessage.setText(this.message);
        Toast toast = new Toast(this.context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
