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
package de.htw.ai.loz.gpan.socket;

import de.htw.ai.loz.gpan.mac.broker.EventBroker;
import de.htw.ai.loz.gpan.mac.broker.EventPublisher;
import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketService extends WebSocketServer implements EventBroker {

    ConcurrentHashMap<String, EventPublisher> brokerMap;

    private static SocketService singleton;
    private SocketService(InetSocketAddress address) {
        super(address);
        brokerMap = new ConcurrentHashMap<>();
    }

    public static boolean startPublisher(InetSocketAddress address) {
        if (singleton != null)
            return address.toString().equals(singleton.getAddress().toString());
        try {
            SocketService socketService = new SocketService(address);
            socketService.start();
            singleton = socketService;
            return true;
        } catch (Exception e) {
            System.out.println("Error on websocket-app");
            e.printStackTrace();
            return false;
        }
    }

    public static EventBroker publisher() {
        return singleton;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        System.out.println("WS OPEN: " + webSocket.getResourceDescriptor() + " " + brokerMap.size());
        ConfirmationResult result = ConfirmationResult.DENIED;
        for (Map.Entry<String, EventPublisher> entry : brokerMap.entrySet()) {
            if (webSocket.getResourceDescriptor().startsWith(entry.getKey())) {
                String eventId = webSocket.getResourceDescriptor().replace(entry.getKey(), "");
                EventSubscriber subscriber = new SocketAsSubscriber(webSocket);
                result = entry.getValue().subscribeAnEvent(eventId, subscriber);
                break;
            }
        }

        webSocket.send(result.name());
        if (result != ConfirmationResult.SUCCESS) {
            webSocket.close();
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("WS CLOSE: " + webSocket.getResourceDescriptor());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        webSocket.send("sending is not supported " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("WS ERROR: " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }

    @Override
    public boolean registerPublisher(String eventKey, EventPublisher broker) {
        eventKey = "/" + eventKey + "/";
        if (brokerMap.get(eventKey) != null)
            return false;
        brokerMap.put(eventKey, broker);
        return true;
    }

    @Override
    public boolean unRegisterPublisher(String eventKey) {
        return false;
    }

    public static void stopService() {
        try {
            singleton.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
