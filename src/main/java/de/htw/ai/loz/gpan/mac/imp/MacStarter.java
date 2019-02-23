package de.htw.ai.loz.gpan.mac.imp;

import de.htw.ai.loz.gpan.mac.macCop.defs.*;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.adaptation.StartHandler;
import java.util.concurrent.BlockingQueue;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.*;

public class MacStarter extends MacHandler implements StartHandler {

    private MacCopParameterSet parameters;

    public MacStarter() {

        this.macStatus = MacCopStatus.OFFLINE;
    }

    public ConfirmationResult start (
            BlockingQueue<ChannelDataCmd> commandQueue,
            BlockingQueue<ChannelDataInd> eventQueue,
            int channel,
            int panId,
            int shortAddr) {

        this.commandQueue = commandQueue;
        this.eventQueue = eventQueue;

        if (!initParameters(channel, panId, shortAddr))
            return ConfirmationResult.INVALID;
        
        while (macStatus != MacCopStatus.ONLINE) {
           ConfirmationResult result;
            if ((result = processStart()) != ConfirmationResult.SUCCESS){
                System.out.println(name + "START: FAIL: " + result.name());
                return result;
            }
        }
        System.out.println(name + "START: " + macStatus.name());
        return ConfirmationResult.SUCCESS;
    }

    private ConfirmationResult processStart() {
        changeStatusOnSuccess();
        switch (macStatus) {
            case RESETTING:
                fireResetMsg();
                break;
            case SUBSCRIBING:
                fireSubscribeMsg();
                break;
            case EXTENDEDSETTING:
                fireSetExtendedMsg();
                break;
            case SHORTSETTING:
                fireSetShortMsg();
                break;
            case STARTING:
                fireStartMsg();
                break;
            case ONLINE:
                return ConfirmationResult.SUCCESS;
        }
        return awaitEvent();
    }



    protected ConfirmationResult processEvent(MacCoPEventSet eventType, byte[] body) {

        if (eventType == MacCoPEventSet.RESET_CONFIRMATION)
            return ConfirmationResult.SUCCESS;
        MacCopEventStatus eventStatus = MacCopEventStatus.resolve(body[0]);

        if (isSuccessfulStartResponse(eventType, eventStatus))
            return awaitEvent();

        return MacCopEventStatus.map(eventStatus);
    }

    private boolean isSuccessfulStartResponse(MacCoPEventSet eventType, MacCopEventStatus eventStatus) {

        if (eventType == MacCoPEventSet.START_RESPONSE)
            return eventStatus == MacCopEventStatus.MAC_SUCCESS;
        return false;
    }

    private void changeStatusOnSuccess() {
        switch (macStatus) {
            case OFFLINE:
                macStatus = MacCopStatus.RESETTING;
                break;
            case RESETTING:
                macStatus = MacCopStatus.SUBSCRIBING;
                break;
            case SUBSCRIBING:
                macStatus = MacCopStatus.EXTENDEDSETTING;
                break;
            case EXTENDEDSETTING:
                macStatus = MacCopStatus.SHORTSETTING;
                break;
            case SHORTSETTING:
                macStatus = MacCopStatus.STARTING;
                break;
            case STARTING:
                macStatus = MacCopStatus.ONLINE;
        }
    }
    private boolean initParameters(int channel, int panId, int shortAddr) {

        if (channel < Channels24.CHANNEL_11.getLogical()
                || (channel & 0xff) > Channels24.CHANNEL_26.getLogical())
            return false;

        if ((panId < 0 || panId > 0xffff))
            return false;

        if ((shortAddr < 0 || shortAddr > 0xffff))
            return false;

        byte[] extAddr = MacCoPStatic.generateExtAddr(panId, shortAddr);
        parameters = new MacCopParameterSet(
                extAddr,
                new byte[]{
                        (byte) (shortAddr >> 8),
                        (byte) (shortAddr)},
                new byte[]{
                        (byte) (panId >> 8),
                        (byte) (panId)},
                (byte) channel);
        return true;
    }

    // FIRE channel-message

    private void fireResetMsg() {
        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.RESET,
                        stubByteTrue
                )));
    }

    private void fireSubscribeMsg() {

        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.SUBSCRIBE,
                        new byte[]{(byte) 0xff, (byte) 0xff, 1})
        ));
    }

    private void fireSetExtendedMsg() {

        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.SET_PARAM,
                        parameters.getAddrExtAsParam())));
    }

    private void fireSetShortMsg() {

        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.SET_PARAM,
                        parameters.getAddrShortAsParam())));
    }

    private void fireStartMsg() {

        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.START,
                        generateStartMsg(
                                parameters.getPanId(),
                                parameters.getLogicalChannel()))));
    }

    protected void handleUnknown(ChannelDataInd unknownInd) {
        System.out.println(name + " unknown while starting: " + toHex(unknownInd.getBody()));
    }

}
