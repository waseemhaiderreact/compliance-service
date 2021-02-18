package com.alsharqi.compliance.events.compliance;


import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.notification.Notification;

public class ComplianceModel {
    private String action;
    private Compliance compliance;

    ComplianceModel() {
        super();
    }

    public ComplianceModel(String action, Compliance compliance) {
        super();
        this.action = action;
        this.compliance = compliance;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Compliance getCompliance() {
        return compliance;
    }

    public void setCompliance(Compliance compliance) {
        this.compliance = compliance;
    }
}
