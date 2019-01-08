package com.example.marius.path.user_data;

public class ImageContent extends PostContent{
    public String image;

    public ImageContent(String image){
        this.image = image;
    }

    @Override
    public String toString() {
        return image;
    }
}
