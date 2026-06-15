package com.apiAuto.models.users;

public class UserUpdate {
    private String name;
    private String email;
    private String password;
    private String avatar;
    private String role;

    public UserUpdate(){};

    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getAvatar() {return avatar;}
    public String getRole() {return role;}

    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public void setAvatar(String avatar) {this.avatar = avatar;}
    public void setRole(String role) {this.role = role;}
}
