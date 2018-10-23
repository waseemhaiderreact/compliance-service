package com.alsharqi.compliance.compliancerequest;

import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.contact.Contact;
import com.alsharqi.compliance.location.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="t_compliance_request")
public class ComplianceRequest {
    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String status;

    private Date dueDate;
    private Date requestDate;

    private Date dateOfCompletion;
    private String shipmentNumber;
    private String requestNumber;
    private String organizationName;
    @JsonIgnore
    @Lob
    private byte[] content;

    @OneToMany(mappedBy="complianceRequest",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Compliance> compliances=new HashSet<Compliance>();

    @Transient
    private Location headOffice;

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

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


    public Set<Compliance> getCompliances() {
        return compliances;
    }

    public void setCompliances(Set<Compliance> compliances) {
        this.compliances = compliances;
    }

    public Location getHeadOffice() {
        return headOffice;
    }

    public void setHeadOffice(Location headOffice) {
        this.headOffice = headOffice;
    }
}
