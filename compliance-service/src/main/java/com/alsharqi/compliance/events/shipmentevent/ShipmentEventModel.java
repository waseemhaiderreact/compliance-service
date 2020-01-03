package com.alsharqi.compliance.events.shipmentevent;

import java.util.HashMap;

public class ShipmentEventModel {
    private String action;
    private String eventCode;
    private String shipmentNumber;
    private String notificationType;
    private String userName;
    private HashMap<String,String> values = new HashMap<>();

    public ShipmentEventModel(){super();}

    public ShipmentEventModel(String action, HashMap<String, String> values) {
        this.action = action;
        this.values = values;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public void setValues(HashMap<String, String> values) {
        this.values = values;
    }


    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
