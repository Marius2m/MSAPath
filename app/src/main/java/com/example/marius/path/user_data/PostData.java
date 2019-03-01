package com.example.marius.path.user_data;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class PostData implements Serializable{
    public String title, location, travelDate, nrDays;
    public String creationDate;
    public String userId;
    public String postText;


    public ArrayList<PostContent> contents = new ArrayList<PostContent>();

    public PostData(){}

    public PostData(String userId, String title, String location, String date, String nrDays, String creationDate) {
        this.userId = userId;
        this.title = title;
        this.location = location;
        this.travelDate = date;
        this.nrDays = nrDays;
        this.creationDate = creationDate;
    }

    public void addPostContent(PostContent postContent){
        contents.add(postContent);
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    @Exclude
    public ArrayList<PostContent> getPostContent(){
        return this.contents;
    }

    public void printContent(){
        System.out.println(this.toString());
        for (PostContent pc: contents) {
            System.out.println("id:" + pc.getId() + "content:" + pc.getContent());
        }
    }

    @Override
    public String toString(){
        return "Title: " + title + " - location: " + location + " - date: " + travelDate
                + " - nrTravelers: " + nrDays + " - postText: " + postText;
    }

}
