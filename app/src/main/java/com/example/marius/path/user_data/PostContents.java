package com.example.marius.path.user_data;

import java.io.Serializable;
import java.util.ArrayList;

public class PostContents implements Serializable {
    private ArrayList<PostContent> postContents = null;

    public PostContents(){}

    public ArrayList<PostContent> getPostContents() {
        return postContents;
    }

    public void setPostContents(ArrayList<PostContent> postContents) {
        this.postContents = postContents;
    }
}
