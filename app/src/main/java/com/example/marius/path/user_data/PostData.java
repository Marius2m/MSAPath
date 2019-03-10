package com.example.marius.path.user_data;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class PostData implements Serializable{
    public String title, location, travelDate, nrDays;
    public String creationDate;
    public String userId;
    public String postText;
    public String coverImg;

    public PostData(){}

    public PostData(String userId, String title, String location, String date, String nrDays,
                    String creationDate, String coverImg){
        this.userId = userId;
        this.title = title;
        this.location = location;
        this.travelDate = date;
        this.nrDays = nrDays;
        this.creationDate = creationDate;
        this.coverImg = "";
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void printContent(){
        System.out.println(this.toString());
//        for (PostContent pc: contents) {
//            System.out.println("id:" + pc.getId() + "content:" + pc.getContent());
//        }
    }

    @Override
    public String toString(){
        return "Title: " + title + " - location: " + location + " - date: " + travelDate
                + " - nrTravelers: " + nrDays + " - postText: " + postText;
    }

}
