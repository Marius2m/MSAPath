package com.example.marius.path.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GlobePostsAdapter extends RecyclerView.Adapter<GlobePostsAdapter.CustomViewHolder> {
    private List<IndividualPost> posts;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnail;
        public TextView location, author, postThumbnailTitle;
        public CardView cardView;
        public ImageView coverImg;

        public CustomViewHolder(View view){
            super(view);
            location = view.findViewById(R.id.postLocationDays);
            author = view.findViewById(R.id.authorPost);
            postThumbnailTitle = view.findViewById(R.id.postThumbnailTitle);
            cardView = view.findViewById(R.id.cardPostView);
            coverImg = view.findViewById(R.id.coverImg);
        }
    }

    public GlobePostsAdapter(List<IndividualPost> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public GlobePostsAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.globe_post, parent, false);
        context = parent.getContext();
        return new GlobePostsAdapter.CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobePostsAdapter.CustomViewHolder holder, int position) {
        final IndividualPost post = posts.get(position);

        holder.location.setText(post.getLocation() + " in " + post.getNrDays() + " days");
        holder.author.setText(post.getTravelDate());
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(Long.parseLong(post.getCreationDate()));
//        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        holder.postThumbnailTitle.setText(post.getTitle());
        Picasso.get()
                .load(Uri.parse(post.getCoverImg()))
                .centerCrop()
                .fit()
                .into(holder.coverImg);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), SinglePostActivity.class);

            System.out.println("SENDING TO NEW PAGE:" + post.toString());
            i.putExtra("postObject",(Serializable) post);

            context.startActivity(i);
            Toast.makeText(v.getContext(), post.getPostId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
