package de.htw.ai.loz.gpan.mac.broker;

import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.msg.MacId;

public interface MacBroker extends EventPublisher {

    ConfirmationResult startAMac(MacId desc);
    ConfirmationResult postFrameCommands(String macId, MacDataCmd[] cmds);
    MacId[] getAllMacIds();
}
