package com.example.marius.path.data_model;

import java.util.ArrayList;

public class GlobePosts {
    String version;
    String message;
    String prevPostId;
    ArrayList<IndividualPost> posts;

    public String getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
    }

    public String getPrevPostId() {
        return prevPostId;
    }

    public ArrayList<IndividualPost> getPosts() {
        return posts;
    }
}
