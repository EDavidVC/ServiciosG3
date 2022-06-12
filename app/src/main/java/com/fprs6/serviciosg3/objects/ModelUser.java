package com.fprs6.serviciosg3.objects;

public class ModelUser {
    private String address;
    private String ageOld;
    private String description;
    private String email;
    private String phone;
    private String profileCoverImage;
    private String profileImage;
    private String uid;
    private String userName;

    public ModelUser() {
    }

    public ModelUser(String address, String ageOld, String description, String email, String phone, String profileCoverImage, String profileImage, String uid, String userName) {
        this.address = address;
        this.ageOld = ageOld;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.profileCoverImage = profileCoverImage;
        this.profileImage = profileImage;
        this.uid = uid;
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAgeOld() {
        return ageOld;
    }

    public void setAgeOld(String ageOld) {
        this.ageOld = ageOld;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileCoverImage() {
        return profileCoverImage;
    }

    public void setProfileCoverImage(String profileCoverImage) {
        this.profileCoverImage = profileCoverImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
