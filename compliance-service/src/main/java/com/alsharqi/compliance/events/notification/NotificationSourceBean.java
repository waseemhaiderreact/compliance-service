package com.alsharqi.compliance.events.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationSourceBean {
    //private Source source;
    private MessageChannel output;

    @Autowired
    public NotificationSourceBean(@Qualifier("outBoundNotificationCreate") MessageChannel output) {
        this.output = output;
    }

    public void publishNewNotification(NotificationModel  notification){
        System.out.println("Sending notification");
        this.output.send(MessageBuilder.withPayload(notification).build());
    }
}
