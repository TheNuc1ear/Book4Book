package com.infosys.b4b;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link myBooks_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myBooks_Fragment extends Fragment{

    // TODO: Rename and change types of parameters
    private bookListing listing;

    public myBooks_Fragment() {
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
    public static myBooks_Fragment newInstance(bookListing listing) {
        myBooks_Fragment fragment = new myBooks_Fragment();
        Bundle args = new Bundle();
        args.putSerializable("BookListing",listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listing = (bookListing) getArguments().getSerializable("BookListing");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this my books fragment
        return inflater.inflate(R.layout.fragment_my_books_fragment, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.titleOfBook);
        TextView genre = view.findViewById(R.id.genreOfBook);
        TextView user = view.findViewById(R.id.userOfBook);
        TextView desc = view.findViewById(R.id.descOfBook);
        ImageView img = view.findViewById(R.id.picOfBook);

        title.setText(listing.getNameOfBook());
        user.setText(listing.getUseruid());
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
    }
    public String getGenres(){
        String out = "";
        for (String s: this.listing.getGenre()){
            out+=s+", ";
        }
        out=out.substring(0, out.length()-2);
        return out;
    }
}