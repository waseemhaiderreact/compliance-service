package com.alsharqi.compliance.events;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {
    @Output("outBoundNotificationCreate")
    MessageChannel outputNotifications();

    @Output("outBoundComplianceCreate")
    MessageChannel outputCompliance();

    @Output("outBoundShipmnetSummary")
    MessageChannel outputShipmentSummaryListEvent();

    @Output("outBoundShipmentUpdate")
    MessageChannel outputShipmentStatus();

    @Output("outBoundOriginCustomsCleared")
    SubscribableChannel outputOriginCustomsClearedEvent();

    @Output("outBoundDestinationCustomsCleared")
    SubscribableChannel outputDestinationCustomsClearedEvent();
}
