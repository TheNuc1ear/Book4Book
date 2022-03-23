package com.infosys.b4b;



import java.util.ArrayList;
import java.util.Random;

public class bookListing {
    private String nameOfBook;
    private String descriptionOfBook; //Maybe just change the description to what the lister is interested in?
    //Possibly add what genre
    //private ArrayList<String> genreList;
    //For now ignore userId
    //private int userId;
    private String listingId;
    private int bookImage;

    //Store the list of ListingId so that no repeat
    public static ArrayList<String> listofListingId;

    public bookListing(String nameOfBook, String descriptionOfBook, int bookImage){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = descriptionOfBook;
        this.bookImage = bookImage;
        this.listingId = generateListingId();
    }
    public bookListing(String nameOfBook,int bookImage){
        this.nameOfBook = nameOfBook;
        this.descriptionOfBook = "Empty";
        this.bookImage = bookImage;
        this.listingId = generateListingId();
    }

    private String generateListingId(){
        Random rng = new Random();
        String listId = String.valueOf(rng.nextInt(100000));//Limited to 100k booklistingId
        while (listofListingId.contains(listId)){
            listId = String.valueOf(rng.nextInt(100000));
        }
        listofListingId.add(listId);
        return listId;
    }

    private String getNameOfBook(){
        return this.nameOfBook;
    }
    private void setNameOfBook(String input){
        this.nameOfBook = input;
    }

    private String getDescriptionOfBook(){
        return this.descriptionOfBook;
    }
    private void setDescriptionOfBook(String input){
        this.descriptionOfBook = input;
    }

    private int getBookImage(){
        return this.bookImage;
    }
    private void setBookImage(int input){
        this.bookImage = input;
    }

}
