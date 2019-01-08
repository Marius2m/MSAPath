package com.example.marius.path.user_data;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class PostData implements Serializable{
    public String title, location, travelDate, nrTravelers, creationDate;
    public String postText;

    public ArrayList<PostContent> contents = new ArrayList<PostContent>();

    public PostData(String title, String location, String date, String nrTravelers, String creationDate) {
        this.title = title;
        this.location = location;
        this.travelDate = date;
        this.nrTravelers = nrTravelers;
        this.creationDate = creationDate;
    }

    public void addPostContent(PostContent postContent){
        contents.add(postContent);
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void printContent(){
        Log.d("Page1", this.toString());
        for (PostContent pc: contents) {
            Log.d("pc:", contents.toString());
        }
    }

    @Override
    public String toString(){
        return "Title: " + title + " - location: " + location + " - date: " + travelDate
                + " - nrTravelers: " + nrTravelers + " - postText: " + postText;
    }

}
