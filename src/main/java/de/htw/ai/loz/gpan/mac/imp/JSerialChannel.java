package de.htw.ai.loz.gpan.mac.imp;


import com.fazecast.jSerialComm.SerialPort;
import de.htw.ai.loz.gpan.mac.adaptation.Channel;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JSerialChannel implements Channel {

    private SerialPort port;
    private String name;
    private boolean isClosed;
    private final BlockingQueue<ChannelDataCmd> inQueue;
    private final BlockingQueue<ChannelDataInd> outQueue;

    public JSerialChannel() {
        inQueue = new LinkedBlockingQueue<>();
        outQueue = new LinkedBlockingQueue<>();
    }

    public void startStreaming() throws Exception {
        isClosed = false;
        loop();
    }

    @Override
    public void stopStreaming() {
        System.out.println(name + " CLOSED");
        isClosed = true;
        port.closePort();
        port = null;
    }

    public boolean open() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        for (SerialPort possiblePort : serialPorts) {
            possiblePort.openPort();

            if (possiblePort.isOpen()) {
                this.port = possiblePort;
                port.setBaudRate(38400);
                port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
                name ="[" + port.getSystemPortName() + "]";
                return true;
            }
        }
        return false;
    }

    private void loop() throws Exception {
        while (!isClosed) {
            try {
                tryReadUsbData();
                tryWriteUsbData();
            } catch (Exception e) {
                port.closePort();
                port = null;
                if (!open())
                    throw e;
            }
        }
    }

    private void tryWriteUsbData() throws Exception {

        if (!inQueue.isEmpty()) {
            ChannelDataCmd cmd = inQueue.poll();
            if (cmd != null) {
                port.getOutputStream().write(cmd.getDataBuffer());
                // System.out.println(name + " OUT: " + toHex(cmd.getDataBuffer()));
            }
        }
    }

    private void tryReadUsbData() throws Exception {

        int dataAvailable = port.bytesAvailable();
        if (!port.isOpen()) throw new Exception("Port is closed");
        if (dataAvailable > 0) {
            byte[] buffer = new byte[dataAvailable];
            int dataLength = port.getInputStream().read(buffer);
            if (dataLength < dataAvailable) {
                buffer = Arrays.copyOf(buffer, dataLength);
            }
            // System.out.println(name + "  IN: " + toHex(buffer));
            outQueue.put(new ChannelDataInd(buffer));
        }
    }

    public BlockingQueue<ChannelDataCmd> getCommandQueue() {
        return inQueue;
    }

    public BlockingQueue<ChannelDataInd> getEventQueue() {
        return outQueue;
    }

    public String getName(){
        return name;
    }
}
