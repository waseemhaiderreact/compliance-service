package com.alsharqi.compliance.compliance;

import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.contact.Contact;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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

    @JsonIgnore
    @Lob
    private byte[] documentContent;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="compliance_request_id")
    private ComplianceRequest complianceRequest;

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

    public byte[] getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(byte[] documentContent) {
        this.documentContent = documentContent;
    }

    public String getComplianceNumber() {
        return complianceNumber;
    }

    public void setComplianceNumber(String complianceNumber) {
        this.complianceNumber = complianceNumber;
    }

    public ComplianceRequest getComplianceRequest() {
        return complianceRequest;
    }

    public void setComplianceRequest(ComplianceRequest complianceRequest) {
        this.complianceRequest = complianceRequest;
    }
}
