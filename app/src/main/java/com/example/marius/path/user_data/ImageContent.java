package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

public class ImageContent extends PostContent{
    public String image = "emptyUri";

    @Exclude
    private static final String contentType = "image";

    public ImageContent() {}

    public ImageContent(String image){
        this.image = image;
    }

    public ImageContent(String image, int id){
        this.image = image;
    }

    @Exclude @Override
    public String getType(){
        return contentType;
    }

    @Override
    public void setContent(String ref){ this.image = ref; }

    @Override @Exclude
    public int getViewType() {
        return this.VIEW_TYPE_IMAGE;
    }

    @Override
    public String toString() {
        return image;
    }

    @Override @Exclude
    public String getContent() {
        return image;
    }
}
