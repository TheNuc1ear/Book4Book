package com.infosys.b4b;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class daoUserData {
    private DatabaseReference databaseReference;
    public daoUserData(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://book4book-862cd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference(userData.class.getSimpleName());
    }
    public Task<Void> add(userData user){
        return databaseReference.push().setValue(user);
    }
}
