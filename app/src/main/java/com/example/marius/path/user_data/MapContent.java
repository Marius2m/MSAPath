package com.example.marius.path.user_data;

public class MapContent extends PostContent {
    public String map;

    public MapContent(String map){
        this.map = map;
    }

    @Override
    public String toString() {
        return map;
    }
}
