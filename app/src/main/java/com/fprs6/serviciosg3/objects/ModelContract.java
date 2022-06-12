package com.fprs6.serviciosg3.objects;

public class ModelContract {
    private String professional;
    private String client;
    private String confirmed_for;
    private String status = "no_action"; // Terminated, Conceled, Pospuest, another
    private String details_contract;
    private String price_contract;
    private double last_time_update;

    public ModelContract() {
    }

    public ModelContract(String professional, String client, String confirmed_for, String status, String details_contract, String price_contract, double last_time_update) {
        this.professional = professional;
        this.client = client;
        this.confirmed_for = confirmed_for;
        this.status = status;
        this.details_contract = details_contract;
        this.price_contract = price_contract;
        this.last_time_update = last_time_update;
    }

    public ModelContract(String professional, String client, String details_contract, String price_contract) {
        this.professional = professional;
        this.client = client;
        this.details_contract = details_contract;
        this.price_contract = price_contract;
    }


    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getConfirmed_for() {
        return confirmed_for;
    }

    public void setConfirmed_for(String confirmed_for) {
        this.confirmed_for = confirmed_for;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails_contract() {
        return details_contract;
    }

    public void setDetails_contract(String details_contract) {
        this.details_contract = details_contract;
    }

    public String getPrice_contract() {
        return price_contract;
    }

    public void setPrice_contract(String price_contract) {
        this.price_contract = price_contract;
    }

}
