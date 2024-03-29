package com.SE114PMCL.chatMessenger.Adapter;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SE114PMCL.chatMessenger.Model.ChatData;
import com.SE114PMCL.chatMessenger.Model.UserModel;
import com.SE114PMCL.chatMessenger.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import Main.ChatTab.MessengerActivity;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
  
    private Context mContext;
    private List<UserModel> mUsers;
    private boolean ischat;

    String theLastMessage;

    public UserListAdapter(Context mContext, List<UserModel> mUsers, boolean ischat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_view, parent, false);
        return new UserListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final UserModel user = mUsers.get(position);
        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        holder.username.setText(user.getUsername());

        if (ischat){
            lastMessage(user.getId(), holder.last_msg, holder);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MessengerActivity.class);
            intent.putExtra("userid", user.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username, sendername;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.txtTenUser);
            profile_image = itemView.findViewById(R.id.imgAvatar);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            sendername = itemView.findViewById(R.id.senderName);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg, final UserListAdapter.ViewHolder holder){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatData chat = snapshot.getValue(ChatData.class);
                    if (firebaseUser != null && chat != null) {
                        String recv = chat.getReceiver();
                        String send = chat.getSender();
                        Boolean read = chat.isIsseen();
                        if (recv.equals(firebaseUser.getUid()) && send.equals(userid) ||
                                recv.equals(userid) && send.equals(firebaseUser.getUid())){
                            if(read==false) {holder.last_msg.setTextColor(Color.RED);}
                            if(chat.getType().equals("image")) { theLastMessage = "Gửi hình ảnh";}
                            else {theLastMessage = chat.getMessage();}
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(send).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    UserModel user = snapshot.getValue(UserModel.class);
                                    String tenchat = user.getUsername();
                                    if(send.equals(userid)){holder.sendername.setText(tenchat);}
                                    else {holder.sendername.setText("Bạn:");}
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

