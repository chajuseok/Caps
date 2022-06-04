package com.example.caps;

public class UserAccount {

    private String idToken; // firebase user_id  계정 키값
    private String emailId;
    private String password;
    private String accessToken;
    private String userSeqNo;
    private String androidId;
    private boolean fingerPrint;

    public UserAccount() { }
    public String getIdToken() { return idToken; }
    public String getEmailId() {
        return emailId;
    }
    public String getPassword() {
        return password;
    }
    public String getAccessToken() { return accessToken; }
    public String getUserSeqNo() { return userSeqNo; }
    public String getAndroidId() { return androidId; }
    public boolean getfingerPrint() { return fingerPrint; }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setUserSeqNo(String userSeqNo) { this.userSeqNo = userSeqNo; }
    public void setAndroidId(String androidId) { this.androidId = androidId; }
    public void setfingerPrint(boolean fingerPrint) { this.fingerPrint = fingerPrint; }


}
