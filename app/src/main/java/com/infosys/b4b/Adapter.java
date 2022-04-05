package com.infosys.b4b;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

//Adapter works with the data of the book upload process,
// 1. Picture of book
// 2. Title of book
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {
    //These lists will take the input from mainActivity
    List<bookListing> listings;
    List<bookListing> fullListings; //Just another list to store all the original listings to be used to get filter list
    LayoutInflater inflater;
    private ItemClickListener listener;



    public Adapter(Context ctx, List<bookListing> listings, ItemClickListener listener){
        this.listings = listings;
        fullListings = new ArrayList<>(listings);
        this.inflater = LayoutInflater.from(ctx);
        this.listener= listener;
    }

    //parent is the RecyclerView in activity_main.xml
    //returns the custom_grid_layout.xml, which will be used to contain the details of the listing
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout,parent,false);
        return new ViewHolder(view);
    }

    //Set the Picture and Book Title uploaded by user to the custom_grid_layout
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //This gives us the exact url of the the respective bookListings
        Task<Uri> url = FirebaseStorage.getInstance().getReference().child("images/" + listings.get(position).getListingId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            //If Successfully got the url, pass the url into Glide and load into ImageView
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.gridpicture.getContext()).load(uri).into(holder.gridpicture);
            }
        });
        //holder.gridpicture.setImageResource(R.drawable.giannis);
        holder.title.setText(listings.get(position).getNameOfBook());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                listener.onItemClick(listings.get(position).getNameOfBook());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }



    //Class of ViewHolder which is each grid
    //Attribute of ImageView which is meant for the the picture of the book
    //TextView for the title of the book
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView gridpicture;
        //itemView is the custom_grid_layout
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            gridpicture = itemView.findViewById(R.id.imageView4);

            }


        }



    @Override
    public Filter getFilter() {
        return firstFilter;
    }
    //Filter Class

    private Filter firstFilter = new Filter() {
        //performFiltering auto executed on background thread, so no lag even if complicated filter conditions
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (fullListings.size()==0){
                fullListings = new ArrayList<>(listings);
            }
            List<bookListing> filteredList = new ArrayList<>();
            if (constraint==null||constraint.length()==0){
                filteredList.addAll(fullListings);
            } else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (bookListing item: fullListings){
                    if(item.getNameOfBook().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listings.clear();
            listings.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    //get second filter
    public Filter getSecondFilter() {
        return secondFilter;
    }


    private Filter secondFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (fullListings.size()==0){
                fullListings = new ArrayList<>(listings);
            }
            List<bookListing> filteredList = new ArrayList<>();
            if (constraint==null||constraint.length()==0){
                filteredList.addAll(fullListings);
            } else{
                String filterPattern = constraint.toString();
                for (bookListing item:fullListings){
                    if(item.getGenre().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            listings.clear();
            listings.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static interface ItemClickListener {
        public void onItemClick(String str);
    }
}
