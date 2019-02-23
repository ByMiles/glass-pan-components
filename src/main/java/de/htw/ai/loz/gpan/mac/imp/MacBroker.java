package de.htw.ai.loz.gpan.mac.imp;

import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.adaptation.Channel;
import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import de.htw.ai.loz.gpan.mac.adaptation.MacAdaptation;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.msg.MacId;
import de.htw.ai.loz.gpan.socket.SocketService;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MacBroker implements de.htw.ai.loz.gpan.mac.broker.MacBroker {

    @Inject
    private Provider<Channel> channelProvider;
    @Inject
    private Provider<MacAdaptation> frameLayerProvider;

    private final Map<String, MacAdaptation> macMap;

    public MacBroker() {
        macMap = new ConcurrentHashMap<>();
        SocketService.publisher().registerPublisher("imp", this);
    }

    @Override
    public ConfirmationResult startAMac(MacId macId) {

        MacAdaptation aMacAdaptation;
        if ((aMacAdaptation = macMap.remove(macId.toString())) != null) {
            aMacAdaptation.stopMac();
        }

        Channel channel = channelProvider.get();
        if (!channel.open()) {
            System.out.println("... after fail...");
            macMap.entrySet()
                    .removeIf(entry -> entry.getValue().stopIfInActive(300));
            System.out.println("... after delete...");
            if (!channel.open()) {
                System.out.println("SERIAL PORT DENIED");
                return ConfirmationResult.DENIED;
            }
        }

        aMacAdaptation = frameLayerProvider.get();

        ConfirmationResult startResult = aMacAdaptation.startMac(channel, macId);

        if (startResult == ConfirmationResult.SUCCESS)
            macMap.put(macId.toString(), aMacAdaptation);

        return startResult;
    }

    @Override
    public ConfirmationResult subscribeAnEvent(String macUrl, EventSubscriber subscriber) {
        MacAdaptation aMacAdaptation = macMap.getOrDefault(macUrl, null);
        System.out.println("subscribe event: " + macUrl);
        if (aMacAdaptation != null)
            return aMacAdaptation.subscribeDataInd(subscriber);
        return ConfirmationResult.INVALID;
    }

    @Override
    public ConfirmationResult postFrameCommands(String macUrl, MacDataCmd[] cmds) {

        MacAdaptation macLayer = macMap.get(macUrl);

       if (macLayer == null)
            return ConfirmationResult.DENIED;
        System.out.println("HTTP: POST: => " + macLayer.getMacId().toString());

        ConfirmationResult lastResult = ConfirmationResult.INVALID;
        for (MacDataCmd cmd : cmds) {
            lastResult = macLayer.sendDataCmd(cmd);
            if (lastResult != ConfirmationResult.SUCCESS)
                return lastResult;
        }
        return lastResult;
    }

    @Override
    public MacId[] getAllMacIds() {
        List<MacId> ids = new LinkedList<>();
        macMap.values().iterator().forEachRemaining(macUrl -> {

            ids.add(macUrl.getMacId());
        });
        return ids.toArray(new MacId[0]);
    }
}
