package com.example.marius.path.user_data;

public class UserAccount {
    public String name;
    public int age;
    public String dateCreated;
    public String email;

    public UserAccount(){}

    public UserAccount(String name, String dateCreated, String email){
        this.name = name;
        this.dateCreated = dateCreated;
        this.email = email;

        this.age = 18;
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

    public void setEmail(String email) {
        this.email = email;
    }
}
