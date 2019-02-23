package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;

import java.util.concurrent.BlockingQueue;

public interface Channel extends Streamable {

    BlockingQueue<ChannelDataCmd> getCommandQueue();
    BlockingQueue<ChannelDataInd> getEventQueue();

    boolean open();
    String getName();
}
