package com.infosys.b4b;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class bookListing {
    private String nameOfBook;
    private String descriptionOfBook; //Maybe just change the description to what the lister is interested in?
    private String genreList;
    //Possibly add what genre
    //private List<String> genreList=new ArrayList<>();
    //For now ignore userId
    //private int userId;
    private String listingId;
    private int bookImage;

    //Store the list of ListingId so that no repeat
    public static int currentListingId=0;

    public bookListing(String nameOfBook, String descriptionOfBook, String genre){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = descriptionOfBook;
        this.genreList=genre;
        this.listingId = assignListingId();
    }
    public bookListing(String nameOfBook, String descriptionOfBook, int bookImage){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = descriptionOfBook;
        this.bookImage = bookImage;
        this.listingId = assignListingId();
    }
    public bookListing(String nameOfBook,int bookImage){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = "Empty";
        this.bookImage = bookImage;
        this.listingId = assignListingId();
    }

    public static String assignListingId(){
        currentListingId+=1;
        return String.valueOf(currentListingId);
    }

    public String getNameOfBook(){
        return this.nameOfBook;
    }
    public void setNameOfBook(String input){
        this.nameOfBook = input;
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
