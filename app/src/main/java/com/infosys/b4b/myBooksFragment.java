package com.infosys.b4b;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link myBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myBooksFragment extends Fragment{

    private bookListing listing;
    private DatabaseReference databaseReference;

    public myBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listing listing
     * @return A new instance of fragment myBooks_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static myBooksFragment newInstance(bookListing listing) {
        myBooksFragment fragment = new myBooksFragment();
        Bundle args = new Bundle();
        //Custom Constructor takes in a bookListing Object when navigating from home_Fragment to myBooks_Fragment
        args.putSerializable("BookListing",listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Unserialize the bookListing object that was passed into myBooks_Fragment
            listing = (bookListing) getArguments().getSerializable("BookListing");
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference("userData");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this my books fragment
        return inflater.inflate(R.layout.fragment_my_books_fragment, container, false);
    }

    //Unpack and display the bookListing object's attribute to the respective fields of the Layout
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.titleOfBook);
        TextView genre = view.findViewById(R.id.genreOfBook);
        TextView user = view.findViewById(R.id.userOfBook);
        TextView desc = view.findViewById(R.id.descOfBook);
        ImageView img = view.findViewById(R.id.picOfBook);
        Button button = view.findViewById(R.id.tradeBtn);

        title.setText(listing.getNameOfBook());
        //Retrieving the userData with the corresponding useruid of the bookListing
        databaseReference.child(listing.getUseruid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                userData userObject = dataSnapshot.getValue(userData.class);
                user.setText(userObject.getUsername());
            }
        });
        desc.setText(listing.getDescriptionOfBook());
        // Method to get genres( may be more than 1)
        String out = getGenres();
        genre.setText(out);
        Task<Uri> url = FirebaseStorage.getInstance().getReference().child("images/" + listing.getListingId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            //If Successfully got the url, pass the url into Glide and load into ImageView
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(img.getContext()).load(uri).into(img);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid()!=listing.getUseruid()){
                    Intent intent = new Intent(getContext(), messageActivity.class);
                    intent.putExtra("userid", listing.getUseruid());
                    getContext().startActivity(intent);
                }

            }
        });
    }

    //Just a quick method to display the genre of the book in a presentable format.
    public String getGenres(){
        String out = "";
        for (String s: this.listing.getGenreOfBook()){
            out+=s+", ";
        }
        out=out.substring(0, out.length()-7);
        return out;
    }
}