package de.htw.ai.loz.gpan.mac.adaptation;
import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.msg.MacId;

public interface MacAdaptation {

    ChannelDataInd takeUnresolvedChannelDataIndication() throws InterruptedException;
    boolean isUnresolvedChannelDataIndicationAvailable();

    ConfirmationResult startMac(Channel channel, MacId macId);
    ConfirmationResult stopMac();
    boolean stopIfInActive(int allowedIdleSeconds);
    ConfirmationResult sendDataCmd (MacDataCmd cmd);
    ConfirmationResult subscribeDataInd (EventSubscriber subscriber);

    MacId getMacId();
}
