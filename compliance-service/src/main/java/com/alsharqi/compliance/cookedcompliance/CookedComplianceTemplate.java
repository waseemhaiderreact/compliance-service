package com.alsharqi.compliance.cookedcompliance;

import javax.persistence.*;

@Entity
@Table
public class CookedComplianceTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String templateName;
    private String typeOfCompliance;
    private String complianceName;
    private String complianceShortCode;
    private Boolean visibleToCustomer;

    //constructors
    public CookedComplianceTemplate() {
    }

    public CookedComplianceTemplate(String templateName, String typeOfCompliance, String complianceName,
                                    String complianceShortCode, Boolean visibleToCustomer) {
        this.templateName = templateName;
        this.typeOfCompliance = typeOfCompliance;
        this.complianceName = complianceName;
        this.complianceShortCode = complianceShortCode;
        this.visibleToCustomer = visibleToCustomer;
    }

    public CookedComplianceTemplate(Long id, String templateName, String typeOfCompliance, String complianceName,
                                    String complianceShortCode, Boolean visibleToCustomer) {
        this.id = id;
        this.templateName = templateName;
        this.typeOfCompliance = typeOfCompliance;
        this.complianceName = complianceName;
        this.complianceShortCode = complianceShortCode;
        this.visibleToCustomer = visibleToCustomer;
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

    public Boolean getVisibleToCustomer() {
        return visibleToCustomer;
    }

    public void setVisibleToCustomer(Boolean visibleToCustomer) {
        this.visibleToCustomer = visibleToCustomer;
    }
}
