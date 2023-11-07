package com.example.cameinw.model;

import com.example.cameinw.enums.Role;

public class User {

    private Integer id;
    private String theUserName;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String imageName = "userImg.jpg";
    private Role role;

    //---------GETTERS & SETTERS---------\\

    public Integer getId() {return id;}

    public void setId(Integer id) {
        this.id = id;
    }
    public String getTheUserName() {
        return theUserName;
    }
    public void setTheUserName(String theUserName) {
        this.theUserName = theUserName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
