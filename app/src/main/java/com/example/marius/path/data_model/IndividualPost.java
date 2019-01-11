package com.example.marius.path.data_model;

import com.example.marius.path.user_data.PostContent;

import java.util.ArrayList;
import java.util.List;

public class IndividualPost {
    private String title;
    private String location;
    private String userId;
    private String creationDate;
    private String travelDate;
    private String nrDays;
    private String thumbnail;
    public List<PostContent> contents = new ArrayList<PostContent>();

    public IndividualPost(){}

    public IndividualPost(String creationDate, String travelDate, String location, String nrDays, String title, String userId) {
        this.creationDate = creationDate;
        this.location = location;
        this.travelDate = travelDate;
        this.nrDays = nrDays;
        this.title = title;
        this.userId = userId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNrDays() {
        return nrDays;
    }

    public void setNrDays(String nrDays) {
        this.nrDays = nrDays;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<PostContent> getContents() {
        return contents;
    }

    public void setContents(List<PostContent> contents) {
        this.contents = contents;
    }
}
