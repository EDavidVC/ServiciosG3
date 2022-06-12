package com.fprs6.serviciosg3.objects;

public class ModelDocument {
    private String reference;
    private String name_document;
    private String url_document;
    private String uid;
    private String Key;

    public ModelDocument() {
    }

    public ModelDocument(String reference, String name_document, String url_document, String uid) {
        this.reference = reference;
        this.name_document = name_document;
        this.url_document = url_document;
        this.uid = uid;
    }

    public ModelDocument(String reference, String name_document, String url_document, String uid, String key) {
        this.reference = reference;
        this.name_document = name_document;
        this.url_document = url_document;
        this.uid = uid;
        Key = key;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName_document() {
        return name_document;
    }

    public void setName_document(String name_document) {
        this.name_document = name_document;
    }

    public String getUrl_document() {
        return url_document;
    }

    public void setUrl_document(String url_document) {
        this.url_document = url_document;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
