package com.alsharqi.compliance.events.compliance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class ComplianceSourceBean {
    private MessageChannel output;

    @Autowired
    public ComplianceSourceBean(@Qualifier("outBoundComplianceCreate") MessageChannel output) {
        this.output = output;
    }

    public void publishCompliance(ComplianceModel compliance){
        System.out.println("Sending Compliance for search-service");
        this.output.send(MessageBuilder.withPayload(compliance).build());
    }
}
