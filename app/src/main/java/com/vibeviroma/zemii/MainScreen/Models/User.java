package com.vibeviroma.zemii.MainScreen.Models;

public class User {
    String user_name, user_surname, contact, sexe, user_thumb_image, user_image;

    public User() {
    }

    public User(String user_name, String user_surname, String contact, String sexe, String user_thumb_image, String user_image) {
        this.user_name = user_name;
        this.user_surname = user_surname;
        this.contact = contact;
        this.sexe = sexe;
        this.user_thumb_image = user_thumb_image;
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_surname() {
        return user_surname;
    }

    public void setUser_surname(String user_surname) {
        this.user_surname = user_surname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String toString(){
        return user_name+"\t"+user_surname+"\t"+user_image+"\t"+user_thumb_image+"\t"+contact+"\t"+sexe;
    }
}
