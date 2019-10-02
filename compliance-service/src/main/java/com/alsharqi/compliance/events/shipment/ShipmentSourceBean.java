package com.alsharqi.compliance.events.shipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class ShipmentSourceBean {
    private MessageChannel output;

    @Autowired
    public ShipmentSourceBean(@Qualifier("outBoundShipmentUpdate") MessageChannel output) {
        this.output = output;
    }

    public void updateShipment(ShipmentModel shipment){
        System.out.println("updating shipment status");
        this.output.send(MessageBuilder.withPayload(shipment).build());
    }
}
