package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;

import java.util.concurrent.BlockingQueue;

public interface StopHandler {

    ConfirmationResult stop(BlockingQueue<ChannelDataCmd> commandQueue,
                            BlockingQueue<ChannelDataInd> eventQueue);

    void setName(String name);
}
