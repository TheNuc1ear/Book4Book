package com.infosys.b4b;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;


public class chat_Fragment extends Fragment {
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<userData> users;

    FirebaseUser fUser;
    DatabaseReference dRef, userDataRef;

    private List<String> userList;
    private List<userData> globalusers = new ArrayList<>();
    private List<userData> tempglobalusers = new ArrayList<>();

    public static chat_Fragment newInstance() {
        chat_Fragment fragment = new chat_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        users = new ArrayList<>();

        userDataRef = FirebaseDatabase.getInstance().getReference("userData");
        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()){
                    userData user = s.getValue(userData.class);
                    globalusers.add(user);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dRef = FirebaseDatabase.getInstance().getReference("Chats");
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                users.clear();
                tempglobalusers = globalusers;
                for (DataSnapshot s : snapshot.getChildren()) {
                    Chat chat = s.getValue(Chat.class);
                    if (chat.getSender().equals(fUser.getUid())) {
                        for (userData gUser : tempglobalusers) {
                            if (users.contains(gUser)) {
                                continue;
                            }
                            if (gUser.getId().equals(chat.getReceiver())) {
                                users.add(gUser);
                            }
                        }
                    }
                    if (chat.getReceiver().equals(fUser.getUid())) {
                        for (userData gUser : tempglobalusers) {
                            if (users.contains(gUser)) {
                                continue;
                            }
                            if (gUser.getId().equals(chat.getSender())) {
                                users.add(gUser);
                            }

                        }

                    }

                }
                userAdapter = new UserAdapter(getContext(), users);
                recyclerView.setAdapter(userAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

//    private void readChats(){
//        global users = new ArrayList<>();
//        dRef = FirebaseDatabase.getInstance().getReference("userData");
//        dRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users.clear();
//
//                for (DataSnapshot s: snapshot.getChildren()){
//                    userData user = s.getValue(userData.class);
//                    for (String id: userList){
//                        if (user.getId().equals(id)){
//                            if (users.size() != 0){
//                                for (userData user1: users){
//                                    if (!user.getId().equals(user1.getId())){
//                                        users.add(user);
//                                        break;
//                                    }
//                                }
//                            } else {
//                                users.add(user);
//                                break;
//                            }
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(), users);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

}

