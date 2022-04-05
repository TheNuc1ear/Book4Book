package com.infosys.b4b;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

//This is for the Adapter for "My Listings" which is used to inflate the activity_mybooks
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    List<bookListing> listings;

    public BookAdapter(List<bookListing> bookListings) {
        this.listings = bookListings;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bookName.setText(listings.get(position).getNameOfBook());
        holder.bookDescription.setText(listings.get(position).getDescriptionOfBook());
        //holder.bookGenre.setText(model.getGenreOfBook());
        Task<Uri> url = FirebaseStorage.getInstance().getReference().child("images/" + listings.get(position).getListingId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            //If successful, load into ImageView
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.bookImage.getContext()).load(uri).into(holder.bookImage);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mylisting, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        TextView bookName, bookDescription, bookGenre;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            bookName = itemView.findViewById(R.id.bookName);
            bookDescription = itemView.findViewById(R.id.bookDescription);
            bookGenre = itemView.findViewById(R.id.bookGenre);
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
