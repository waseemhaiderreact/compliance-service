package com.alsharqi.compliance.compliance;

import com.alsharqi.compliance.attachment.FileAttachments;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.contact.Contact;
import com.alsharqi.compliance.issuingauthority.IssuingAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="t_compliance")
public class Compliance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeOfCompliance;
    private String statusOfCompliance;
    private String statusOfCustomer;
    private String shipmentNumber;
    private String customer;
    private String subsidiary;
    private String location;
    private String squad;
    private Boolean visibleToCustomer;
    private String complianceName;
    private String complianceShortCode;

    private String complianceNumber;
    private String issuingAuthority;
    private String issuingAuthorityLocation;
    private String country;
    private Long dueDate;
    private String dueDateInputUnit;

    private String comments;
    private String agentAssigned;
    private Integer active;
    private Integer version;
    private String username;
    private String additionalComments;
    private String documentType;

    @OneToMany(targetEntity = IssuingAuthority.class, fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="compliance_id",referencedColumnName = "id")
    private Set<IssuingAuthority> issuingAuthorities;
    //constructors
    public Compliance() {
    }

    public Compliance(String typeOfCompliance, String statusOfCompliance, String statusOfCustomer,
                      String shipmentNumber, String customer, String subsidiary, String location, String squad,
                      Boolean visibleToCustomer, String complianceName, String complianceShortCode,
                      String complianceNumber, String issuingAuthority, String issuingAuthorityLocation,
                      String country, Long dueDate, String dueDateInputUnit, String comments, String agentAssigned,
                      String username, String additionalComments, String documentType,
                      Set<IssuingAuthority> issuingAuthorities) {
        this.typeOfCompliance = typeOfCompliance;
        this.statusOfCompliance = statusOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.shipmentNumber = shipmentNumber;
        this.customer = customer;
        this.subsidiary = subsidiary;
        this.location = location;
        this.squad = squad;
        this.visibleToCustomer = visibleToCustomer;
        this.complianceName = complianceName;
        this.complianceShortCode = complianceShortCode;
        this.complianceNumber = complianceNumber;
        this.issuingAuthority = issuingAuthority;
        this.issuingAuthorityLocation = issuingAuthorityLocation;
        this.country = country;
        this.dueDate = dueDate;
        this.dueDateInputUnit = dueDateInputUnit;
        this.comments = comments;
        this.agentAssigned = agentAssigned;
        this.username = username;
        this.additionalComments = additionalComments;
        this.documentType = documentType;
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

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSquad() {
        return squad;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }

    public Boolean getVisibleToCustomer() {
        return visibleToCustomer;
    }

    public void setVisibleToCustomer(Boolean visibleToCustomer) {
        this.visibleToCustomer = visibleToCustomer;
    }

    public String getComplianceNumber() {
        return complianceNumber;
    }

    public void setComplianceNumber(String complianceNumber) {
        this.complianceNumber = complianceNumber;
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

    public String getAgentAssigned() {
        return agentAssigned;
    }

    public void setAgentAssigned(String agentAssigned) {
        this.agentAssigned = agentAssigned;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Set<IssuingAuthority> getIssuingAuthorities() {
        return issuingAuthorities;
    }

    public void setIssuingAuthorities(Set<IssuingAuthority> issuingAuthorities) {
        this.issuingAuthorities = issuingAuthorities;
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
    //getters and setters

//    private String type;
//    private String status;
//    private Date dueDate;
//    private Date requestDate=new Date();
//    private Date dateOfCompletion;
//    private String complianceNumber;
//    private Date dateStarted;
//    private boolean visibleToCustomer;
//    private String countryCode;
//
//    @JsonIgnore
//    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//    @JoinColumn(name="compliance_request_id")
//    private ComplianceRequest complianceRequest;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="user")
//    private Contact user;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="authority")
//    private Contact issuingAuthority;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="vendor")
//    private Contact vendor;
//
////    @JsonIgnore
////    @OneToMany(cascade = CascadeType.ALL,mappedBy = "compliance",fetch = FetchType.EAGER)
////    private Set<Attachment> attachmentSet = new HashSet<Attachment>();
//
//    @OneToMany(mappedBy="compliance",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    private Set<FileAttachments> attachments=new HashSet<FileAttachments>();
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Date getDueDate() {
//        return dueDate;
//    }
//
//    public void setDueDate(Date dueDate) {
//        this.dueDate = dueDate;
//    }
//
//    public Date getRequestDate() {
//        return requestDate;
//    }
//
//    public void setRequestDate(Date requestDate) {
//        this.requestDate = requestDate;
//    }
//
//    public Date getDateOfCompletion() {
//        return dateOfCompletion;
//    }
//
//    public void setDateOfCompletion(Date dateOfCompletion) {
//        this.dateOfCompletion = dateOfCompletion;
//    }
//
//    public String getComplianceNumber() {
//        return complianceNumber;
//    }
//
//    public void setComplianceNumber(String complianceNumber) {
//        this.complianceNumber = complianceNumber;
//    }
//
//    public Date getDateStarted() {
//        return dateStarted;
//    }
//
//    public void setDateStarted(Date dateStarted) {
//        this.dateStarted = dateStarted;
//    }
//
//    public ComplianceRequest getComplianceRequest() {
//        return complianceRequest;
//    }
//
//    public void setComplianceRequest(ComplianceRequest complianceRequest) {
//        this.complianceRequest = complianceRequest;
//    }
//
//    public Contact getUser() {
//        return user;
//    }
//
//    public void setUser(Contact user) {
//        this.user = user;
//    }
//
//    public Contact getIssuingAuthority() {
//        return issuingAuthority;
//    }
//
//    public void setIssuingAuthority(Contact issuingAuthority) {
//        this.issuingAuthority = issuingAuthority;
//    }
//
//    public boolean isVisibleToCustomer() {
//        return visibleToCustomer;
//    }
//
//    public void setVisibleToCustomer(boolean visibleToCustomer) {
//        this.visibleToCustomer = visibleToCustomer;
//    }
//
//    public Set<FileAttachments> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(Set<FileAttachments> attachments) {
//        this.attachments = attachments;
//    }
//
//    public String getCountryCode() {
//        return countryCode;
//    }
//
//    public void setCountryCode(String countryCode) {
//        this.countryCode = countryCode;
//    }
//
//    public Contact getVendor() {
//        return vendor;
//    }
//
//    public void setVendor(Contact vendor) {
//        this.vendor = vendor;
//    }
//
//    public void copyComplianceValues(Compliance cp){
//        if(cp.id!=null)
//            this.id = cp.id;
//        this.status=cp.status;
//        this.dueDate=cp.dueDate;
//        this.dateOfCompletion=cp.dateOfCompletion;
//        this.user=cp.user;
//        this.issuingAuthority=cp.issuingAuthority;
//        this.vendor = cp.vendor;
//        this.type = cp.type;
//        if(this.user!=null)
//            this.user.getUser().add(this);
//        if(this.issuingAuthority!=null)
//            this.issuingAuthority.getIssuingAuthorities().add(this);
//
//        if(this.vendor!=null)
//            this.vendor.getVendors().add(this);
//    }
}
