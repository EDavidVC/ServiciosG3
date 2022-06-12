package com.fprs6.serviciosg3.objects;

public class ModelServices {
    private String service_logo, description_es, type_name_es, type_service;

    public ModelServices(){}

    public ModelServices(String service_logo, String description_es, String type_name_es, String type_service) {
        this.service_logo = service_logo;
        this.description_es = description_es;
        this.type_name_es = type_name_es;
        this.type_service = type_service;
    }

    public String getType_service() {
        return type_service;
    }

    public void setType_service(String type_service) {
        this.type_service = type_service;
    }

    public String getService_logo() {
        return service_logo;
    }

    public void setService_logo(String service_logo) {
        this.service_logo = service_logo;
    }

    public String getDescription_es() {
        return description_es;
    }

    public void setDescription_es(String description_es) {
        this.description_es = description_es;
    }

    public String getType_name_es() {
        return type_name_es;
    }

    public void setType_name_es(String type_name_es) {
        this.type_name_es = type_name_es;
    }
}
