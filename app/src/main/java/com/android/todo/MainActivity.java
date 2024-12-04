package com.android.todo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home)
                replaceFragment(new HomeFragment());
            else if(item.getItemId() == R.id.calendar)
                replaceFragment(new CalendarFragment());
            else if(item.getItemId() == R.id.settings)
                replaceFragment(new SettingsFragment());

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}