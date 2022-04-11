package com.infosys.b4b;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class bookListing implements Serializable {
    private String nameOfBook;
    private String descriptionOfBook; //Maybe just change the description to what the lister is interested in?
    private ArrayList<String> genre=new ArrayList<>();
    private String useruid;
    //Possibly add what genre
    //private List<String> genreList=new ArrayList<>();
    //For now ignore userId
    //private int userId;
    private String listingId;
    private int bookImage;


    //For Javabeans
    public bookListing(){}

    public bookListing(String nameOfBook, String descriptionOfBook, ArrayList<String> genre){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = descriptionOfBook;
        this.genre=genre;
        genre.add("All");
        this.listingId = assignListingId();
        this.useruid = firebaseuseruid();
    }

    public bookListing(String nameOfBook, ArrayList<String> genre){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = "No description";
        this.genre=genre;
        genre.add("All");
        this.listingId = assignListingId();
        this.useruid = firebaseuseruid();
    }
    //For testing
    public bookListing(String nameOfBook, int bookImage){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = descriptionOfBook;
        this.listingId = assignListingId();
        this.useruid = firebaseuseruid();
        this.bookImage = bookImage;
    }

    public String assignListingId(){
        final String randomKey = UUID.randomUUID().toString();
        return randomKey;
    }
    public String firebaseuseruid(){
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String s = currentFirebaseUser.getUid();
        return s;
    }

    public String getNameOfBook(){
        return this.nameOfBook;
    }
    public void setNameOfBook(String input){
        this.nameOfBook = input;
    }

    public ArrayList<String> getGenreOfBook(){
        return this.genre;
    }
    public void setGenreOfBook(ArrayList<String> input){
        this.genre = input;
    }

    public String getDescriptionOfBook(){
        return this.descriptionOfBook;
    }
    public void setDescriptionOfBook(String input){
        this.descriptionOfBook = input;
    }

    public int getBookImage(){
        return this.bookImage;
    }
    public void setBookImage(int input){
        this.bookImage = input;
    }

    public String getListingId(){
        return this.listingId;
    }

    public String getUseruid() {
        return useruid;
    }
}
