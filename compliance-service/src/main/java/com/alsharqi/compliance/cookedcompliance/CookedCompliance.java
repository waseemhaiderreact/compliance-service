package com.alsharqi.compliance.cookedcompliance;

import javax.persistence.*;

@Entity
@Table(name="t_cookedCompliance")
public class CookedCompliance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeOfCompliance;
    private String statusOfCustomer;
    private String shipmentNumber;
    private String customer;

    //constructors
    public CookedCompliance() {
    }

    public CookedCompliance(String typeOfCompliance, String statusOfCustomer, String shipmentNumber, String customer) {
        this.typeOfCompliance = typeOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.shipmentNumber = shipmentNumber;
        this.customer = customer;
    }

    public CookedCompliance(Long id, String typeOfCompliance, String statusOfCustomer, String shipmentNumber, String customer) {
        this.id = id;
        this.typeOfCompliance = typeOfCompliance;
        this.statusOfCustomer = statusOfCustomer;
        this.shipmentNumber = shipmentNumber;
        this.customer = customer;
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
}
