package com.example.cameinw.dto;

import android.graphics.Bitmap;

import java.time.LocalDateTime;

public class MessageDTO {
    private Integer senderId;
    private String senderUsername;
    private String senderImageName;
    private Integer receiverId;
    private String receiverUsername;
    private String receiverImageName;
    private String message;
    private String messageTimestamp;
    private Bitmap senderImage;
    private Bitmap receiverImage;


    public Integer getSenderId() {
        return senderId;
    }
    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }
    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getSenderImageName() {
        return senderImageName;
    }
    public void setSenderImageName(String senderImageName) {
        this.senderImageName = senderImageName;
    }

    public String getReceiverImageName() {
        return receiverImageName;
    }
    public void setReceiverImageName(String receiverImageName) {
        this.receiverImageName = receiverImageName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTimestamp() {
        return messageTimestamp;
    }
    public void setMessageTimestamp(String messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public void setSenderImage(Bitmap senderImage) {
        this.senderImage = senderImage;
    }

    public Bitmap getSenderImage() {
        return senderImage;
    }

    public Bitmap getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(Bitmap receiverImage) {
        this.receiverImage = receiverImage;
    }
}
