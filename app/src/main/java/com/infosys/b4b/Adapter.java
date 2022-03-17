package com.infosys.b4b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Adapter works with the data of the book upload process,
// 1. Picture of book
// 2. Title of book
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    //These lists will take the input from mainActivity
    List<String> bookTitles;
    List<Integer> bookImages;
    LayoutInflater inflater;


    public Adapter(Context ctx, List<String> titles, List<Integer> images){
        this.bookTitles = titles;
        this.bookImages = images;
        this.inflater = LayoutInflater.from(ctx);
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
        holder.title.setText(bookTitles.get(position));
        holder.gridpicture.setImageResource(bookImages.get(position));

    }

    @Override
    public int getItemCount() {
        return bookTitles.size();
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
}
