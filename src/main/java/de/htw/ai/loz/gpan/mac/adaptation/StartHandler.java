package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;

import java.util.concurrent.BlockingQueue;

public interface StartHandler {

    ConfirmationResult start(
            BlockingQueue<ChannelDataCmd> commandQueue,
            BlockingQueue<ChannelDataInd> eventQueue,
            int logicalChannel,
            int panId,
            int shortAddr);

    void setName(String name);
}
