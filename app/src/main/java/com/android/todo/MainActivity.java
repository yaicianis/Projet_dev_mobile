package com.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String userEnergyLevel = "medium";

    public String getUserEnergyLevel() {
        return userEnergyLevel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.energy) {
                showEnergyLevelPopup();
            } else if (item.getItemId() == R.id.settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String tag = fragment.getClass().getSimpleName();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment != null) {
            fragmentTransaction.replace(R.id.fragmentContainer, existingFragment);
        } else {
            fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag);
        }

        fragmentTransaction.commit();
    }

    private void showEnergyLevelPopup() {
        String[] energyLevels = {"low", "medium", "high"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Energy Level")
                .setItems(energyLevels, (dialog, which) -> {
                    userEnergyLevel = energyLevels[which];
                    Toast.makeText(this, "Energy level set to " + userEnergyLevel, Toast.LENGTH_SHORT).show();
                    notifyFragmentsOfEnergyChange();
                });
        builder.create().show();
    }


    private void notifyFragmentsOfEnergyChange() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof EnergyLevelAware) {
            ((EnergyLevelAware) currentFragment).onEnergyLevelChanged(userEnergyLevel);
        }
    }
}
