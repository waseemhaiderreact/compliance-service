package com.alsharqi.compliance.events.shipmentevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OriginCustomsClearedEventBean {
    private MessageChannel output;

    @Autowired
    public OriginCustomsClearedEventBean(@Qualifier("outBoundOriginCustomsCleared") MessageChannel output) {
        this.output = output;
    }

    public void sendNotificationToAudit(ShipmentEventModel shipmentEventModel){

        this.output.send(MessageBuilder.withPayload(shipmentEventModel).build());
    }
}
