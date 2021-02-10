package com.alsharqi.compliance.document;

import javax.persistence.*;

@Entity
@Table(name="t_document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String documentName;
    private String documentUUID;
//    private String customerType;
//    private Long OrganizationID;

    public Document() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentUUID() {
        return documentUUID;
    }

    public void setDocumentUUID(String documentUUID) {
        this.documentUUID = documentUUID;
    }

//    public Long getOrganizationID() {
//        return OrganizationID;
//    }
//
//    public void setOrganizationID(Long organizationID) {
//        OrganizationID = organizationID;
//    }
//
//    public String getCustomerType() {
//        return customerType;
//    }
//
//    public void setCustomerType(String customerType) {
//        this.customerType = customerType;
//    }
}
