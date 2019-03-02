package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public abstract class PostContent implements Serializable {
    @Exclude
    protected int id = 0;

    @Exclude
    public abstract String getType();

    public String getContent() {
        return content;
    }

    public abstract void setContent(String content);

    @Exclude
    public int getId(){
        return id;
    }

    public String content;

    public PostContent(){}
}
