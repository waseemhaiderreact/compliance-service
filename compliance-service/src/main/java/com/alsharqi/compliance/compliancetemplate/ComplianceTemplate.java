package com.alsharqi.compliance.compliancetemplate;

import com.alsharqi.compliance.issuingauthority.IssuingAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="t_complianceTemplate")
public class ComplianceTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;
    private String typeOfCompliance;
    private String complianceName;
    private String complianceShortCode;
    private String statusOfCompliance;
    private String statusOfCustomer;
    private Boolean visibleToCustomer;
    private String issuingAuthority;
    private String issuingAuthorityLocation;
    private String country;
    private Long dueDate;
    private String dueDateInputUnit;
    private String comments;
    private Integer active;
    private Integer version;

    @OneToMany(targetEntity = IssuingAuthority.class, fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="complianceTemplate_id",referencedColumnName = "id")
    private Set<IssuingAuthority> issuingAuthorities;

    //constructors
    public ComplianceTemplate() {
    }

    public ComplianceTemplate(String templateName, String typeOfCompliance, String complianceName,
                              String complianceShortCode, String statusOfCompliance, String statusOfCustomer,
                              Boolean visibleToCustomer, String issuingAuthority, String issuingAuthorityLocation,
                              String country, Long dueDate, String dueDateInputUnit, String comments,
                              Set<IssuingAuthority> issuingAuthorities) {
        this.templateName = templateName;
        this.typeOfCompliance = typeOfCompliance;
        this.complianceName = complianceName;
        this.complianceShortCode = complianceShortCode;
        this.statusOfCompliance = statusOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.visibleToCustomer = visibleToCustomer;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
        this.country = country;
        this.dueDate = dueDate;
        this.dueDateInputUnit = dueDateInputUnit;
        this.comments = comments;
        this.issuingAuthorities = issuingAuthorities;
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

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getComplianceName() {
        return complianceName;
    }

    public void setComplianceName(String complianceName) {
        this.complianceName = complianceName;
    }

    public String getComplianceShortCode() {
        return complianceShortCode;
    }

    public void setComplianceShortCode(String complianceShortCode) {
        this.complianceShortCode = complianceShortCode;
    }

    public Set<IssuingAuthority> getIssuingAuthorities() {
        return issuingAuthorities;
    }

    public void setIssuingAuthorities(Set<IssuingAuthority> issuingAuthorities) {
        this.issuingAuthorities = issuingAuthorities;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDateInputUnit() {
        return dueDateInputUnit;
    }

    public void setDueDateInputUnit(String dueDateInputUnit) {
        this.dueDateInputUnit = dueDateInputUnit;
    }
}
