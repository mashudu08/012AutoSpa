package com.example.a012autospa;

import android.media.Image;
import android.widget.ImageView;

import java.sql.Blob;

public class User {
    private String name;
    private String number;
    private String email;
    private String password;
    private String profilePictureUrl;

    public User(){
    }

    public User(String inputName, String inputNumber, String inputEmail, String inputPass, String inputPictureUrl) {
        this.name = inputName;
        this.email = inputEmail;
        this.password = inputPass;
        this.number = inputNumber;
        this.profilePictureUrl = inputPictureUrl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
