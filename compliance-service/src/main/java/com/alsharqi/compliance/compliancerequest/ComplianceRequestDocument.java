package com.alsharqi.compliance.compliancerequest;

import javax.persistence.Lob;

public class ComplianceRequestDocument {
    String shipmentNumber;
    @Lob
    private byte[] content;

    private String contentType="data:application/pdf;base64,";

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
