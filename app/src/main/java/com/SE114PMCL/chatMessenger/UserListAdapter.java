package com.SE114PMCL.chatMessenger;

import android.content.Context;
import android.icu.util.Freezable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.schwaab.avvylib.AvatarView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    ArrayList<FriendData> listUser;
    private OnUserListener mOnUserListener;

    public UserListAdapter(Context context, ArrayList<FriendData> listUser,OnUserListener onUserListener) {
        this.context = context;
        this.listUser = listUser;
        this.mOnUserListener=onUserListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // gán view
        View view = LayoutInflater.from(context).inflate(R.layout.user_view, parent, false);
        return new ViewHolder(view,mOnUserListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gán dữ liêuk
        FriendData friend = listUser.get(position);
        holder.txtTenUser.setText(friend.getTenUser());
        holder.txtChatMessage.setText(friend.getLastMessage());
        holder.avatarView.setShowBadge(friend.getActiveStatus());
        holder.imgAvatar.setImageResource(friend.getAvatar());
    }

    @Override
    public int getItemCount() {
        return listUser.size(); // trả item tại vị trí postion
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView imgAvatar;
        AvatarView avatarView;
        TextView txtTenUser,txtChatMessage;
        OnUserListener onUserListener;
        public ViewHolder(@NonNull View itemView,OnUserListener onUserListener) {
            super(itemView);
            // Ánh xạ view
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            avatarView=itemView.findViewById(R.id.activeStatus);
            txtTenUser = itemView.findViewById(R.id.txtTenUser);
            txtChatMessage=itemView.findViewById(R.id.txtChatMessage);
            this.onUserListener=onUserListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserListener.onUserClick(getBindingAdapterPosition());
        }
    }
    public interface OnUserListener{
        void onUserClick(int position);
    }
}

