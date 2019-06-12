package com.example.marius.path.data_model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class IndividualPost implements Serializable {
    public enum PostType {
        USER_POST,
        SELF_POST;
    }

    private String title; //5
    private String location; //3
    private String userId; //7
    private String postId;
    private String creationDate; //2
    private String travelDate; //6
    private String nrDays; //4
    private String coverImg; //1
    private Double latitude;
    private Double longitude;

    @Exclude
    private PostType type = PostType.SELF_POST;

    @Exclude
    public PostType getType() {
        return type;
    }

    @Exclude
    public void setType(PostType type) {
        this.type = type;
    }

    public IndividualPost(){}

    public IndividualPost(String creationDate, String travelDate, String location, String nrDays, String title, String userId, String coverImg) {
        this.creationDate = creationDate;
        this.location = location;
        this.travelDate = travelDate;
        this.nrDays = nrDays;
        this.title = title;
        this.userId = userId;
        this.coverImg = coverImg;
    }

    public IndividualPost(Double latitude, Double longitude, String creationDate, String travelDate, String location, String nrDays, String title, String userId, String coverImg) {
        this.creationDate = creationDate;
        this.location = location;
        this.travelDate = travelDate;
        this.nrDays = nrDays;
        this.title = title;
        this.userId = userId;
        this.coverImg = coverImg;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "IndividualPost{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", userId='" + userId + '\'' +
                ", postId='" + postId + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", travelDate='" + travelDate + '\'' +
                ", nrDays='" + nrDays + '\'' +
                ", coverImg='" + coverImg + '\'' +
                '}';
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
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
}
