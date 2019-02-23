package de.htw.ai.loz.gpan.mac.broker;

public interface EventSubscriber {

    boolean sendToSubscriber(String event);
    void kickSubscriber();
}
