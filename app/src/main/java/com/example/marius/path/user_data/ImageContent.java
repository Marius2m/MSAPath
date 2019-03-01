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

    public ImageContent(int id){
        this.id = id;
    }

    public ImageContent(String image, int id){
        this.image = image;
        this.id = id;
    }

    public void setImageRef(String ref){ this.image = ref; }

    @Override
    public String toString() {
        return image;
    }

    @Override @Exclude
    public String getContent() {
        return image;
    }
}
