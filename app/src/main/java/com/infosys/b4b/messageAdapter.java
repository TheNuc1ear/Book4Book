package com.infosys.b4b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;

    FirebaseUser fUser;

    public messageAdapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public messageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new messageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        // Puts Delivered / Read info ONLY for most recent text
        if (position == mChat.size()-1){
            if (chat.isRead()){
                holder.text_seen.setText("Read");
            } else {
                holder.text_seen.setText("Delivered");
            }
        } else {
            holder.text_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_img;
        public TextView text_seen;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_img = itemView.findViewById(R.id.profile_image);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        // Checks the list of messages in mChat is from the sender and showing it
        // either on the right if its from logged-in user
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
