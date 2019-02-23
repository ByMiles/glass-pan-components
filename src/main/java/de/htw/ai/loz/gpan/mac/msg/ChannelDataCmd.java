package de.htw.ai.loz.gpan.mac.msg;


public class ChannelDataCmd {

    private final byte[] dataBuffer;
    public ChannelDataCmd(byte[]dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public byte[] getDataBuffer() {
        return dataBuffer;
    }
}
