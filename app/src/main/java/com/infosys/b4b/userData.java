package com.infosys.b4b;

import java.util.ArrayList;

public class userData  {
    private String email;
    private ArrayList<String> userListing = new ArrayList<String>();

    public userData(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public userData(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
