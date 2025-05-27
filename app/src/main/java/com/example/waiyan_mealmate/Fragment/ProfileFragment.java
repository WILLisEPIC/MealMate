package com.example.waiyan_mealmate.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.waiyan_mealmate.Database.DBHelper;
import com.example.waiyan_mealmate.MainActivity;
import com.example.waiyan_mealmate.MealMate;
import com.example.waiyan_mealmate.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    int userId;
    TextView Username,Email;
    DBHelper dbHelper;
    MealMate mealMate;
    ShapeableImageView user_photo;
    byte[] photo;
    String username,email,password,currentpass,newpass;
    private static final int PICK_IMAGE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        dbHelper = new DBHelper(getContext());
        mealMate = (MealMate) requireActivity().getApplication();
        userId = mealMate.getUserId();
        Username = view.findViewById(R.id.Username);
        Email = view.findViewById(R.id.Email);

        //retrieve data
        retrieveData();

        Username.setText(username);
        Email.setText(email);

        Username.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showNameEditDialog();
                return true;
            }
        });

        Email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showEmailEditDialog();
                return true;
            }
        });

        user_photo = view.findViewById(R.id.profile_image);
        //set default photo if there is not photo in database
        if (photo == null) {
            user_photo.setImageResource(R.drawable.profile_picture_default);
        } else {
            user_photo.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
        }

        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,PICK_IMAGE);
                }
            }
        });

        user_photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (user_photo.getDrawable().getConstantState() == Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.profile_picture_default)).getConstantState()){
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete the profile picture?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean check = dbHelper.updateUserData(userId, username, email, password, null);
                                if (check) {
                                    showCompleteToast("Photo deleted successfully");
                                    retrieveData();
                                    user_photo.setImageResource(R.drawable.profile_picture_default);
                                } else {
                                    showErrorToast("Failed to delete Photo!");
                                }
                            }
                        }).setNegativeButton("Cancel",null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });

        //change password section
        EditText PW = view.findViewById(R.id.CurrentPass);
        EditText CPW = view.findViewById(R.id.NewPass);
        TextView PWerror = view.findViewById(R.id.currentpasswordError);
        TextView CPWerror = view.findViewById(R.id.newpasswordError);

        Button change = view.findViewById(R.id.ChangePassword);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentpass = PW.getText().toString();
                newpass = CPW.getText().toString();
                PWerror.setVisibility(View.INVISIBLE);
                CPWerror.setVisibility(View.INVISIBLE);
                PW.setBackgroundResource(R.drawable.edittext_design);
                CPW.setBackgroundResource(R.drawable.edittext_design);

                if(currentpass.isEmpty() && newpass.isEmpty()){

                    PW.setBackgroundResource(R.drawable.edittext_error);
                    CPW.setBackgroundResource(R.drawable.edittext_error);
                    PWerror.setText("*Please enter current password");
                    CPWerror.setText("*Please enter new password");
                    PWerror.setVisibility(View.VISIBLE);
                    CPWerror.setVisibility(View.VISIBLE);

                } else if (currentpass.isEmpty()){

                    PW.setBackgroundResource(R.drawable.edittext_error);
                    PWerror.setText("*Please enter current password");
                    PWerror.setVisibility(View.VISIBLE);

                } else if (newpass.isEmpty()) {

                    CPW.setBackgroundResource(R.drawable.edittext_error);
                    CPWerror.setText("*Please enter new password");
                    CPWerror.setVisibility(View.VISIBLE);

                } else if (!currentpass.equals(password)) {

                    PW.setBackgroundResource(R.drawable.edittext_error);
                    PWerror.setText("*Incorrect current password");
                    PWerror.setVisibility(View.VISIBLE);

                } else if (!isPasswordValid(newpass)) {

                    CPW.setBackgroundResource(R.drawable.edittext_error);
                    CPWerror.setText("*New password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character");
                    CPWerror.setVisibility(View.VISIBLE);

                } else {
                    boolean check = dbHelper.updateUserData(userId,username,email,newpass,photo);
                    if(check) {
                        showCompleteToast("Password updated successfully");
                        PW.setText(null);
                        CPW.setText(null);
                        retrieveData();
                    } else {
                        showErrorToast("Failed to update password!");
                    }
                }
            }
        });

        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear login status
                SharedPreferences preferences = getActivity().getSharedPreferences("MealMate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", false);  //set login status to false
                editor.remove("UserID"); //clear user ID
                editor.apply();
                //go to welcome page
                startActivity(new Intent(requireContext(), MainActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                user_photo.setImageBitmap(bitmap);
                saveImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        photo = outputStream.toByteArray();
        boolean check = dbHelper.updateUserData(userId,username,email,password,photo);
        if(check) {
            showCompleteToast("Profile picture changed");
            retrieveData();
        } else {
            showErrorToast("Failed to change profile picture!");
        }
    }

    private void retrieveData(){
        Cursor cursor = dbHelper.selectUserDataByID(userId);
        if (cursor != null && cursor.moveToNext()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
            email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
            photo = cursor.getBlob(cursor.getColumnIndexOrThrow("Profile"));
            cursor.close();
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

    private void showNameEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_profile_name, null);
        EditText EditUsername = view.findViewById(R.id.EditUsername);
        EditUsername.setText(username);
        builder.setTitle("Edit Profile Name")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = EditUsername.getText().toString();
                        if (username.isEmpty()) {
                            showErrorToast("Username cannot be empty!");
                        } else if (username.length() > 30) {
                            showErrorToast("Username cannot contain more than 30 characters!");
                        } else {
                            boolean check = dbHelper.updateUserData(userId,username,email,password,photo);
                            if(check) {
                                showCompleteToast("Username updated successfully");
                                retrieveData();
                                Username.setText(username);
                            } else {
                                showErrorToast("Failed to update Username!");
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showEmailEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_profile_email, null);
        EditText EditEmail = view.findViewById(R.id.EditEmail);
        EditEmail.setText(email);
        builder.setTitle("Edit Email")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email;
                        email = EditEmail.getText().toString();
                        if (email.isEmpty()) {
                            showErrorToast("Email cannot be empty!");
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            showErrorToast("Invalid Email Format!");
                        } else if (emailNotAvailable(email)) {
                            showErrorToast("This email already exists!");
                        } else {
                            boolean check = dbHelper.updateUserData(userId,username,email,password,photo);
                            if(check) {
                                showCompleteToast("Email updated successfully");
                                retrieveData();
                                Email.setText(email);
                            } else {
                                showErrorToast("Failed to update Email!");
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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

    private void showErrorToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.error_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.error_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(getContext());
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void showCompleteToast(String message) {
        View toastView = getLayoutInflater().inflate(R.layout.complete_toast, null);
        TextView toastMessage = toastView.findViewById(R.id.complete_toast_message);
        toastMessage.setText(message);
        Toast toast = new Toast(getContext());
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
