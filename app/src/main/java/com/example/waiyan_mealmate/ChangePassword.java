package com.example.waiyan_mealmate;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private TextView tvEmailError;
    private EditText edtEmail;
    private Button btnVerifyEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        mAuth = FirebaseAuth.getInstance();

        tvEmailError = findViewById(R.id.emailError);
        edtEmail = findViewById(R.id.Email);
        btnVerifyEmail = findViewById(R.id.verifyEmail);

        btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();
                tvEmailError.setText("");
                if (email.isEmpty()){
                    tvEmailError.setText("Please enter email");
                } else {
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        tvEmailError.setText("Invalid Email");
                    } else {
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                new Message("Reset Email Sent", getApplicationContext()).showCompleteToast();
                            } else {
                                if (task.getException() != null){
                                    new Message(task.getException().toString(), getApplicationContext()).showErrorToast();
                                } else {
                                    new Message("Error", getApplicationContext()).showErrorToast();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
