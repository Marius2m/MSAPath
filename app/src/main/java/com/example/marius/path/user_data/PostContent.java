package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public abstract class PostContent implements Serializable {
    public String content;

    @Exclude
    protected static final int VIEW_TYPE_PARAGRAPH = 1;
    @Exclude
    protected static final int VIEW_TYPE_IMAGE = 2;

    public PostContent(){}

    @Exclude
    public  String getType(){return "String";}

    public String getContent() {
        return content;
    }

    public void setContent(String content){};

    @Exclude
    public abstract int getViewType();

}
