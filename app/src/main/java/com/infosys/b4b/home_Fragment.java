package com.infosys.b4b;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_Fragment extends Fragment {

    // TODO: Rename and change types of parameters
    private List<bookListing> allBookListing;
    private SearchView searchBar;
    private RecyclerView bookList;
    private adapter_Home adapterHome;
    private DatabaseReference realTimeDb;
    private ImageButton filterButton;
    String[] genres = upload_fragment.genres;
    private Integer selectedGenre;

    public home_Fragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_Fragment newInstance() {
        home_Fragment fragment = new home_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookList = view.findViewById(R.id.bookList);
        searchBar = view.findViewById(R.id.searchBar);
        filterButton = view.findViewById(R.id.filterButton);

        allBookListing = new ArrayList<>();
        //Initialise Firebase reference
        realTimeDb = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Booklisting");
        //Listen out for changes in Database for bookListings
        realTimeDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()){
                    bookListing change = snap.getValue(bookListing.class);
                    allBookListing.add(change);
                }
                adapterHome.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Initialise RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        bookList.setLayoutManager(gridLayoutManager);
        adapterHome = new adapter_Home(getContext(),allBookListing,this.getActivity());
        bookList.setAdapter(adapterHome);

        //Set up search bar and Filter feature
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterHome.getFilter().filter(s); //s is whatever the user type into the searchview
                return true;
            }
        });

        //Set up filterButton to bring up an AlertDialog for selection of genre to filter
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getContext()
                );
                builder.setTitle("Select genre");
                builder.setCancelable(false);
                builder.setItems(genres, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedGenre=i;
                        String genre = genres[i];
                        adapterHome.getSecondFilter().filter(genre);

                    }
                });
                builder.show();
            }
        });

    }

}