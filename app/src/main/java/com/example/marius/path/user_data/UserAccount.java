package com.example.marius.path.user_data;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class UserAccount {
    public String name;
    public int age;
    public String dateCreated;
    public String email;
    public String profilePictureUrl;

    @Exclude
    public ArrayList<String> postsIds = new ArrayList<>();

    public UserAccount(){}

    public UserAccount(String name, String dateCreated, String email){
        this.name = name;
        this.dateCreated = dateCreated;
        this.email = email;

        this.age = 18;
    }

    public UserAccount(String name, String dateCreated, String email, ArrayList<String> postsIds){
        this.name = name;
        this.dateCreated = dateCreated;
        this.email = email;
        this.postsIds = postsIds;

        this.age = 18;
    }

    @Exclude
    public ArrayList<String> getPostsIds() {
        return postsIds;
    }

    public void setPostsIds(ArrayList<String> postsIds) {
        this.postsIds = postsIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", dateCreated='" + dateCreated + '\'' +
                ", email='" + email + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", postsIds=" + postsIds +
                '}';
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
