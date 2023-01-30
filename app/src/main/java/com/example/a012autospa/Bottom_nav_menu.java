package com.example.a012autospa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Bottom_nav_menu extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    BookingFragment bookingFragment = new BookingFragment();
    ContactFragment contactFragment = new ContactFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav_menu);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //sets the first window as home fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        //showing desired page based on icon clicks
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.booking:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, bookingFragment).commit();
                        return true;
                    case R.id.contact:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, contactFragment).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                        return true;

                }
                return false;
            }
        });

    }
}