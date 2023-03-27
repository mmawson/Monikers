package com.example.monikers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private List<String> mData;

    public MyRecyclerAdapter(List<String> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData.get(position);
        holder.textView.setText(item);
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current like count
                int currentLikes = Integer.parseInt(holder.likeCount.getText().toString().trim().split(" ")[0]);

                // Increment the like count and update the TextView
                int newLikes = currentLikes + 1;
                holder.likeCount.setText(newLikes + " likes");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView likeBtn;
        public TextView likeCount;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_word);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            likeCount = itemView.findViewById(R.id.likeCount);
        }
    }
}