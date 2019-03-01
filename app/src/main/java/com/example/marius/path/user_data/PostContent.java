package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class PostContent implements Serializable {
    @Exclude
    protected int id = 0;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Exclude
    public int getId(){
        return id;
    }

    public String content;

    public PostContent(){}
}
