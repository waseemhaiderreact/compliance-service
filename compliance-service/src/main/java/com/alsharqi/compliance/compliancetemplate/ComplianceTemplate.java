package com.alsharqi.compliance.compliancetemplate;

import javax.persistence.*;

@Entity
@Table(name="t_complianceTemplate")
public class ComplianceTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeOfCompliance;
    private String statusOfCompliance;
    private String statusOfCustomer;
    private Boolean visibleToCustomer;
    private String issuingAuthority;
    private String issuingAuthorityLocation;
    private String country;
    private String dueDate;
    private String comments;
    private Integer active;
    private Integer version;

    //constructors
    public ComplianceTemplate() {
    }

    public ComplianceTemplate(String typeOfCompliance, String statusOfCompliance, String statusOfCustomer,
                              Boolean visibleToCustomer, String issuingAuthority, String issuingAuthorityLocation,
                              String country, String dueDate, String comments, Integer active, Integer version) {
        this.typeOfCompliance = typeOfCompliance;
        this.statusOfCompliance = statusOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.visibleToCustomer = visibleToCustomer;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
        this.country = country;
        this.dueDate = dueDate;
        this.comments = comments;
        this.active = active;
        this.version = version;
    }

    public ComplianceTemplate(String typeOfCompliance, String statusOfCompliance, String statusOfCustomer,
                              Boolean visibleToCustomer, String issuingAuthority, String issuingAuthorityLocation,
                              String country, String dueDate, String comments) {
        this.typeOfCompliance = typeOfCompliance;
        this.statusOfCompliance = statusOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.visibleToCustomer = visibleToCustomer;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
        this.country = country;
        this.dueDate = dueDate;
        this.comments = comments;
    }

    public ComplianceTemplate(Long id, String typeOfCompliance, String statusOfCustomer, Boolean visibleToCustomer,
                              String issuingAuthority, String issuingAuthorityLocation,
                              String country, String dueDate, String comments) {
        this.id = id;
        this.typeOfCompliance = typeOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.visibleToCustomer = visibleToCustomer;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
        this.country = country;
        this.dueDate = dueDate;
        this.comments = comments;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeOfCompliance() {
        return typeOfCompliance;
    }

    public void setTypeOfCompliance(String typeOfCompliance) {
        this.typeOfCompliance = typeOfCompliance;
    }

    public String getStatusOfCustomer() {
        return statusOfCustomer;
    }

    public void setStatusOfCustomer(String statusOfCustomer) {
        this.statusOfCustomer = statusOfCustomer;
    }

    public Boolean getVisibleToCustomer() {
        return visibleToCustomer;
    }

    public void setVisibleToCustomer(Boolean visibleToCustomer) {
        this.visibleToCustomer = visibleToCustomer;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getIssuingAuthorityLocation() {
        return issuingAuthorityLocation;
    }

    public void setIssuingAuthorityLocation(String issuingAuthorityLocation) {
        this.issuingAuthorityLocation = issuingAuthorityLocation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatusOfCompliance() {
        return statusOfCompliance;
    }

    public void setStatusOfCompliance(String statusOfCompliance) {
        this.statusOfCompliance = statusOfCompliance;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
