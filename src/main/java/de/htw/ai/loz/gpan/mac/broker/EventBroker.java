package de.htw.ai.loz.gpan.mac.broker;

public interface EventBroker {


    boolean registerPublisher(String eventKey, EventPublisher publisher);
    boolean unRegisterPublisher(String eventKey);
}
