package com.alsharqi.compliance.request;

import com.alsharqi.compliance.contact.Contact;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditComplianceRecordRequest {

    private String type;
    private String status;
    private Date dueDate;
    private Date requestDate=new Date();
    private Date dateOfCompletion;
    private String complianceNumber;
    private Date dateStarted;
    private boolean visibleToCustomer;

    private Contact user;
    private Contact issuingAuthority;

    private List<MultipartFile> attachements = new ArrayList<MultipartFile>();

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

    public boolean isVisibleToCustomer() {
        return visibleToCustomer;
    }

    public void setVisibleToCustomer(boolean visibleToCustomer) {
        this.visibleToCustomer = visibleToCustomer;
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

    public List<MultipartFile> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<MultipartFile> attachements) {
        this.attachements = attachements;
    }
}
