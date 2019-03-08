package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public abstract class PostContent implements Serializable {
    public String content;

    @Exclude
    protected int id = 0;

    public PostContent(){}

    @Exclude
    public  String getType(){return "String";}

    public String getContent() {
        return content;
    }

    public void setContent(String content){};

    @Exclude
    public int getId(){
        return id;
    }


}
