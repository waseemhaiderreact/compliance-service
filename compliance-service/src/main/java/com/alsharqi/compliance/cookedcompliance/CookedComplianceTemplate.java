package com.alsharqi.compliance.cookedcompliance;

import javax.persistence.*;

@Entity
@Table
public class CookedComplianceTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeOfCompliance;
    private String country;

    private String issuingAuthority;
    private String issuingAuthorityLocation;

    //constructors
    public CookedComplianceTemplate() {
    }

    public CookedComplianceTemplate(String typeOfCompliance, String country,
                                    String issuingAuthority, String issuingAuthorityLocation) {
        this.typeOfCompliance = typeOfCompliance;
        this.country = country;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
    }

    public CookedComplianceTemplate(Long id, String typeOfCompliance, String country,
                                    String issuingAuthority, String issuingAuthorityLocation) {
        this.id = id;
        this.typeOfCompliance = typeOfCompliance;
        this.country = country;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String statusOfCustomer) {
        this.country = statusOfCustomer;
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
}
