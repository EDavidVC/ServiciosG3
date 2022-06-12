package com.fprs6.serviciosg3.objects;

public class ModelComment {
    private String commented;
    private String commenter;
    private String comment;
    private double valoration;
    private String time_comment;

    public ModelComment() {
    }

    public ModelComment(String commented, String commenter, String comment, double valoration, String time_comment) {
        this.commented = commented;
        this.commenter = commenter;
        this.comment = comment;
        this.valoration = valoration;
        this.time_comment = time_comment;
    }

    public ModelComment(String commented, String commenter, String comment, double valoration) {
        this.commented = commented;
        this.commenter = commenter;
        this.comment = comment;
        this.valoration = valoration;
    }

    public String getCommented() {
        return commented;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getValoration() {
        return valoration;
    }

    public void setValoration(double valoration) {
        this.valoration = valoration;
    }

    public String getTime_comment() {
        return time_comment;
    }

    public void setTime_comment(String time_comment) {
        this.time_comment = time_comment;
    }
}
