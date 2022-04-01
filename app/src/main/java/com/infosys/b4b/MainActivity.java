package com.infosys.b4b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnLogOut;
    FirebaseAuth mAuth;
    ImageButton chatBox;
    SearchView searchBar;
    RecyclerView bookList;
    List<String> bookTitles;
    List<Integer> bookImages;
    List<bookListing> allBookListing;
    Adapter adapter;
    BottomNavigationView navbar;
    NavController navcontroller;
    private Fragment profile;
    private Fragment upload;
    private Fragment chat;
    private Fragment home;

    // Display BookListing
    protected void displayHomeFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (home.isAdded()){
            ft.show(home);
        }
        else{
            ft.add(R.id.fragmentContainerView,home);
        }
        if (profile.isAdded()){ft.hide(profile);}
        if (upload.isAdded()){ft.hide(upload);}
        if (chat.isAdded()){ft.hide(chat);}
        ft.commit();
    }


    // Show upload, hide everything else
    protected void displayUploadFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (upload.isAdded()){
            ft.show(upload);
        }
        else{
            ft.add(R.id.fragmentContainerView,upload);
        }
        if (profile.isAdded()){ft.hide(profile);}
        if (home.isAdded()){ft.hide(home);}
        if (chat.isAdded()){ft.hide(chat);}
        ft.commit();
    }
    // Show Profile, hide everything else
    protected void displayProfileFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (profile.isAdded()){
            ft.show(profile);
        }
        else{
            ft.add(R.id.fragmentContainerView,profile);
        }
        if (upload.isAdded()){ft.hide(upload);}
        if (home.isAdded()){ft.hide(home);}
        if (chat.isAdded()){ft.hide(chat);}
        ft.commit();
    }
    // Show Profile, hide everything else
    protected void displayChatFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (chat.isAdded()){
            ft.show(chat);
        }
        else{
            ft.add(R.id.fragmentContainerView,chat);
        }
        if (upload.isAdded()){ft.hide(upload);}
        if (home.isAdded()){ft.hide(home);}
        if (profile.isAdded()){ft.hide(profile);}
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // load it back if it has been saved
        super.onCreate(savedInstanceState);
        // set view as before
        setContentView(R.layout.activity_main);
        // If no saved state, create new instance
        if (savedInstanceState == null) {
            profile = new profile_fragment();
            upload = new upload_fragment();
            chat = new chat_fragment();
            home = new home_fragment();
        }
        // Create all fragments
//        final Fragment home = new home_fragment();

        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] active = {home};
        //btnLogOut = findViewById(R.id.btnLogout);
        chatBox = findViewById(R.id.chatBox);
        //searchBar = findViewById(R.id.searchBar);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        bookTitles = new ArrayList<>();
        bookImages = new ArrayList<>();
        allBookListing = new ArrayList<>();
        //Convert database of book with picture and title to list of title/pics
        //allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        //allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        //allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        //allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        //allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        //allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        //Initialise RecyclerView
        //setUpRecyclerView();
        //Initialise Bottom Navigation bar
        navbar = findViewById(R.id.bottomNavigationView);
        navbar = findViewById(R.id.bottomNavigationView);
        //Tells the Navigation bar what to do when each button on it is pressed
        navbar.setOnItemSelectedListener(navListener);

//        NavHostFragment navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView);
        //Tells the Navigation bar what to do when each button on it is pressed
        //navbar.setOnItemSelectedListener(navListener);
        //For when MainActivity first loads, start with the home_fragment which contains the listings
        fm.beginTransaction().replace(R.id.fragmentContainerView, new home_fragment()).commit();
        //Set chats button to change to chat_fragment
//        chatBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new chat_fragment()).commit();
//            }
//        });
//        int nav_control = navcontroller.find
        // Show fragments being used
//        fm.beginTransaction().add(R.id.fragmentContainerView, book_listing, "3").commit();
//
//        // Hide fragments not used
//        fm.beginTransaction().add(R.id.fragmentContainerView, upload, "2").hide(upload).commit();
//        fm.beginTransaction().add(R.id.fragmentContainerView, profile, "2").hide(upload).commit();


        /*
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s); //s is whatever the user type into the searchview
                return true;
            }
        });

        /*
        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
         */


        /*
        We want to load the myBooks fragments into the Fragment container view
         */
//        myBooks_fragment books_fragment1 = new myBooks_fragment();
//        myBooks_fragment books_fragment2 = new myBooks_fragment();
//        FragmentContainerView.addView(books_fragment1.onCreateView(getLayoutInflater(),));
    }
        //Created the Listener for the Item select for Navigation bar, override the method with the conditions for when each button is pressed, what happens
        BottomNavigationView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chat_fragment:
                        displayChatFragment();
                        return true;

                    case R.id.upload_fragment:
                        displayUploadFragment();
                        return true;

                    case R.id.profile_fragment:
                        displayProfileFragment();
                        return true;
                    case R.id.home_fragment:
                        displayHomeFragment();
                        return true;
                }
                return false;
            }
        };




    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null){                                                                  // Redirect to login page
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
    /*
    private void setUpRecyclerView(){
        bookList = findViewById(R.id.bookList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        adapter = new Adapter(this,allBookListing);
        bookList.setLayoutManager(gridLayoutManager);
        bookList.setAdapter(adapter);
    }
     */

}

