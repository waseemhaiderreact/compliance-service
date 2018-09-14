package com.alsharqi.compliance.exception;

public class ForeignKeyContraintException extends Exception {
    private String description;
    private Long ResourseID;

    public ForeignKeyContraintException(String description, Long resourseID) {
        this.description = description;
        ResourseID = resourseID;
    }

    public ForeignKeyContraintException(String message, String description, Long resourseID) {
        super(message);
        this.description = description;
        ResourseID = resourseID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getResourseID() {
        return ResourseID;
    }

    public void setResourseID(Long resourseID) {
        ResourseID = resourseID;
    }
}
