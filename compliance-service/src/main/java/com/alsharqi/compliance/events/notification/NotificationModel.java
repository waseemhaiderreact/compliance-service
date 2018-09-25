package com.alsharqi.compliance.events.notification;


import com.alsharqi.compliance.notification.Notification;

public class NotificationModel {
    private String action;
    private Notification notification;
    private String type;

    NotificationModel() {
        super();
    }

    public NotificationModel(String action, Notification notification, String type) {
        super();
        this.action = action;
        this.notification = notification;
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
