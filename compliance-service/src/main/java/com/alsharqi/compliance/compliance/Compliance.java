package com.alsharqi.compliance.compliance;

import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.contact.Contact;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.awt.print.Pageable;
import java.util.Date;

@Entity
@Table(name="t_compliance")
public class Compliance {
    @Id
    @GeneratedValue
    private Long id;

    private String type;
    private String status;
    private Date dueDate;
    private Date requestDate=new Date();
    private Date dateOfCompletion;
    private String complianceNumber;
    private Date dateStarted;
    private boolean visibleToCustomer;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="compliance_request_id")
    private ComplianceRequest complianceRequest;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user")
    private Contact user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="authority")
    private Contact issuingAuthority;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(Date dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public String getComplianceNumber() {
        return complianceNumber;
    }

    public void setComplianceNumber(String complianceNumber) {
        this.complianceNumber = complianceNumber;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public ComplianceRequest getComplianceRequest() {
        return complianceRequest;
    }

    public void setComplianceRequest(ComplianceRequest complianceRequest) {
        this.complianceRequest = complianceRequest;
    }

    public Contact getUser() {
        return user;
    }

    public void setUser(Contact user) {
        this.user = user;
    }

    public Contact getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(Contact issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public boolean isVisibleToCustomer() {
        return visibleToCustomer;
    }

    public void setVisibleToCustomer(boolean visibleToCustomer) {
        this.visibleToCustomer = visibleToCustomer;
    }

    public void copyComplianceValues(Compliance cp){
        this.status=cp.status;
        this.dueDate=cp.dueDate;
        this.dateOfCompletion=cp.dateOfCompletion;
        this.user=cp.user;
        this.issuingAuthority=cp.issuingAuthority;
        this.user.getUser().add(this);
        this.issuingAuthority.getIssuingAuthorities().add(this);
    }
}
