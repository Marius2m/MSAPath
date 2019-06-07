package com.example.marius.path.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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
        public ImageView coverImg;
        public ImageButton deleteBtn;

        public CustomViewHolder(View view){
            super(view);
            location = view.findViewById(R.id.postLocationDays);
            author = view.findViewById(R.id.authorPost);
            postThumbnailTitle = view.findViewById(R.id.postThumbnailTitle);
            cardView = view.findViewById(R.id.cardPostView);
            coverImg = view.findViewById(R.id.coverImg);
            deleteBtn = view.findViewById(R.id.deleteBtn);
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
        cal.setTimeInMillis(Long.parseLong(post.getCreationDate()));
        System.out.println("CAL: " + cal.toString());
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        holder.author.setText(post.getTravelDate());
        holder.postThumbnailTitle.setText(post.getTitle());
        Picasso.get()
                .load(Uri.parse(post.getCoverImg()))
                .centerCrop()
                .fit()
                .into(holder.coverImg);

        holder.deleteBtn.setOnClickListener(v -> {
//                DialogFragment dialog = new DeletePostDialog();
//                dialog.setTargetFragment(this, );
//                dialog.show(etFragmentManager(), "tag");
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SinglePostActivity.class);

                System.out.println("SENDING TO NEW PAGE:" + post.toString());
                i.putExtra("postObject",(Serializable) post);

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
