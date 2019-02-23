package de.htw.ai.loz.gpan.mac.imp;


import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPEventSet;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCopCommandSet;
import de.htw.ai.loz.gpan.mac.adaptation.StopHandler;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.*;
import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCopStatus.OFFLINE;

public class MacStopper extends MacHandler implements StopHandler {



    public MacStopper() {
        this.macStatus = OFFLINE;
    }

    private void fireResetMsg() {
        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.RESET,
                        stubByteTrue
                )));
    }

    @Override
    public ConfirmationResult stop(BlockingQueue<ChannelDataCmd> commandQueue, BlockingQueue<ChannelDataInd> eventQueue) {
        this.commandQueue = commandQueue;
        this.eventQueue = eventQueue;
        LocalTime resettingStart = LocalTime.now();
        fireResetMsg();
        ConfirmationResult result;
        while ((result = awaitEvent()) == null) {
            if (Duration.between(resettingStart, LocalTime.now()).getSeconds() > 3){
                result = ConfirmationResult.TIMEOUT;
                break;
            }
        }
        return result;
    }


    @Override
    protected void handleUnknown(ChannelDataInd unknownInd) {
        System.out.println(name + " Unknown while stopping: " + toHex(unknownInd.getBody()));
    }

    protected ConfirmationResult processEvent(MacCoPEventSet eventType, byte[] body){
        return ConfirmationResult.SUCCESS;
    }
}
