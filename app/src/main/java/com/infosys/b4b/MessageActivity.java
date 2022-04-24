package com.infosys.b4b;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_img;
    TextView username;
    FirebaseUser fUser;
    DatabaseReference ref;
    ImageButton button;
    EditText text;
    Intent intent;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    String userId;
    String usernameTemp;
    ValueEventListener readListener;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Initialise the db reference path
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("userData");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_img = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        button = findViewById(R.id.send_button);
        text = findViewById(R.id.text_content);

        intent = getIntent();
        // gets id and username from the clicked user (sent from userAdapter)
        userId = intent.getStringExtra("userid");
        usernameTemp = intent.getStringExtra("usernameTemp");

        
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        // Button to send message
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mesg = text.getText().toString();
                if (!mesg.equals("")){
                    sendMessage(fUser.getUid(), userId, mesg);
                } else {
                    Toast.makeText(MessageActivity.this, "Nothing to send!", Toast.LENGTH_SHORT).show();
                }
                text.setText("");
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("userData");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //current soln for when accessing MessageActivity from the myBooks_Fragment
                if (intent.getStringExtra("usernameTemp")==null){
                    databaseReference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            userData user = dataSnapshot.getValue(userData.class);
                            username.setText(user.getUsername());
                        }
                });
                } else{
                    username.setText(usernameTemp);
                }
                profile_img.setImageResource(R.mipmap.ic_launcher);
                readMsg(fUser.getUid(), userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        seenMessage(userId);
    }

    private void seenMessage(String userid){
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        // Checks if other user have read the message
        readListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()){
                    Chat chat = s.getValue(Chat.class);
                    if (chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("read", true);
                        s.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("read", false);

        ref.child("Chats").push().setValue(hashMap);

        // NEW IMPLEMENTATION FOR FUTURE USES (LINE 174-188), CURRENTLY USING THE OLD METHOD OF GOING THROUGH
        // ALL THE CHATS AND CHECKING THE USERID OF THE CURRENT LOGGED-IN USER IN SENDER AND RECEIVER
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ListOfChats")
                .child(fUser.getUid()).child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void readMsg(String ownid, String userid){
        mChat = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                // When a user is clicked, the array list containing the messages will be cleared first
                // Then, it will go through list of chats and put in the messages that have both userId's
                // (Current logged-in user AND user being clicked)
                for (DataSnapshot s: snapshot.getChildren()){
                    Chat chat = s.getValue(Chat.class);
                    if (chat.getReceiver().equals(ownid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(ownid)){
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}