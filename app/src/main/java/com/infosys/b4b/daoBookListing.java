package com.infosys.b4b;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class daoBookListing {
    private DatabaseReference databaseReference;
    public daoBookListing(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference("Booklisting");
    }
    public Task<Void> add(bookListing book){
        return databaseReference.child(book.getListingId()).setValue(book);
    }
}
