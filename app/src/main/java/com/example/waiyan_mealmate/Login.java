package com.example.waiyan_mealmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waiyan_mealmate.Database.DBHelper;

public class Login extends AppCompatActivity {

    EditText editEmail,editPassword;
    String email,password;
    DBHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        dbHelper = new DBHelper(this);

        editEmail = findViewById(R.id.Email);
        editPassword = findViewById(R.id.Password);

        TextView error = findViewById(R.id.errorMessage);
        TextView emailError = findViewById(R.id.emailError);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve data
                email = editEmail.getText().toString().trim();
                password = editPassword.getText().toString().trim();

                //cancel error display
                editEmail.setBackgroundResource(R.drawable.edittext_design);
                emailError.setVisibility(View.INVISIBLE);
                editPassword.setBackgroundResource(R.drawable.edittext_design);
                error.setText(null);
                error.setVisibility(View.INVISIBLE);

                //if email and password is null
                if (email.isEmpty() && password.isEmpty()){

                    editEmail.setBackgroundResource(R.drawable.edittext_error);
                    emailError.setVisibility(View.VISIBLE);
                    editPassword.setBackgroundResource(R.drawable.edittext_error);
                    error.setText("*Please enter password");
                    error.setVisibility(View.VISIBLE);

                } else if (email.isEmpty()) { //if email is empty

                    editEmail.setBackgroundResource(R.drawable.edittext_error);
                    emailError.setVisibility(View.VISIBLE);

                } else if (password.isEmpty()) { //if password is empty

                    editPassword.setBackgroundResource(R.drawable.edittext_error);
                    error.setText("*Please enter password");
                    error.setVisibility(View.VISIBLE);

                } else { //if email and password are not empty

                    Cursor cursor = dbHelper.selectUserData(email, password);//select user data from database

                    if (cursor != null && cursor.moveToFirst()) {//if the inputs match the data from database and there is data

                        // retrieve UserID
                        int UserID = cursor.getInt(0);

                        // save login status
                        SharedPreferences preferences = getSharedPreferences("MealMate", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);  //set login status to true
                        editor.apply();

                        //set UserID
                        MealMate mealMate = (MealMate) getApplication();
                        mealMate.setUserId(UserID);

                        //changing activities
                        //to Main page
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else { //if the inputs do not match the data from database
                        editEmail.setBackgroundResource(R.drawable.edittext_error);
                        editPassword.setBackgroundResource(R.drawable.edittext_error);
                        error.setText("*Incorrect Email or Password!");
                        error.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class)); //to Sign up page
                finish();
            }
        });


    }
}
