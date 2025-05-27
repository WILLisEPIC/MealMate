package com.example.waiyan_mealmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.waiyan_mealmate.Fragment.GroceryFragment;
import com.example.waiyan_mealmate.Fragment.MealFragment;
import com.example.waiyan_mealmate.Fragment.ProfileFragment;
import com.example.waiyan_mealmate.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private GestureDetector gestureDetector;
    private int current = 0;
    private final Fragment[] fragments = {
            new MealFragment(),
            new GroceryFragment(),
            new ProfileFragment()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //storing user login/logout section
        sharedPreferences = getSharedPreferences("MealMate", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        gestureDetector = new GestureDetector(this, new GestureListener());

        if (isLoggedIn) {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //for gesture listening

            loadFragment(fragments[current]);
            //changing intent using navigation buttons
            binding.navView.setOnNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.navigation_meal){
                    current = 0;
                } else if (item.getItemId() == R.id.navigation_grocery) {
                    current = 1;
                } else {
                    current = 2;
                }
                loadFragment(fragments[current]);
                return true;
            });
        } else {
            //If user hasn't logged in, proceed to welcome screen
            setContentView(R.layout.welcome_view);

            //to login form
            Button login = findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            });

            //to register form
            Button signup = findViewById(R.id.signup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Signup.class));
                    finish();
                }
            });
        }
    }

    private boolean loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragment_activity_main, fragment);
        transaction.commit();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(e2.getY() - e1.getY())) {
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void onSwipeRight() {
        if (current > 0) {
            current--;
            if (loadFragment(fragments[current])) {
                binding.navView.getMenu().getItem(current).setChecked(true);
            }
        }
    }

    private void onSwipeLeft() {
        if (current < fragments.length - 1) {
            current++;
            if (loadFragment(fragments[current])) {
                binding.navView.getMenu().getItem(current).setChecked(true);
            }
        }
    }
}