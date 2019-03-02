package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

public class MapContent extends PostContent {
    public String map;
    @Exclude
    private static final String contentType = "map";

    @Exclude @Override
    public String getType(){
        return contentType;
    }

    @Override
    public void setContent(String map){
        this.map = map;
    }

    public MapContent(String map){
        this.map = map;
    }

    @Override
    public String toString() {
        return map;
    }
}
