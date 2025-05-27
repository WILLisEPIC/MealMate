package com.example.waiyan_mealmate;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waiyan_mealmate.Database.DBHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    ImageButton back;
    TextView login,errorUsername,errorEmail,errorPassword,errorConPassword;
    Button signup;
    DBHelper dbHelper;
    EditText Username,Email,Password,ConPassword;
    String username,email,password,conpassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        dbHelper = new DBHelper(this);

        back = findViewById(R.id.back);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        back.setOnClickListener(this);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

        Username = findViewById(R.id.Username);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        ConPassword = findViewById(R.id.ConPassword);

        errorEmail = findViewById(R.id.emailError);
        errorUsername = findViewById(R.id.usernameError);
        errorPassword = findViewById(R.id.passwordError);
        errorConPassword = findViewById(R.id.conpasswordError);
    }

    @Override
    public void onClick(View v) {
        username = Username.getText().toString().trim();
        email = Email.getText().toString().trim();
        password = Password.getText().toString().trim();
        conpassword = ConPassword.getText().toString().trim();
        if(v.getId() == R.id.back){
            startActivity(new Intent(Signup.this, MainActivity.class));
            finish();
        } else if (v.getId() == R.id.login) {
            startActivity(new Intent(Signup.this, Login.class));
            finish();
        } else {
            Username.setBackgroundResource(R.drawable.edittext_design);
            Email.setBackgroundResource(R.drawable.edittext_design);
            Password.setBackgroundResource(R.drawable.edittext_design);
            ConPassword.setBackgroundResource(R.drawable.edittext_design);
            errorUsername.setText(null);
            errorUsername.setVisibility(View.INVISIBLE);
            errorEmail.setText(null);
            errorEmail.setVisibility(View.INVISIBLE);
            errorPassword.setText(null);
            errorPassword.setVisibility(View.INVISIBLE);
            errorConPassword.setText(null);
            errorConPassword.setVisibility(View.INVISIBLE);

            if (username.isEmpty() && email.isEmpty() && password.isEmpty() && conpassword.isEmpty()) {
                
                Username.setBackgroundResource(R.drawable.edittext_error);
                Email.setBackgroundResource(R.drawable.edittext_error);
                Password.setBackgroundResource(R.drawable.edittext_error);
                ConPassword.setBackgroundResource(R.drawable.edittext_error);

                errorUsername.setText("*Please enter username");
                errorUsername.setVisibility(View.VISIBLE);
                errorEmail.setText("*Please enter email");
                errorEmail.setVisibility(View.VISIBLE);
                errorPassword.setText("*Please enter password");
                errorPassword.setVisibility(View.VISIBLE);
                errorConPassword.setText("*Please enter confirm password");
                errorConPassword.setVisibility(View.VISIBLE);
                
            } else if (username.isEmpty()) {
                
                Username.setBackgroundResource(R.drawable.edittext_error);
                errorUsername.setText("*Please enter username");
                errorUsername.setVisibility(View.VISIBLE);
                
            } else if(username.length() > 30){

                Username.setBackgroundResource(R.drawable.edittext_error);
                errorUsername.setText("*Username cannot contain more than 30 characters");
                errorUsername.setVisibility(View.VISIBLE);

            } else if (email.isEmpty()) {

                Email.setBackgroundResource(R.drawable.edittext_error);
                errorEmail.setText("*Please enter email");
                errorEmail.setVisibility(View.VISIBLE);

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                Email.setBackgroundResource(R.drawable.edittext_error);
                errorEmail.setText("*Invalid Email");
                errorEmail.setVisibility(View.VISIBLE);

            } else if (emailNotAvailable(email)) {

                Email.setBackgroundResource(R.drawable.edittext_error);
                errorEmail.setText("*This email already exists");
                errorEmail.setVisibility(View.VISIBLE);

            } else if (password.isEmpty()) {

                Password.setBackgroundResource(R.drawable.edittext_error);
                errorPassword.setText("*Please enter password");
                errorPassword.setVisibility(View.VISIBLE);

            } else if (!isPasswordValid(password)) {

                Password.setBackgroundResource(R.drawable.edittext_error);
                errorPassword.setText("*Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character");
                errorPassword.setVisibility(View.VISIBLE);

            } else if (conpassword.isEmpty()) {
                
                ConPassword.setBackgroundResource(R.drawable.edittext_error);
                errorConPassword.setText("*Please enter confirm password");
                errorConPassword.setVisibility(View.VISIBLE);

            }  else if (!conpassword.equals(password)) {

                Password.setBackgroundResource(R.drawable.edittext_error);
                ConPassword.setBackgroundResource(R.drawable.edittext_error);
                errorConPassword.setText("*Password and Confirm Password must be the same");
                errorConPassword.setVisibility(View.VISIBLE);
                
            } else {

                boolean check = dbHelper.insertUserData(username, email, password);
                if(check) {
                    LayoutInflater inflater = getLayoutInflater();
                    View toastCompleteView = inflater.inflate(R.layout.complete_toast, null);
                    TextView completeToastMessage = toastCompleteView.findViewById(R.id.complete_toast_message);
                    completeToastMessage.setText("Registration Complete");
                    Toast completeToast = new Toast(Signup.this);
                    completeToast.setView(toastCompleteView);
                    completeToast.setDuration(Toast.LENGTH_LONG);
                    completeToast.show();

                    Username.setText(null);
                    Email.setText(null);
                    Password.setText(null);
                    ConPassword.setText(null);
                } else {
                    // Show registration error toast
                    LayoutInflater inflater = getLayoutInflater();
                    View toastErrorView = inflater.inflate(R.layout.error_toast, null);
                    TextView errorToastMessage = toastErrorView.findViewById(R.id.error_toast_message);
                    errorToastMessage.setText("Registration Failed!");
                    Toast errorToast = new Toast(Signup.this);
                    errorToast.setView(toastErrorView);
                    errorToast.setDuration(Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        }
    }

    private boolean emailNotAvailable(String value) {
        boolean notAvailable = false;
        Cursor cursor = dbHelper.selectUserDataByEmail(value);

        if (cursor != null && cursor.moveToFirst()) {
            notAvailable = true;
        }
        cursor.close();

        return notAvailable;
    }

    //check password strength
    private boolean isPasswordValid(String pass) {
        boolean isValid = false;

        //regular expression to match the password criteria
        String expression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,12}$";
        CharSequence input = pass;

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
