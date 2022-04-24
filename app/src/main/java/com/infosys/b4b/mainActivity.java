package com.infosys.b4b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class mainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    List<String> bookTitles;
    List<Integer> bookImages;
    List<bookListing> allBookListing;
    BottomNavigationView navbar;
    private Fragment profile;
    private Fragment upload;
    private Fragment chat;
    private Fragment home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // load it back if it has been saved
        super.onCreate(savedInstanceState);
        // set view as before
        setContentView(R.layout.activity_main);
        // If no saved state, create new instance
        if (savedInstanceState == null) {
            profile = new profile_Fragment();
            upload = new upload_fragment();
            chat = new chat_Fragment();
            home = new home_Fragment();
        }

        final FragmentManager fm = getSupportFragmentManager();
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        bookTitles = new ArrayList<>();
        bookImages = new ArrayList<>();
        allBookListing = new ArrayList<>();
        //Initialise Bottom Navigation bar
        navbar = findViewById(R.id.bottomNavigationView);
        //Tells the Navigation bar what to do when each button on it is pressed
        navbar.setOnItemSelectedListener(navListener);

        //For when MainActivity first loads, start with the home_fragment which contains the listings
        fm.beginTransaction().replace(R.id.fragmentContainerView, new home_Fragment()).commit();

    }

    //Created the Listener for the Item select for Navigation bar, override the method with the conditions for when each button is pressed, what happens
    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            Fragment selectedFragment = null;
            switch (id) {
                case R.id.home_fragment:
                    selectedFragment= home_Fragment.newInstance();
                    break;
                case R.id.chat_fragment:
                    selectedFragment = chat_Fragment.newInstance();
                    break;
                case R.id.upload_fragment:
                    selectedFragment = upload_fragment.newInstance();
                    break;
                case R.id.profile_fragment:
                    selectedFragment = profile_Fragment.newInstance();;
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,selectedFragment).commit();
            return true;
        }
    };


        @Override
        protected void onStart() {
            super.onStart();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null) {                                                                  // Redirect to login page
                startActivity(new Intent(mainActivity.this, loginActivity.class));
            }
        }


    }


