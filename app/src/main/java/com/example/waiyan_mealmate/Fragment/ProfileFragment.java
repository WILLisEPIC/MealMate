package com.example.waiyan_mealmate.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MainActivity;
import com.example.waiyan_mealmate.Message;
import com.example.waiyan_mealmate.Photo;
import com.example.waiyan_mealmate.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DBHelper dbHelper;
    private TextView tvUsername, tvEmail, tvPasswordError, tvNewPasswordError, tvNewUsernameError, tvConfirmPasswordError;
    private EditText etPassword, etNewPassword, etNewUsername, etConfirmPassword;
    private Button btnChangeUsername, btnChangePassword, btnLogout;
    private ImageButton ibUsernameToggle, ibPasswordToggle;
    private ConstraintLayout layoutChangeUsername, layoutChangePassword;
    private ShapeableImageView sivUserPhoto;
    private ActivityResultLauncher<Intent> activity;
    private byte[] photo;
    private String userId, username, email, providerId;
    private boolean isUsernameLayoutVisible, isPasswordLayoutVisible = false;

    @Override
    public void onResume() {
        super.onResume();
        if(user != null){
            userId = user.getUid();
            username = user.getDisplayName();
            email = user.getEmail();
            Cursor cursor = dbHelper.selectUserPhoto(userId);
            if (cursor != null && cursor.moveToFirst()){
                byte[] photo = cursor.getBlob(0);
                if (photo != null){
                    this.photo = photo;
                }
            }
            changeUserPhoto();
            isUsernameLayoutVisible = false;
            isPasswordLayoutVisible = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbHelper = new DBHelper(getContext());
        sivUserPhoto = view.findViewById(R.id.profile_image);
        tvUsername = view.findViewById(R.id.Username);
        tvEmail = view.findViewById(R.id.Email);
        btnLogout = view.findViewById(R.id.logout);

        //change username items
        ibUsernameToggle = view.findViewById(R.id.changeUsernameLayoutToggle);
        layoutChangeUsername = view.findViewById(R.id.changeUsernameBox);
        etNewUsername = view.findViewById(R.id.newUsername);
        tvNewUsernameError = view.findViewById(R.id.newUsernameError);
        etConfirmPassword = view.findViewById(R.id.confirmPassword);
        tvConfirmPasswordError = view.findViewById(R.id.confirmPasswordError);
        btnChangeUsername = view.findViewById(R.id.changeUsernameButton);

        //change password items
        ibPasswordToggle = view.findViewById(R.id.changePasswordLayoutToggle);
        layoutChangePassword = view.findViewById(R.id.changePasswordBox);
        etPassword = view.findViewById(R.id.CurrentPass);
        tvPasswordError = view.findViewById(R.id.currentpasswordError);
        etNewPassword = view.findViewById(R.id.NewPass);
        tvNewPasswordError = view.findViewById(R.id.newpasswordError);
        btnChangePassword = view.findViewById(R.id.changePasswordButton);

        if(user != null) {
            userId = user.getUid();
            username = user.getDisplayName();
            email = user.getEmail();
            Cursor cursor = dbHelper.selectUserPhoto(userId);
            if (cursor != null && cursor.moveToFirst()) {
                byte[] photo = cursor.getBlob(0);
                if (photo != null) {
                    this.photo = photo;
                }
            }
        }

        changeUserPhoto();
        tvUsername.setText(username);
        tvEmail.setText(email);

        ibUsernameToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUsernameLayoutVisible){
                    ibUsernameToggle.setImageResource(R.drawable.arrow_drop_up);
                    layoutChangeUsername.setVisibility(View.VISIBLE);
                }else {
                    ibUsernameToggle.setImageResource(R.drawable.arrow_drop_down);
                    layoutChangeUsername.setVisibility(View.GONE);
                }
                isUsernameLayoutVisible = !isUsernameLayoutVisible;
            }
        });

        ibPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (providerId != null && providerId.equals("google.com")) {
                    new Message("Google login cannot change password", getContext()).showCompleteToast();
                } else {
                    if (!isPasswordLayoutVisible) {
                        ibPasswordToggle.setImageResource(R.drawable.arrow_drop_up);
                        layoutChangePassword.setVisibility(View.VISIBLE);
                    } else {
                        ibPasswordToggle.setImageResource(R.drawable.arrow_drop_down);
                        layoutChangePassword.setVisibility(View.GONE);
                    }
                    isPasswordLayoutVisible = !isPasswordLayoutVisible;
                }
            }
        });

        btnChangeUsername.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     String newUsername = etNewUsername.getText().toString().trim();
                     String confirmPass = etConfirmPassword.getText().toString().trim();
                     tvNewUsernameError.setVisibility(View.INVISIBLE);
                     tvConfirmPasswordError.setVisibility(View.INVISIBLE);
                     etNewUsername.setBackgroundResource(R.drawable.edittext_design);
                     etConfirmPassword.setBackgroundResource(R.drawable.edittext_design);

                     if (confirmPass.isEmpty() && newUsername.isEmpty()) {

                         etNewUsername.setBackgroundResource(R.drawable.edittext_error);
                         etConfirmPassword.setBackgroundResource(R.drawable.edittext_error);
                         tvNewUsernameError.setText("*Please enter new username");
                         tvConfirmPasswordError.setText("*Please enter confirm password");
                         tvNewUsernameError.setVisibility(View.VISIBLE);
                         tvConfirmPasswordError.setVisibility(View.VISIBLE);

                     } else if (confirmPass.isEmpty()) {

                         etConfirmPassword.setBackgroundResource(R.drawable.edittext_error);
                         tvConfirmPasswordError.setText("*Please enter confirm password");
                         tvConfirmPasswordError.setVisibility(View.VISIBLE);

                     } else if (newUsername.isEmpty()) {

                         etNewUsername.setBackgroundResource(R.drawable.edittext_error);
                         tvNewUsernameError.setText("*Please enter new username");
                         tvNewUsernameError.setVisibility(View.VISIBLE);

                     } else {
                         AuthCredential credential = EmailAuthProvider.getCredential(email, confirmPass);
                         user.reauthenticate(credential)
                                 .addOnCompleteListener(task -> {
                                     if (task.isSuccessful()) {
                                         if (newUsername.length() <= 50){
                                             UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                     .setDisplayName(newUsername)
                                                             .build();
                                             user.updateProfile(request)
                                                     .addOnCompleteListener(task1 -> {
                                                         if (task1.isSuccessful()){
                                                             new Message("Username completely changed", getContext()).showCompleteToast();
                                                             etNewUsername.setText(null);
                                                             etConfirmPassword.setText(null);
                                                             tvNewUsernameError.setText(null);
                                                             tvConfirmPasswordError.setText(null);
                                                             tvUsername.setText(newUsername);
                                                         } else {
                                                             new Message("Failed to change username", getContext()).showErrorToast();
                                                             etNewUsername.setText(null);
                                                             etConfirmPassword.setText(null);
                                                             tvNewUsernameError.setText(null);
                                                             tvConfirmPasswordError.setText(null);
                                                         }
                                                     });
                                         }
                                     } else {
                                         etConfirmPassword.setBackgroundResource(R.drawable.edittext_error);
                                         tvConfirmPasswordError.setText("*Incorrect password");
                                         tvConfirmPasswordError.setVisibility(View.VISIBLE);
                                     }
                                 });
                     }
                 }
             });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = etNewPassword.getText().toString().trim();
                String currentPass = etPassword.getText().toString().trim();
                tvNewPasswordError.setVisibility(View.INVISIBLE);
                tvPasswordError.setVisibility(View.INVISIBLE);
                etNewPassword.setBackgroundResource(R.drawable.edittext_design);
                etPassword.setBackgroundResource(R.drawable.edittext_design);

                if (currentPass.isEmpty() && newPass.isEmpty()) {

                    etPassword.setBackgroundResource(R.drawable.edittext_error);
                    etNewPassword.setBackgroundResource(R.drawable.edittext_error);
                    tvPasswordError.setText("*Please enter current password");
                    tvNewPasswordError.setText("*Please enter new password");
                    tvPasswordError.setVisibility(View.VISIBLE);
                    tvNewPasswordError.setVisibility(View.VISIBLE);

                } else if (currentPass.isEmpty()) {

                    etPassword.setBackgroundResource(R.drawable.edittext_error);
                    tvPasswordError.setText("*Please enter current password");
                    tvPasswordError.setVisibility(View.VISIBLE);

                } else if (newPass.isEmpty()) {

                    etNewPassword.setBackgroundResource(R.drawable.edittext_error);
                    tvNewPasswordError.setText("*Please enter new password");
                    tvNewPasswordError.setVisibility(View.VISIBLE);

                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPass);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (!isPasswordValid(newPass)) {
                                        etNewPassword.setBackgroundResource(R.drawable.edittext_error);
                                        tvNewPasswordError.setText("*New password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character");
                                        tvNewPasswordError.setVisibility(View.VISIBLE);
                                    } else {
                                        user.updatePassword(newPass)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        new Message("Password updated successfully", getContext()).showCompleteToast();
                                                        etNewPassword.setText(null);
                                                        etPassword.setText(null);
                                                        tvPasswordError.setText(null);
                                                        tvNewPasswordError.setText(null);
                                                    } else {
                                                        new Message("Failed to update password", getContext()).showErrorToast();
                                                        etNewPassword.setText(null);
                                                        etPassword.setText(null);
                                                        tvPasswordError.setText(null);
                                                        tvNewPasswordError.setText(null);
                                                    }
                                                });
                                    }
                                } else {
                                    etPassword.setBackgroundResource(R.drawable.edittext_error);
                                    tvPasswordError.setText("*Incorrect password");
                                    tvPasswordError.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        });

        activity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Uri image = result.getData().getData();
                        photo = new Photo(getContext()).stringToByteArray(image);
                        if (dbHelper.updateUserPhoto(userId, photo)){
                            new Message("Profile picture changed successfully", getContext()).showCompleteToast();
                            changeUserPhoto();
                        } else {
                            new Message("Cannot change profile picture", getContext()).showErrorToast();
                            photo = null;
                            changeUserPhoto();
                        }
                    }
                });

        sivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                        ? android.Manifest.permission.READ_MEDIA_IMAGES
                        : android.Manifest.permission.READ_EXTERNAL_STORAGE;

                if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, R.string.read_storage_request_code);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.launch(intent);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear login status
                mAuth.signOut();
                //go to welcome page
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
        });

        return view;
    }

    private void changeUserPhoto(){
        if (photo == null) {
            sivUserPhoto.setImageResource(R.drawable.profile_picture_default);
        } else {
            sivUserPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
        }
    }

    private boolean isPasswordValid(String pass) {
        String expression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,12}$";
        return pass.matches(expression);
    }
}
