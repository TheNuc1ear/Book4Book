package com.infosys.b4b;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    ValueEventListener readListener;

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
        userId = intent.getStringExtra("userid");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

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
                userData user = snapshot.getValue(userData.class);
                username.setText(user.getUsername());
                profile_img.setImageResource(R.mipmap.ic_launcher);
//                if (user.getImageURL().equals("default")){
//                    profile_img.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_img);
//                }
//                readMsg(fUser.getUid(), userId, user.getImageURL());
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

        // ADD USER TO CHAT FRAGMENT IF ALREADY HAVE CHAT HISTORY
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

//    private void status (String status){
//        ref = FirebaseDatabase.getInstance().getReference("userData").child(fUser.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//        ref.updateChildren(hashMap);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        status("Online");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        ref.removeEventListener(readListener);
//        status("Offline");
//    }
}