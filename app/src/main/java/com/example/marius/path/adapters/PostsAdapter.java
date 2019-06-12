package com.example.marius.path.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marius.path.R;
import com.example.marius.path.SinglePostActivity;
import com.example.marius.path.data_model.IndividualPost;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<IndividualPost> posts;
    private Context context;
    private static final int TYPE_USER_POST = 0;
    private static final int TYPE_SELF_POST = 1;

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView location, author, postThumbnailTitle;
        CardView cardView;
        ImageView coverImg;
        ImageButton deleteBtn;

        CustomViewHolder(View view){
            super(view);
            location = view.findViewById(R.id.postLocationDays);
            author = view.findViewById(R.id.authorPost);
            postThumbnailTitle = view.findViewById(R.id.postThumbnailTitle);
            cardView = view.findViewById(R.id.cardPostView);
            coverImg = view.findViewById(R.id.coverImg);
            deleteBtn = view.findViewById(R.id.deleteBtn);
        }
    }

    public class UserPostViewHolder extends RecyclerView.ViewHolder{
        TextView location, author, postThumbnailTitle;
        CardView cardView;
        ImageView coverImg;

        UserPostViewHolder(View view){
            super(view);
            location = view.findViewById(R.id.postLocationDays2);
            author = view.findViewById(R.id.authorPost2);
            postThumbnailTitle = view.findViewById(R.id.postThumbnailTitle2);
            cardView = view.findViewById(R.id.cardPostView2);
            coverImg = view.findViewById(R.id.coverImg2);
        }
    }
    public PostsAdapter(List<IndividualPost> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        context = parent.getContext();

        if (viewType == TYPE_USER_POST) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_post_list, parent, false);
            return new UserPostViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_list, parent, false);
            return new CustomViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_USER_POST) {
            setUserPostWidgets((UserPostViewHolder)holder, position);
        } else {
            setSelfPostWidgets((CustomViewHolder)holder, position);
        }
    }

    private void setSelfPostWidgets(CustomViewHolder holder, int position) {
        final IndividualPost post = posts.get(position);

        holder.location.setText(post.getLocation() + " in " + post.getNrDays() + " days");
        holder.author.setText(post.getTravelDate());
        holder.postThumbnailTitle.setText(post.getTitle());
        Picasso.get()
                .load(Uri.parse(post.getCoverImg()))
                .centerCrop()
                .fit()
                .into(holder.coverImg);

        holder.deleteBtn.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Path")
                    .setMessage("Are you sure you want to delete this path? Action cannot be undone.")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            (dialog, id) -> {
                                String postId = post.getPostId();
                                posts.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeRemoved(position, 1);

                                Intent intent = new Intent("nr-paths-changed");
                                intent.putExtra("position", position);
                                intent.putExtra("postId", postId);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            })
                    .setNegativeButton("No",
                            (dialog, id) -> dialog.cancel());
            final AlertDialog alert = builder.create();
            alert.show();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), SinglePostActivity.class);
            i.putExtra("postObject",(Serializable) post);
            context.startActivity(i);

            Toast.makeText(v.getContext(), post.getPostId(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setUserPostWidgets(UserPostViewHolder holder, int position) {
        final IndividualPost post = posts.get(position);

        holder.location.setText(post.getLocation() + " in " + post.getNrDays() + " days");
        holder.author.setText(post.getTravelDate());
        holder.postThumbnailTitle.setText(post.getTitle());
        Picasso.get()
                .load(Uri.parse(post.getCoverImg()))
                .centerCrop()
                .fit()
                .into(holder.coverImg);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), SinglePostActivity.class);
            i.putExtra("postObject",(Serializable) post);

            context.startActivity(i);
            Toast.makeText(v.getContext(), post.getPostId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemViewType(int position) {
        IndividualPost post = posts.get(position);

        if (post.getType() == IndividualPost.PostType.USER_POST) {
            return TYPE_USER_POST;
        }

        return TYPE_SELF_POST;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
