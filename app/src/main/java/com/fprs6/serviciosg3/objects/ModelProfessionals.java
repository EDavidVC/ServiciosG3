package com.fprs6.serviciosg3.objects;

public class ModelProfessionals {
    private boolean actived = false;
    private String professional_uid = "";
    private double professional_valoration = 0.0;
    private String references_image[] = new String[]{};
    private String references_coument[] = new String[]{};
    private String service_abstract = "";
    private String service_description_general = "";
    private String service_type = "";
    private String price_base = "";
    private int comments = 0;

    public ModelProfessionals() {
    }

    public ModelProfessionals(String service_abstract, String service_description_general, String service_type, String price_base) {
        this.service_abstract = service_abstract;
        this.service_description_general = service_description_general;
        this.service_type = service_type;
        this.price_base = price_base;
    }

    public ModelProfessionals(boolean actived, String professional_uid, double professional_valoration, String[] references_image, String[] references_coument, String service_abstract, String service_description_general, String service_type, String price_base, int comments) {
        this.actived = actived;
        this.professional_uid = professional_uid;
        this.professional_valoration = professional_valoration;
        this.references_image = references_image;
        this.references_coument = references_coument;
        this.service_abstract = service_abstract;
        this.service_description_general = service_description_general;
        this.service_type = service_type;
        this.price_base = price_base;
        this.comments = comments;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }

    public String getProfessional_uid() {
        return professional_uid;
    }

    public void setProfessional_uid(String professional_uid) {
        this.professional_uid = professional_uid;
    }

    public double getProfessional_valoration() {
        return professional_valoration;
    }

    public void setProfessional_valoration(double professional_valoration) {
        this.professional_valoration = professional_valoration;
    }

    public String[] getReferences_image() {
        return references_image;
    }

    public void setReferences_image(String[] references_image) {
        this.references_image = references_image;
    }

    public String[] getReferences_coument() {
        return references_coument;
    }

    public void setReferences_coument(String[] references_coument) {
        this.references_coument = references_coument;
    }

    public String getService_abstract() {
        return service_abstract;
    }

    public void setService_abstract(String service_abstract) {
        this.service_abstract = service_abstract;
    }

    public String getService_description_general() {
        return service_description_general;
    }

    public void setService_description_general(String service_description_general) {
        this.service_description_general = service_description_general;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getPrice_base() {
        return price_base;
    }

    public void setPrice_base(String price_base) {
        this.price_base = price_base;
    }
}