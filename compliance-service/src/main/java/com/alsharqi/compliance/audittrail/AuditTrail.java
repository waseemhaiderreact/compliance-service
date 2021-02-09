package com.alsharqi.compliance.audittrail;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String complianceNumber;
    private String statusOfCompliance;
    private String statusOfCustomer;
    private String username;
    private Date lastUpdated;
    //constructors
    public AuditTrail() {
    }

    public AuditTrail(String complianceNumber, String statusOfCompliance, String statusOfCustomer, String username) {
        this.complianceNumber = complianceNumber;
        this.statusOfCompliance = statusOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.username = username;
    }

    public AuditTrail(String complianceNumber, String statusOfCompliance,
                      String statusOfCustomer, String username, Date lastUpdated) {
        this.complianceNumber = complianceNumber;
        this.statusOfCompliance = statusOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.username = username;
        this.lastUpdated = lastUpdated;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplianceNumber() {
        return complianceNumber;
    }

    public void setComplianceNumber(String complianceNumber) {
        this.complianceNumber = complianceNumber;
    }

    public String getStatusOfCompliance() {
        return statusOfCompliance;
    }

    public void setStatusOfCompliance(String statusOfCompliance) {
        this.statusOfCompliance = statusOfCompliance;
    }

    public String getStatusOfCustomer() {
        return statusOfCustomer;
    }

    public void setStatusOfCustomer(String statusOfCustomer) {
        this.statusOfCustomer = statusOfCustomer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
