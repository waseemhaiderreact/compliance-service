package com.alsharqi.compliance.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomChannels {
    @Output("outBoundNotificationCreate")
    MessageChannel outputNotifications();

    @Output("outBoundComplianceCreate")
    MessageChannel outputCompliance();
}
