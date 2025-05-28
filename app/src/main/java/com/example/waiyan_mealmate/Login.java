package com.example.waiyan_mealmate;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.waiyan_mealmate.Database.DBHelper;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvForgetPassword, tvError, tvEmailError;
    private String email,password;
    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;
    private DBHelper dbHelper;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        toMain(user);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);
        dbHelper = new DBHelper(this);

        etEmail = findViewById(R.id.Email);
        etPassword = findViewById(R.id.Password);
        tvForgetPassword = findViewById(R.id.forgetPassword);
        tvError = findViewById(R.id.errorMessage);
        tvEmailError = findViewById(R.id.emailError);

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ChangePassword.class));
            }
        });

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve data
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                //cancel error display
                etEmail.setBackgroundResource(R.drawable.edittext_design);
                tvEmailError.setVisibility(View.INVISIBLE);
                etPassword.setBackgroundResource(R.drawable.edittext_design);
                tvError.setText(null);
                tvError.setVisibility(View.INVISIBLE);

                if (email.isEmpty() && password.isEmpty()){
                    etEmail.setBackgroundResource(R.drawable.edittext_error);
                    tvEmailError.setVisibility(View.VISIBLE);
                    etPassword.setBackgroundResource(R.drawable.edittext_error);
                    tvError.setText("*Please enter password");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                if (email.isEmpty()) {
                    etEmail.setBackgroundResource(R.drawable.edittext_error);
                    tvEmailError.setVisibility(View.VISIBLE);
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setBackgroundResource(R.drawable.edittext_error);
                    tvError.setText("*Please enter password");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                toMain(user);
                            } else {
                                etEmail.setBackgroundResource(R.drawable.edittext_error);
                                etPassword.setBackgroundResource(R.drawable.edittext_error);
                                tvError.setText("*Incorrect Email or Password!");
                                tvError.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        //Google Login
        Button googleLogin = findViewById(R.id.google_login);
        googleLogin.setOnClickListener(v -> {
            googleSignIn();
        });

        //To Registration Page
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class)); //to Sign up page
                finish();
            }
        });
    }

    public void googleSignIn(){
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(getString(R.string.web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Extract credential from the result returned by Credential Manager
                        handleSignIn(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("GoogleLogin", e.getLocalizedMessage());
                    }
                }
        );
    }

    private void handleSignIn(Credential credential) {
        // Check if credential is of type Google ID
        if(credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            Bundle credentialData = credential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);
            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Log.e("GoogleLogin", "Credential is not of type Google ID! " + credential.getType());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null){
                            Cursor cursor = dbHelper.selectUser(user.getUid());
                            if (!cursor.moveToFirst()){
                                boolean check = dbHelper.insertUser(user.getUid());
                                if (check){
                                    toMain(user);
                                } else {
                                    new Message("Login Failed", this).showErrorToast();
                                }
                                cursor.close();
                            } else {
                                toMain(user);
                            }
                            cursor.close();
                        }
                    } else {
                        Log.e("GoogleLogin", "signInWithCredentialFailure : " + task.getException());
                    }
                });
    }

    public void toMain(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }
}
