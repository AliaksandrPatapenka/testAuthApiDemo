package com.apiAuto.models.auth;

public class UserLogin {
    private String email;
    private String password;

    public UserLogin(){};

    public String getEmail() {return email;}
    public String getPassword() {return password;}

    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
}
