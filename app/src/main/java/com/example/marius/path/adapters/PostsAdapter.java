package com.example.marius.path.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.R;
import com.example.marius.path.SinglePostActivity;
import com.example.marius.path.data_model.IndividualPost;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.CustomViewHolder> {
    private List<IndividualPost> posts;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView location, author, postThumbnailTitle;
        public CardView cardView;

        public CustomViewHolder(View view){
            super(view);
            location = (TextView) view.findViewById(R.id.postLocationDays);
            author = (TextView) view.findViewById(R.id.authorPost);
            postThumbnailTitle = (TextView) view.findViewById(R.id.postThumbnailTitle);
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
        context = parent.getContext();
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.CustomViewHolder holder, int position) {
        final IndividualPost post = posts.get(position);

        holder.location.setText(post.getLocation() + " in " + post.getNrDays() + " days");
        //holder.author.setText("by " + post.getUserId() + ", " + post.getCreationDate());
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Integer.parseInt(post.getCreationDate()));
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        holder.author.setText(date);
        holder.postThumbnailTitle.setText(post.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SinglePostActivity.class);
                i.putExtra("postId",post.getPostId());
                context.startActivity(i);
                Toast.makeText(v.getContext(), post.getPostId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
