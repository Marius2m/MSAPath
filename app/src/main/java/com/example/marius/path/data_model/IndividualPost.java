package com.example.marius.path.data_model;

import com.example.marius.path.user_data.PostContent;

import java.util.ArrayList;

public class IndividualPost {
    private String creationDate;
    private String travelDate;
    private String location;
    private String nrDays;
    private String title;
    private String author;
    private String thumbnail;
    public ArrayList<PostContent> contents = new ArrayList<PostContent>();

    public IndividualPost(String creationDate, String travelDate, String location, String nrDays, String title, String author) {
        this.creationDate = creationDate;
        this.travelDate = travelDate;
        this.location = location;
        this.nrDays = nrDays;
        this.title = title;
        this.author = author;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<PostContent> getContents() {
        return contents;
    }

    public void setContents(ArrayList<PostContent> contents) {
        this.contents = contents;
    }
}
