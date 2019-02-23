package de.htw.ai.loz.gpan.mac.msg;

public class ChannelDataInd {

    private byte[] body;

    public ChannelDataInd(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }
}
