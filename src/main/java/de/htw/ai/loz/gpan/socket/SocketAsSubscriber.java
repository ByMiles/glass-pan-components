package de.htw.ai.loz.gpan.socket;

import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import org.java_websocket.WebSocket;

public class SocketAsSubscriber implements EventSubscriber {

    private WebSocket client;

    public SocketAsSubscriber(WebSocket client) {
        this.client = client;
    }

    @Override
    public boolean sendToSubscriber(String event) {
        try {
            client.send(event);
            return true;
        } catch (Exception e) {
            try {
                client.close();
            } catch (Exception ignored) {}
            return false;
        }
    }

    @Override
    public void kickSubscriber() {
        try {
            client.close();
        } catch (Exception ignored){}
    }
}
