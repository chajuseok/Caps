package com.example.caps;

public class InquiryData {

    private String inout_type; //이용금액
    private String content; // 이용 내역
    private String tran_amt;

    public InquiryData(String inout_type, String content, String tran_amt) {
        this.inout_type = inout_type;
        this.content = content;
        this.tran_amt = tran_amt;
    }

    public String getInout_type() {
        return inout_type;
    }

    public void setInout_type(String inout_type) {
        this.inout_type = inout_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTran_amt() {
        return tran_amt;
    }

    public void setTran_amt(String tran_amt) {
        this.tran_amt = tran_amt;
    }
}
