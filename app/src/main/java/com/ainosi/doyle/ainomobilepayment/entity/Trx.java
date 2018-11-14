package com.ainosi.doyle.ainomobilepayment.entity;

public class Trx {
    private String mid;
    private String tid;
    private String shift;
    private int trxid;
    private String trxamt;
    private String trxdate;
    private String trxissid;

    public Trx(String mid, String tid, String shift,
               int trxid, String trxamt, String trxdate,
               String trxissid) {
        this.mid = mid;
        this.tid = tid;
        this.shift = shift;
        this.trxid = trxid;
        this.trxamt = trxamt;
        this.trxdate = trxdate;
        this.trxissid = trxissid;
    }

    public Trx() {
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public int getTrxid() {
        return trxid;
    }

    public void setTrxid(int trxid) {
        this.trxid = trxid;
    }

    public String getTrxamt() {
        return trxamt;
    }

    public void setTrxamt(String trxamt) {
        this.trxamt = trxamt;
    }

    public String getTrxdate() {
        return trxdate;
    }

    public void setTrxdate(String trxdate) {
        this.trxdate = trxdate;
    }

    public String getTrxissid() {
        return trxissid;
    }

    public void setTrxissid(String trxissid) {
        this.trxissid = trxissid;
    }
}
