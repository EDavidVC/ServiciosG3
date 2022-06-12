package com.fprs6.serviciosg3.objects;

public class ModelChat {
    //Independiately Key
    private String keyMessage = "";


    private String sender = "";
    private String receiver = "";
    private String message = "";
    private boolean messageStatus = false;
    private String timeSending = "";
    private String type = "";
    private double latitude = 0;
    private double longitude = 0;
    private ModelContract modelContract = new ModelContract();

    public ModelChat() {
    }

    public ModelChat(String keyMessage, String sender, String receiver, String message, boolean messageStatus, String timeSending, String type, double latitude, double longitude, ModelContract modelContract) {
        this.keyMessage = keyMessage;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.messageStatus = messageStatus;
        this.timeSending = timeSending;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.modelContract = modelContract;
    }

    public String getKeyMessage() {
        return keyMessage;
    }

    public void setKeyMessage(String keyMessage) {
        this.keyMessage = keyMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(boolean messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getTimeSending() {
        return timeSending;
    }

    public void setTimeSending(String timeSending) {
        this.timeSending = timeSending;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ModelContract getModelContract() {
        return modelContract;
    }

    public void setModelContract(ModelContract modelContract) {
        this.modelContract = modelContract;
    }


}
