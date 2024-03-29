package com.infosys.b4b;

import java.util.ArrayList;

public class userData  {
    private String email;
    private String username;
    private String id;
    private ArrayList<String> userListing = new ArrayList<String>();

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public userData(){

    }

    // This constructor is called when registering a new user
    public userData(String email, String username, String id) {
        this.email = email;
        this.username = username;
        this.id = id;
    }

    // Getters and Setters as required
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }
}
