package com.alsharqi.compliance.events.shipment;

public class ShipmentModel {
    private String action;
    private ShipmentStatus shipment;

    ShipmentModel() {
        super();
    }

    public ShipmentModel(String action, ShipmentStatus shipment) {
        super();
        this.action = action;
        this.shipment = shipment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ShipmentStatus getShipment() {
        return shipment;
    }

    public void setShipment(ShipmentStatus shipment) {
        this.shipment = shipment;
    }
}
