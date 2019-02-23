package de.htw.ai.loz.gpan.mac.imp;

import de.htw.ai.loz.gpan.mac.msg.*;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCopStatus;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPEventSet;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCopCommandSet;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCopEventStatus;
import de.htw.ai.loz.gpan.mac.adaptation.DataHandler;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.*;

public class MacDataHandler extends MacHandler implements DataHandler {

    private BlockingQueue<ChannelDataInd> unresolvedQueue;

    private final BlockingDeque<MacDataInd> inds;
    private final BlockingQueue<MacDataCnf> cnfs;
    private final BlockingQueue<MacDataRsp> rsps;
    private final Semaphore entry;
    private final ReentrantLock channelLock;
    private final ReentrantLock confirmationLock;
    private final Condition newConfirmation;
    private boolean isClosed;

    private Thread streamingThread;

    public MacDataHandler() {
        macStatus = MacCopStatus.ONLINE;
        inds = new LinkedBlockingDeque<>();
        cnfs = new LinkedBlockingQueue<>();
        unresolvedQueue = new LinkedBlockingQueue<>();
        rsps = new LinkedBlockingQueue<>();
        entry = new Semaphore(0);
        channelLock = new ReentrantLock();
        confirmationLock = new ReentrantLock();
        newConfirmation = confirmationLock.newCondition();
    }

    @Override
    public DataHandler prepareStreaming(
            BlockingQueue<ChannelDataCmd> commandQueue,
            BlockingQueue<ChannelDataInd> eventQueue) {
        this.commandQueue = commandQueue;
        this.eventQueue = eventQueue;
        return this;
    }

    public void startStreaming() throws Exception {
        isClosed = false;
        streamingThread = Thread.currentThread();
        entry.release(3);
        loop();
    }

    public void stopStreaming() {
        System.out.println("Called interrupt: " + name + " Framer");
        isClosed = true;
        streamingThread.interrupt();
    }

    private void loop() {
        while (!isClosed) {
            ConfirmationResult result = awaitEvent();
            if (!(result == ConfirmationResult.SUCCESS || result == ConfirmationResult.TIMEOUT)) {
                System.out.println(name + " etwas ist schief gegangen! => " + result.name());
            }
        }
    }

    protected ConfirmationResult processEvent(MacCoPEventSet eventType, byte[] body) {
        if (eventType == MacCoPEventSet.DATA_INDICATION) {
            try {
                inds.put(new MacDataInd(
                        resolveSourcePanIdFromDataInd(body),
                        resolveSourceAddrFromDataInd(body),
                        resolveDestPanIdFromDataInd(body),
                        resolveDestAddrFromDataInd(body),
                        resolveLinkQualityFromDataInd(body),
                        resolveDataFromDataInd(body)
                ));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(name + " Error on putting data ind (stored: " + inds.size() + ")");
                return ConfirmationResult.FAIL;
            }
            return ConfirmationResult.SUCCESS;
        }

        ConfirmationResult result =
                MacCopEventStatus.map(MacCopEventStatus.resolve(body[0]));

        switch (eventType) {
            case DATA_RESPONSE:
                try {
                    rsps.put(new MacDataRsp(result));
                } catch (Exception e) {
                    System.out.println(name + " Error on putting data rsp (stored: " + rsps.size() + ")");
                    return ConfirmationResult.FAIL;
                }
                break;
            case DATA_CONFIRMATION:
                try {
                    cnfs.put(new MacDataCnf(result, body[1]));
                } catch (Exception e) {
                    System.out.println(name + " Error on putting data cnf (stored: " + cnfs.size() + ")");
                    e.printStackTrace();
                    return ConfirmationResult.FAIL;
                }
                confirmationLock.lock();
                newConfirmation.signalAll();
                confirmationLock.unlock();
                break;
        }
        return result;
    }

    protected void handleUnknown(ChannelDataInd ind) {
        System.out.println(name + " Unknown while framing: " + toHex(ind.getBody()));
        unresolvedQueue.add(ind);
    }


    @Override
    public ConfirmationResult sendData(MacDataCmd cmd) {

        if (!validateDataParameter(cmd))
            return ConfirmationResult.INVALID;

        entry.acquireUninterruptibly();
        MacDataRsp rsp = null;
        try {
            channelLock.lock();
            rsps.clear();
            allocateHandleId(cmd.getHandleId());
            fireDataMessage(cmd);
            rsp = rsps.poll(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            channelLock.unlock();
        }

        ConfirmationResult result = (rsp == null)
                ? ConfirmationResult.TIMEOUT : rsp.getResult();

        if (result != ConfirmationResult.SUCCESS)
            entry.release();
        return result;
    }

    private void allocateHandleId(byte handleId) {
        confirmationLock.lock();
        cnfs.removeIf(macDataCnf -> macDataCnf.getHandleId() == handleId);
    }

    private void fireDataMessage(MacDataCmd cmd) {
        ChannelDataCmd channelDataCmd =
                new ChannelDataCmd(
                        generateMessage(
                                MacCopCommandSet.DATA,
                                generateDataMsg(
                                        cmd.getDestPanId(),
                                        cmd.getDestAddress(),
                                        cmd.getHandleId(),
                                        cmd.getBody()
                                )));
        try {
            commandQueue.put(channelDataCmd);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public ConfirmationResult awaitConfirmation(byte handleId) {

        ConfirmationResult result;
        LocalTime awaitingStart = LocalTime.now();
        while ((result = checkConfirmations(handleId)) == null) {
            if (Duration.between(awaitingStart, LocalTime.now()).getSeconds() > 5) {
                result = ConfirmationResult.TIMEOUT;
                break;
            }
        }
        entry.release();
        return result;
    }


    @Override
    public MacDataInd awaitIndication() throws Exception {
        if (isClosed)
            throw new Exception("Stream is closed");

        try {
            return inds.take();
        } catch (InterruptedException e){
            System.out.println(name + " Interrupted on awaiting indication");
        }
        return awaitIndication();
    }

    @Override
    public void reQueueIndication(MacDataInd ind) {
        inds.addFirst(ind);
    }


    private ConfirmationResult checkConfirmations(int handleId) {
        try {
            confirmationLock.lock();
            MacDataCnf awaitedCnf = null;
            if (!cnfs.isEmpty()) {
                for (MacDataCnf macDataCnf : cnfs) {
                    if (macDataCnf.getHandleId() == handleId) {
                        awaitedCnf = macDataCnf;
                        break;
                    }
                }
            }
            if (awaitedCnf != null) {
                cnfs.remove(awaitedCnf);
                return awaitedCnf.getCmdStatus();
            } else {
                newConfirmation.await(3, TimeUnit.SECONDS);
                return null;
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            confirmationLock.unlock();
        }
    }

    private boolean validateDataParameter(MacDataCmd cmd) {

        return (cmd.getDestAddress() >= 0 && cmd.getDestAddress() <= 0xffff
                &&
                cmd.getDestPanId() >= 0 && cmd.getDestPanId() <= 0xffff
                &&
                cmd.getHandleId() == 0
                &&
                cmd.getBody().length < 100);
    }

    @Override
    public BlockingQueue<ChannelDataInd> getUnresolvedChannelDataIndications() {
        return unresolvedQueue;
    }
}
