package com.techroof.nooninvest.ModelClass;

public class Notifications {

    private String Uid;
    private String profitedAmount;
    private String notificationId;

    Notifications(){


    }
    public String getUid() {
        return Uid;
    }

    public String getProfitedAmount() {
        return profitedAmount;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setProfitedAmount(String profitedAmount) {
        this.profitedAmount = profitedAmount;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Notifications(String uid, String profitedAmount, String notificationId) {
        Uid = uid;
        this.profitedAmount = profitedAmount;
        this.notificationId = notificationId;
    }


}
