package com.infosys.b4b;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class bookListing {
    private String nameOfBook;
    private String descriptionOfBook; //Maybe just change the description to what the lister is interested in?
    private String genre;
    //Possibly add what genre
    //private List<String> genreList=new ArrayList<>();
    //For now ignore userId
    //private int userId;
    private String listingId;
    private int bookImage;

    public bookListing(String nameOfBook, String descriptionOfBook, String genre){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = descriptionOfBook;
        this.genre=genre;
        this.listingId = assignListingId();
    }

    public String assignListingId(){
        final String randomKey = UUID.randomUUID().toString();
        return randomKey;
    }

    public String getNameOfBook(){
        return this.nameOfBook;
    }
    public void setNameOfBook(String input){
        this.nameOfBook = input;
    }

    public String getGenreOfBook(){
        return this.genre;
    }
    public void setGenreOfBook(String input){
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

}
