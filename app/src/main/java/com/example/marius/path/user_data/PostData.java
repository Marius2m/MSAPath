package com.example.marius.path.user_data;

import java.io.Serializable;

public class PostData implements Serializable{
    public String title, location, date, nrTravelers;
    public String postText;

    public PostData(String title, String location, String date, String nrTravelers) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.nrTravelers = nrTravelers;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    @Override
    public String toString(){
        return "Title: " + title + " - location: " + location + " - date: " + date
                + " - nrTravelers: " + nrTravelers + " - postText: " + postText;
    }

}
