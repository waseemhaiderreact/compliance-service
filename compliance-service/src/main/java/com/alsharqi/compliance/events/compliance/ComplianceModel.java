package com.alsharqi.compliance.events.compliance;


import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.notification.Notification;

public class ComplianceModel {
    private String action;
    private ComplianceRequest complianceRequest;

    ComplianceModel() {
        super();
    }

    public ComplianceModel(String action, ComplianceRequest complianceRequest) {
        super();
        this.action = action;
        this.complianceRequest = complianceRequest;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ComplianceRequest getComplianceRequest() {
        return complianceRequest;
    }

    public void setComplianceRequest(ComplianceRequest complianceRequest) {
        this.complianceRequest = complianceRequest;
    }
}
