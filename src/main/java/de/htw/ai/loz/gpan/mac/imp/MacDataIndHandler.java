package de.htw.ai.loz.gpan.mac.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.ai.loz.gpan.mac.msg.MacDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import de.htw.ai.loz.gpan.mac.adaptation.DataHandler;
import de.htw.ai.loz.gpan.mac.adaptation.IndicationHandler;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MacDataIndHandler implements IndicationHandler {

    private DataHandler transformer;
    private final ConcurrentLinkedQueue<EventSubscriber> subscribers;
    private final Object subscriberLock;
    private boolean isClosed;
    private LocalTime lastActivity;
    private Thread streamingThread;

    public MacDataIndHandler() {
        subscribers = new ConcurrentLinkedQueue<>();
        subscriberLock = new ReentrantReadWriteLock();
    }

    @Override
    public ConfirmationResult subscribeAnEvent(String macUrl, EventSubscriber subscriber) {
        System.out.println("?!!#");
        if (isClosed)
            return ConfirmationResult.DENIED;
        System.out.println("?!#");
        subscribers.add(subscriber);
        logActivity();
        synchronized (subscriberLock) {
            subscriberLock.notify();
        }
        System.out.println("!!!#");
        return ConfirmationResult.SUCCESS;
    }

    private void logActivity() {
        lastActivity = LocalTime.now();
    }

    @Override
    public boolean isActive(int allowedIdleSeconds) {
        return (!subscribers.isEmpty()
                ||
                Duration.between(lastActivity, LocalTime.now()).getSeconds() <= allowedIdleSeconds)
                &&
                !isClosed;
    }

    @Override
    public void startStreaming() throws Exception {
        isClosed = false;
        streamingThread = Thread.currentThread();
        logActivity();
        loop();
    }

    private void loop() throws Exception {
        while (!isClosed && awaitSubscriber()) {
            MacDataInd ind = transformer.awaitIndication();
            if (!publishIndication(ind))
                transformer.reQueueIndication(ind);
            else logActivity();
        }
    }

    private boolean awaitSubscriber() {

        synchronized (subscriberLock) {
            while (subscribers.isEmpty()) {
                try {
                    subscriberLock.wait();
                } catch (Exception ignored) {
                    if (isClosed) return false;
                }
            }
        }
        return true;
    }

    private boolean publishIndication(MacDataInd ind) {
        if (subscribers.isEmpty())
            return false;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String asString = mapper.writeValueAsString(ind);
            subscribers.iterator().forEachRemaining(subscriber -> {
                if (!subscriber.sendToSubscriber(asString))
                    subscribers.remove(subscriber);
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return true;
        }
        return subscribers.size() > 0;
    }

    @Override
    public void stopStreaming() {
        isClosed = true;

        System.out.println("Called interrupt:  IndHandler " + subscribers.size());
        streamingThread.interrupt();
    }

    @Override
    public void setDataHandler(DataHandler transformer) {
        this.transformer = transformer;
    }
}
