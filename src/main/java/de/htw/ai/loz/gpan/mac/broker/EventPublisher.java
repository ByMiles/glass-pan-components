package de.htw.ai.loz.gpan.mac.broker;

import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;

public interface EventPublisher {

    ConfirmationResult subscribeAnEvent(String eventId, EventSubscriber subscriber);
}
