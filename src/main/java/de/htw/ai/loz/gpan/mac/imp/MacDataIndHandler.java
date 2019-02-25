/*
Copyright 2019 Miles Lorenz

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
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
        subscriberLock = new Object();
    }

    @Override
    public ConfirmationResult subscribeAnEvent(String macUrl, EventSubscriber subscriber) {
        if (isClosed)
            return ConfirmationResult.DENIED;
        subscribers.add(subscriber);
        logActivity();
        synchronized (subscriberLock) {
            subscriberLock.notify();
            System.out.println("Notified!!!");
        }
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

        streamingThread.interrupt();
    }

    @Override
    public void setDataHandler(DataHandler dataHandler) {
        this.transformer = dataHandler;
    }
}
