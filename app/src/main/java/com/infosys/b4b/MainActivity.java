package com.infosys.b4b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //btnLogOut = findViewById(R.id.btnLogout);
        chatBox = findViewById(R.id.chatBox);
        searchBar = findViewById(R.id.searchBar);
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
        setUpRecyclerView();

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
         */   //For sign out button

        chatBox.setOnClickListener(view->{
            //TODO: Change to ChatActivity/activity_chat
        });

        searchBar.setOnClickListener(view->{
            //TODO: Change to a RecyclerView for books
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null){                                                                  // Redirect to login page
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void setUpRecyclerView(){
        bookList = findViewById(R.id.bookList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        adapter = new Adapter(this,allBookListing);
        bookList.setLayoutManager(gridLayoutManager);
        bookList.setAdapter(adapter);
    }
}

