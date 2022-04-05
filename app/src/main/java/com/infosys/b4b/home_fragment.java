package com.infosys.b4b;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_fragment extends Fragment implements Adapter.ItemClickListener {

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
    ArrayAdapter<String> adapterItems;
    private ImageButton filterButton;
    String[] genres = {"All","Action and Adventure", "Classics", "Comic Book /Graphic Novel", "Detective and Mystery"
            , "Fantasy", "Historical Fiction", "Horror", "Literary Fiction","Romance", "Sci-Fi",
            "Short Stories","Suspense and Thrillers", "Women's Fiction" , "Biographies/Autobiographies",
            "History", "Memoir", "Poetry", "Self-Help", "True Crime", "Others"};
    private Integer selectedGenre;

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
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        return view;
    }

    private void initUpRecyclerView(View view){
        bookList = view.findViewById(R.id.bookList);
//        Intent myIntent = new Intent(MainActivity.this,MainActivity.class);
//        startActivity(myIntent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),2,GridLayoutManager.VERTICAL,false);
        adapter = new Adapter(view.getContext(),allBookListing,this::onItemClick);
        bookList.setLayoutManager(gridLayoutManager);
        bookList.setAdapter(adapter);
    }
    public void onItemClick(String s){
        Fragment mybook_fagement = myBooks_fragment.newInstance("a","b");
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView,mybook_fagement);
        transaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookList = view.findViewById(R.id.bookList);
        searchBar = view.findViewById(R.id.searchBar);
        filterButton = view.findViewById(R.id.filterButton);

//        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_genres, genres);
//        filterButton.setAdapter(adapterItems);

        allBookListing = new ArrayList<>();
        //Initialise Firebase reference
        realTimeDb = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Booklisting");
        realTimeDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()){
                    bookListing change = snap.getValue(bookListing.class);
                    allBookListing.add(change);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Initialise RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        bookList.setLayoutManager(gridLayoutManager);
        adapter = new Adapter(getContext(),allBookListing,this);
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
                        adapter.getSecondFilter().filter(genre);

                    }
                });
                builder.show();
            }
        });

    }


}