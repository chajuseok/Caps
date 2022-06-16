package com.example.caps;

public class Pay {

    private String bankInfo; //은행정보 ex) 국민, 광주
    private String bankId; // 계좌번호
    private String money; // 잔액

    public Pay(String bankInfo,String bankId ,String money) {
        this.bankInfo = bankInfo;
        this.money = money;
        this.bankId= bankId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
