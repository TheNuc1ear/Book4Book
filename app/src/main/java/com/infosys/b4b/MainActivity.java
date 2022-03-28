package com.infosys.b4b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //btnLogOut = findViewById(R.id.btnLogout);
        chatBox = findViewById(R.id.chatBox);
        //searchBar = findViewById(R.id.searchBar);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        bookTitles = new ArrayList<>();
        bookImages = new ArrayList<>();
        allBookListing = new ArrayList<>();
        //Convert database of book with picture and title to list of title/pics
        allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        //Initialise RecyclerView
        //setUpRecyclerView();
        //Initialise Bottom Navigation bar
        navbar = findViewById(R.id.bottomNavigationView);
        //Tells the Navigation bar what to do when each button on it is pressed
        navbar.setOnItemSelectedListener(navListener);
        //For when MainActivity first loads, start with the home_fragment which contains the listings
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new home_fragment()).commit();
        //Set chats button to change to chat_fragment
        chatBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,new chat_fragment()).commit();
            }
        });



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

    }
    //Created the Listener for the Item select for Navigation bar, override the method with the conditions for when each button is pressed, what happens
    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            Fragment selectedFragment = null;
            switch (id){
                case R.id.home:
                    selectedFragment = new home_fragment();
                    break;
                case R.id.myBooks:
                    selectedFragment = new myBooks_fragment();
                    break;
                case R.id.upload:
                    selectedFragment = new upload_fragment();
                    break;
                case R.id.profile:
                    selectedFragment = new profile_fragment();
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

