package com.unittestdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.unittestdemo.R;
import com.unittestdemo.adapters.viewholder.UserViewHolder;
import com.unittestdemo.model.UserModel;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private List<UserModel> list;
    private Context context;
    private int size = 0;

    // RecyclerView recyclerView;
    public UserListAdapter(Context context, List<UserModel> list, int size) {
        this.list = list;
        this.context = context;
        this.size = size > list.size() || size == 0 ? list.size() : size;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final UserModel userModel = list.get(position);
        holder.tvName.setText(userModel.getName());
        holder.tvEmail.setText(userModel.getEmail());
       /* holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "click on item: " + myListData.getDescription(), Toast.LENGTH_LONG).show();
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return this.size;
    }


}  