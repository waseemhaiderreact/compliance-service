package com.alsharqi.compliance.events.shipmentevent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OriginCustomsClearedEventBean {
    private MessageChannel output;
    private static final Logger LOGGER = LogManager.getLogger(OriginCustomsClearedEventBean.class);

    @Autowired
    public OriginCustomsClearedEventBean(@Qualifier("outBoundOriginCustomsCleared") MessageChannel output) {
        this.output = output;
    }

    public void sendNotificationToAudit(ShipmentEventModel shipmentEventModel){

        try {
            this.output.send(MessageBuilder.withPayload(shipmentEventModel).build());
        } catch (Exception e) {
            LOGGER.error("Error while sending origin customs cleared notification to audit",e);
        }
    }
}
