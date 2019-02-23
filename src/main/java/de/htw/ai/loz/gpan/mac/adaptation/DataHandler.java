package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.msg.*;

import java.util.concurrent.BlockingQueue;

public interface DataHandler extends Streamable {

    DataHandler prepareStreaming(
            BlockingQueue<ChannelDataCmd> commandQueue,
            BlockingQueue<ChannelDataInd> eventQueue);

    ConfirmationResult sendData(MacDataCmd cmd);

    ConfirmationResult awaitConfirmation(byte handleId);

    MacDataInd awaitIndication() throws Exception;

    void reQueueIndication(MacDataInd ind);

    BlockingQueue<ChannelDataInd> getUnresolvedChannelDataIndications();

    void setName(String name);
}
