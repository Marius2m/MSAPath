package com.example.marius.path.user_data;

import java.io.Serializable;

public class PostData implements Serializable{
    public String title, location, travelDate, nrDays;
    public String creationDate;
    public String userId;
    public String postText;
    public String coverImg;
    public Double latitude;
    public Double longitude;

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
        this.latitude = null;
        this.longitude = null;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
