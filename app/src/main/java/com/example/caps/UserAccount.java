package com.example.caps;

public class UserAccount {

    private String idToken; // firebase user_id  계정 키값
    private String emailId;
    private String password;
    private String accessToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public UserAccount() {
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() { return accessToken ;}

    public void setAccessToken(String accessToken) {this.accessToken = accessToken; }

}
