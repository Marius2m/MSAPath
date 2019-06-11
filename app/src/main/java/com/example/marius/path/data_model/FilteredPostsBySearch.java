package com.example.marius.path.data_model;

import java.util.ArrayList;

public class FilteredPostsBySearch {
    String version;
    String message;
    String prevSortLocation;
    ArrayList<IndividualPost> posts;

    public String getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
    }

    public String prevSortLocation() {
        return prevSortLocation;
    }

    public ArrayList<IndividualPost> getPosts() {
        return posts;
    }
}
