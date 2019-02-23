package de.htw.ai.loz.gpan.mac.imp;

import de.htw.ai.loz.gpan.mac.adaptation.*;
import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.msg.MacId;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MacLayer implements MacAdaptation {

    private Channel channel;


    @Inject
    private StartHandler startHandler;

    @Inject
    private StopHandler stopHandler;


    @Inject
    private DataHandler dataHandler;

    @Inject
    private IndicationHandler indHandler;

    @Inject
    private CommandHandler cmdHandler;

    private final ExecutorService executor;

    private boolean isStarted;
    private boolean hadException;
    private MacId macId;

    public MacLayer() {
        isStarted = false;
        executor = Executors.newFixedThreadPool(3);
        hadException = false;
    }

    @Override
    public ChannelDataInd takeUnresolvedChannelDataIndication() throws InterruptedException, NullPointerException {
        if (hadException) throw new NullPointerException();
        return dataHandler.getUnresolvedChannelDataIndications().take();
    }

    @Override
    public boolean isUnresolvedChannelDataIndicationAvailable() {
        return !hadException
                && dataHandler != null
                && !dataHandler.getUnresolvedChannelDataIndications().isEmpty();
    }

    @Override
    public ConfirmationResult startMac(Channel channel, MacId macId) {
        this.channel = channel;
        this.macId = macId;
        executor.execute(() -> {
            try {
                channel.startStreaming();
            } catch (Exception e) {
                System.out.println("ERROR CHANNEL");
                e.printStackTrace();
                hadException = true;
                dataHandler.stopStreaming();
            }
        });
        this.startHandler.setName(channel.getName());
        this.stopHandler.setName((channel.getName()));
        this.dataHandler.setName(channel.getName());
        ConfirmationResult result = this.startHandler.start(channel.getCommandQueue(), channel.getEventQueue(), macId.getChannel(), macId.getPanId(), macId.getAddress());
        if (result != ConfirmationResult.SUCCESS)
            return result;

        executor.execute(()-> {
            try {
                dataHandler
                        .prepareStreaming(channel.getCommandQueue(), channel.getEventQueue())
                        .startStreaming();
            } catch (Exception e) {
                hadException = true;
                System.out.println("ERROR FRAME_HANDLER");
                e.printStackTrace();
                stopMac();
            }
        });
        indHandler.setDataHandler(dataHandler);
        cmdHandler.setDataHandler(dataHandler);

        executor.execute(() -> {
                try {
                    indHandler.startStreaming();
                } catch (Exception e) {
                    System.out.println(channel.getName() + " Error on macInd-handler: " + e.getMessage());
                    e.printStackTrace();
                    hadException = true;
                    stopMac();
                }
        });

        isStarted = true;
        System.out.println(channel.getName() + " is " + macId.toString());
        return ConfirmationResult.SUCCESS;
    }

    @Override
    public ConfirmationResult stopMac() {
        indHandler.stopStreaming();
        dataHandler.stopStreaming();
        ConfirmationResult result = stopHandler.stop(channel.getCommandQueue(), channel.getEventQueue());
        channel.stopStreaming();
        System.out.println(channel.getName() + macId + " STOPPED");
        return result;
    }

    @Override
    public ConfirmationResult sendDataCmd(MacDataCmd cmd) {
        // TODO: handle some errors
        return cmdHandler.sendData(cmd);
    }

    @Override
    public ConfirmationResult subscribeDataInd(EventSubscriber subscriber) {
        if (hadException)
            return ConfirmationResult.FAIL;

        if (!isStarted)
            return ConfirmationResult.DENIED;

        return indHandler.subscribeAnEvent(macId.toString(), subscriber);
    }

    @Override
    public boolean stopIfInActive(int allowedIdleSeconds){
        System.out.println(channel.getName() + " stop if inactive...");
        if (indHandler.isActive(allowedIdleSeconds))
            return false;
        stopMac();
        return true;
    }

    @Override
    public MacId getMacId() {
        return this.macId;
    }
}
