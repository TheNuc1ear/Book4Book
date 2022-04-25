package com.infosys.b4b;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

//This is for the Adapter for "My Listings" which is used to inflate the activity_mybooks
public class adapterProfile extends RecyclerView.Adapter<adapterProfile.ViewHolder> {

    List<bookListing> listings;

    public adapterProfile(List<bookListing> bookListings) {
        this.listings = bookListings;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bookName.setText(listings.get(position).getNameOfBook());
        String genre = listings.get(position).getGenreOfBook().toString();
        holder.bookGenre.setText(genre.substring(1, genre.length() - 6));
        StorageReference listingId = FirebaseStorage.getInstance().getReference().child("images/" + listings.get(position).getListingId());
        Task<Uri> url = FirebaseStorage.getInstance().getReference().child("images/" + listings.get(position).getListingId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            //If successful, load into ImageView
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.bookImage.getContext()).load(uri).into(holder.bookImage);
            }
        });

        //For deleting listing
        holder.deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Set a alert dialog to prompt user to confirm their deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.alertDelete);
                builder.setPositiveButton(R.string.confirmYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String listingId = listings.get(position).getListingId();
                        deleteBook(listingId);
                        Toast.makeText(view.getContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                        listings.remove(position);
                        notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton(R.string.confirmNo, null);
                builder.show();
            }
        });
    }

    private void deleteBook(String listingId) {
        DatabaseReference bookid = FirebaseDatabase.getInstance().getReference("Booklisting").child(listingId);
        bookid.removeValue();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mylisting, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        TextView bookName, bookGenre;
        ImageButton deletebutton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            bookName = itemView.findViewById(R.id.bookName);
            bookGenre = itemView.findViewById(R.id.bookGenre);
            deletebutton = itemView.findViewById(R.id.deletebook);
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
