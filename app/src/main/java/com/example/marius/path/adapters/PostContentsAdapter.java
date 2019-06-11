package com.example.marius.path.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marius.path.R;
import com.example.marius.path.user_data.PostContent;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class PostContentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PostContent> contents;

    public PostContentsAdapter(List<PostContent> strings) {
        this.contents = strings;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.paragraph_content, parent, false);
            return new ParagraphViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_content, parent, false);
            return new ImageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostContent s = contents.get(position);

        if (holder.getItemViewType() == 1) {
            ((ParagraphViewHolder) holder).paragraph_text_view.setText(s.getContent());
        } else {
            Picasso.get()
                    .load(Uri.parse(s.getContent()))
                    .into(((ImageViewHolder) holder).image_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        PostContent s = contents.get(position);

        return s.getViewType();
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class ParagraphViewHolder extends RecyclerView.ViewHolder {
        TextView paragraph_text_view;

        public ParagraphViewHolder(View itemView) {
            super(itemView);
            this.paragraph_text_view = itemView.findViewById(R.id.paragraph_text_view);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image_view = itemView.findViewById(R.id.image_image_view);
        }
    }
}
