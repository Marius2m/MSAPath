package com.example.marius.path.data_model;

import java.io.Serializable;

public class IndividualPost implements Serializable {
    private String title;
    private String location;
    private String userId;
    private String postId;
    private Long creationDate;
    private String travelDate;
    private String nrDays;
    private String coverImg;

    public IndividualPost(){}

    public IndividualPost(Long creationDate, String travelDate, String location, String nrDays, String title, String userId, String coverImg) {
        this.creationDate = creationDate;
        this.location = location;
        this.travelDate = travelDate;
        this.nrDays = nrDays;
        this.title = title;
        this.userId = userId;
        this.coverImg = coverImg;
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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
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
}
