package com.example.marius.path.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marius.path.R;
import com.example.marius.path.data_model.IndividualPost;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.CustomViewHolder> {
    private List<IndividualPost> posts;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView title, author;
        public CardView cardView;

        public CustomViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.titlePost);
            author = (TextView) view.findViewById(R.id.authorPost);
            cardView = (CardView) view.findViewById(R.id.cardPostView);
        }
    }

    public PostsAdapter(List<IndividualPost> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostsAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.CustomViewHolder holder, int position) {
        IndividualPost post = posts.get(position);

        holder.title.setText(post.getLocation() + " in " + post.getNrDays() + " days");
        holder.author.setText("by " + post.getAuthor() + ", " + post.getCreationDate());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
