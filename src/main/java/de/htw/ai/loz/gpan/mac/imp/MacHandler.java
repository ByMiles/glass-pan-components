package de.htw.ai.loz.gpan.mac.imp;

import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPEventSet;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCopStatus;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class MacHandler {


    protected BlockingQueue<ChannelDataCmd> commandQueue;
    protected BlockingQueue<ChannelDataInd> eventQueue;
    protected MacCopStatus macStatus;
    protected String name;

    protected ConfirmationResult awaitEvent() {

        ChannelDataInd nextInd;
        try {
            nextInd = eventQueue.poll(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            nextInd = null;
        }
        if (nextInd == null)
            return ConfirmationResult.TIMEOUT;

        byte[] body = nextInd.getBody();
        int bodyPointer = MacCoPStatic.fetchSOP(body);
        if (bodyPointer < 1) return awaitEvent();
        int length = (body[bodyPointer++] & 0xff);//length
        MacCoPEventSet eventType = MacCoPEventSet.resolveNotification(body[bodyPointer++], body[bodyPointer++]);
        if (eventType == null
                || !macStatus.isIntendedCommand(eventType.command)
            /*|| !validateFCS(body)*/) {

            handleUnknown(nextInd);
            return awaitEvent();
        }
        return processEvent(eventType, Arrays.copyOfRange(nextInd.getBody(), bodyPointer, bodyPointer + length));
    }


    public void setName(String name) {
        this.name = name;
    }


    protected abstract void handleUnknown(ChannelDataInd unknownInd);

    protected abstract ConfirmationResult processEvent(MacCoPEventSet eventType, byte[] body);
}
