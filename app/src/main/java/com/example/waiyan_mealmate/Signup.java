package com.example.waiyan_mealmate;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waiyan_mealmate.Database.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private ImageButton ibBack;
    private TextView tvLogin, tvUsernameError, tvEmailError, tvPasswordError, tvConfirmPasswordError;
    private Button btnSignup;
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private String username,email,password,conpassword;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        dbHelper = new DBHelper(this);
        mAuth = FirebaseAuth.getInstance();
        ibBack = findViewById(R.id.back);
        tvLogin = findViewById(R.id.login);
        btnSignup = findViewById(R.id.signup);

        ibBack.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

        etUsername = findViewById(R.id.Username);
        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        etConfirmPassword = findViewById(R.id.ConPassword);

        tvEmailError = findViewById(R.id.emailError);
        tvUsernameError = findViewById(R.id.usernameError);
        tvPasswordError = findViewById(R.id.passwordError);
        tvConfirmPasswordError = findViewById(R.id.conpasswordError);
    }

    @Override
    public void onClick(View v) {
        username = etUsername.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        conpassword = etConfirmPassword.getText().toString().trim();
        if(v.getId() == R.id.back){
            startActivity(new Intent(Signup.this, MainActivity.class));
            finish();
        } else if (v.getId() == R.id.login) {
            startActivity(new Intent(Signup.this, Login.class));
            finish();
        } else {

            restart();

            if (username.isEmpty() && email.isEmpty() && password.isEmpty() && conpassword.isEmpty()) {
                
                etUsername.setBackgroundResource(R.drawable.edittext_error);
                etEmail.setBackgroundResource(R.drawable.edittext_error);
                etPassword.setBackgroundResource(R.drawable.edittext_error);
                etConfirmPassword.setBackgroundResource(R.drawable.edittext_error);

                tvUsernameError.setText("*Please enter username");
                tvUsernameError.setVisibility(View.VISIBLE);
                tvEmailError.setText("*Please enter email");
                tvEmailError.setVisibility(View.VISIBLE);
                tvPasswordError.setText("*Please enter password");
                tvPasswordError.setVisibility(View.VISIBLE);
                tvConfirmPasswordError.setText("*Please enter confirm password");
                tvConfirmPasswordError.setVisibility(View.VISIBLE);
                return;
                
            }
            if (username.isEmpty()) {
                etUsername.setBackgroundResource(R.drawable.edittext_error);
                tvUsernameError.setText("*Please enter username");
                tvUsernameError.setVisibility(View.VISIBLE);
                return;
            }
            if(username.length() > 30){
                etUsername.setBackgroundResource(R.drawable.edittext_error);
                tvUsernameError.setText("*Username cannot contain more than 30 characters");
                tvUsernameError.setVisibility(View.VISIBLE);
                return;
            }
            if (email.isEmpty()) {
                etEmail.setBackgroundResource(R.drawable.edittext_error);
                tvEmailError.setText("*Please enter email");
                tvEmailError.setVisibility(View.VISIBLE);
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmail.setBackgroundResource(R.drawable.edittext_error);
                tvEmailError.setText("*Invalid Email");
                tvEmailError.setVisibility(View.VISIBLE);
                return;
            }
            if (password.isEmpty()) {
                etPassword.setBackgroundResource(R.drawable.edittext_error);
                tvPasswordError.setText("*Please enter password");
                tvPasswordError.setVisibility(View.VISIBLE);
                return;
            }
            if (!isPasswordValid(password)) {
                etPassword.setBackgroundResource(R.drawable.edittext_error);
                tvPasswordError.setText("*Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character");
                tvPasswordError.setVisibility(View.VISIBLE);
                return;
            }
            if (conpassword.isEmpty()) {
                etConfirmPassword.setBackgroundResource(R.drawable.edittext_error);
                tvConfirmPasswordError.setText("*Please enter confirm password");
                tvConfirmPasswordError.setVisibility(View.VISIBLE);
                return;
            }
            if (!conpassword.equals(password)) {
                etPassword.setBackgroundResource(R.drawable.edittext_error);
                etConfirmPassword.setBackgroundResource(R.drawable.edittext_error);
                tvConfirmPasswordError.setText("*Password and Confirm Password must be the same");
                tvConfirmPasswordError.setVisibility(View.VISIBLE);
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                    Cursor cursor = dbHelper.selectUser(user.getUid());
                                    if (cursor == null){
                                        boolean check = dbHelper.insertUser(user.getUid());
                                        if (check){
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username)
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            new Message("Registration Complete", this).showCompleteToast();
                                                            etUsername.setText(null);
                                                            etEmail.setText(null);
                                                            etPassword.setText(null);
                                                            etConfirmPassword.setText(null);
                                                        }
                                                    });
                                        } else {
                                            new Message("Registration Failed", this).showErrorToast();
                                        }
                                    }
                                mAuth.signOut();
                            }
                        } else {
                            String errorMsg = "Registration Failed!";
                            Exception exception = task.getException();
                            if (exception instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                errorMsg = "This email is already registered. Try signing in instead.";
                            }

                            new Message(errorMsg, this).showErrorToast();
                        }
                    });
        }
    }

    public void restart(){
        etUsername.setBackgroundResource(R.drawable.edittext_design);
        etEmail.setBackgroundResource(R.drawable.edittext_design);
        etPassword.setBackgroundResource(R.drawable.edittext_design);
        etConfirmPassword.setBackgroundResource(R.drawable.edittext_design);
        tvUsernameError.setText(null);
        tvUsernameError.setVisibility(View.INVISIBLE);
        tvEmailError.setText(null);
        tvEmailError.setVisibility(View.INVISIBLE);
        tvPasswordError.setText(null);
        tvPasswordError.setVisibility(View.INVISIBLE);
        tvConfirmPasswordError.setText(null);
        tvConfirmPasswordError.setVisibility(View.INVISIBLE);
    }

    //check password strength
    public boolean isPasswordValid(String pass) {
        String expression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,100}$";
        return pass.matches(expression);
    }
}
