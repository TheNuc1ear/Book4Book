package com.infosys.b4b;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<bookListing> allBookListing;
    private SearchView searchBar;
    private RecyclerView bookList;
    private Adapter adapter;
    private DatabaseReference realTimeDb;
    private StorageReference storageReference;


    public home_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_fragment newInstance(String param1, String param2) {
        home_fragment fragment = new home_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        return inflater.inflate(R.layout.fragment_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookList = view.findViewById(R.id.bookList);
        searchBar = view.findViewById(R.id.searchBar);
        allBookListing = new ArrayList<>();
        //Initialise Firebase reference
//        realTimeDb = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Booklisting");
//        realTimeDb.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                bookListing change = snapshot.getValue(bookListing.class);
//                storageReference = FirebaseStorage.getInstance().getReference().child("images/" + change.getListingId());
//                try {
//                    final File localFile = File.createTempFile("hiiiii","jpg");
//                    storageReference.getFile(localFile);
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    change.setBookImage(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                allBookListing.add(change);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        //Convert database of book with picture and title to list of title/pics
        allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));
        allBookListing.add(new bookListing("Giannis",R.drawable.giannis));
        allBookListing.add(new bookListing("Midnight Library",R.drawable.midnight_lib));

        //Initialise RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        bookList.setLayoutManager(gridLayoutManager);
        adapter = new Adapter(getContext(),allBookListing);
        bookList.setAdapter(adapter);

        //Set up search bar and Filter feature
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
    }


}