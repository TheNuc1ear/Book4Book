package com.infosys.b4b;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {
    private Context mContext;
    private List<userData> mUsers;
    private boolean isChat;

    public userAdapter(Context mContext, List mUsers){
        this.mContext = mContext;
        this.mUsers =mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new userAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final userData user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        holder.profile_img.setImageResource(R.mipmap.ic_launcher);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData userClicked = mUsers.get(holder.getBindingAdapterPosition());
                Intent intent = new Intent(mContext, messageActivity.class);
                intent.putExtra("userid", userClicked.getId());
                intent.putExtra("usernameTemp", userClicked.getUsername());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_img = itemView.findViewById(R.id.profile_image);
        }
    }
}
