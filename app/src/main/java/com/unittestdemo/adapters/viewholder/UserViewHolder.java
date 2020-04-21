package com.unittestdemo.adapters.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.unittestdemo.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public AppCompatImageView ivProfile;
    public TextView tvName, tvEmail;

    public UserViewHolder(View itemView) {
        super(itemView);
        this.ivProfile = itemView.findViewById(R.id.ivProfile);
        this.tvName = (TextView) itemView.findViewById(R.id.tvName);
        this.tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
    }
}