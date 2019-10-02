package com.alsharqi.compliance.events.shipmentsummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SummaryListSourceBean {

    private MessageChannel output;

    @Autowired
    public SummaryListSourceBean(@Qualifier("outBoundShipmnetSummary") MessageChannel output) {
        this.output = output;
    }

    public void sendShipmentSummaryKafkaEvent(SummaryListModel summaryListModel){

        this.output.send(MessageBuilder.withPayload(summaryListModel).build());
    }
}
