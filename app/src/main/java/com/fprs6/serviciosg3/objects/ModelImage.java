package com.fprs6.serviciosg3.objects;

public class ModelImage {
    private String image_url;
    private String uid;
    private String key;
    private String Reference;

    public ModelImage() {
    }

    public ModelImage(String image_url, String uid, String key, String reference) {
        this.image_url = image_url;
        this.uid = uid;
        this.key = key;
        Reference = reference;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }
}
