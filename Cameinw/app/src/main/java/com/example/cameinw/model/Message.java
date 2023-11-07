package com.example.cameinw.model;

import android.graphics.Bitmap;

import java.time.LocalDateTime;

public class Message {
    private Integer id;
    private String message;
    private User sender;
    private User receiver;
    private String timestamp;
    private Bitmap userImage = null;

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getMessage() {return message;}

    public void setMessage(String text) {this.message = text;}

    public User getSender() {return sender;}

    public void setSender(User sender) {this.sender = sender;}
    public User getReceiver() {return receiver;}

    public void setReceiver(User receiver) {this.receiver = receiver;}
    public String getTimestamp() {return timestamp;}

    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}

    public Bitmap getUserImage() {return userImage;}

    public void setUserImage(Bitmap userImage) {this.userImage = userImage;}
}
