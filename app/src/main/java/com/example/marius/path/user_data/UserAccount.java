package com.example.marius.path.user_data;

public class UserAccount {
    public String name;
    public int age;
    public String dateCreated;
    public String email;

    public UserAccount(String name, String dateCreated, String email){
        this.name = name;
        this.dateCreated = dateCreated;
        this.email = email;

        this.age = 18;
    }
}
