package com.alsharqi.compliance.compliance;

import com.alsharqi.compliance.attachment.FileAttachments;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.contact.Contact;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    private String countryCode;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="vendor")
    private Contact vendor;

//    @JsonIgnore
//    @OneToMany(cascade = CascadeType.ALL,mappedBy = "compliance",fetch = FetchType.EAGER)
//    private Set<Attachment> attachmentSet = new HashSet<Attachment>();

    @OneToMany(mappedBy="compliance",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<FileAttachments> attachments=new HashSet<FileAttachments>();

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

    public Set<FileAttachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<FileAttachments> attachments) {
        this.attachments = attachments;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Contact getVendor() {
        return vendor;
    }

    public void setVendor(Contact vendor) {
        this.vendor = vendor;
    }

    public void copyComplianceValues(Compliance cp){
        if(cp.id!=null)
            this.id = cp.id;
        this.status=cp.status;
        this.dueDate=cp.dueDate;
        this.dateOfCompletion=cp.dateOfCompletion;
        this.user=cp.user;
        this.issuingAuthority=cp.issuingAuthority;
        this.vendor = cp.vendor;
        this.type = cp.type;
        if(this.user!=null)
            this.user.getUser().add(this);
        if(this.issuingAuthority!=null)
            this.issuingAuthority.getIssuingAuthorities().add(this);

        if(this.vendor!=null)
            this.vendor.getVendors().add(this);
    }
}
