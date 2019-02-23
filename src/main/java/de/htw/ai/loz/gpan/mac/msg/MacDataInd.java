package de.htw.ai.loz.gpan.mac.msg;

import java.util.Base64;

public class MacDataInd {

    private int sourceAddress;
    private int destAddress;
    private int sourcePanId;
    private int destPanId;
    private int quality;
    private byte[] data;

    public MacDataInd(int sourcePanId, int sourceAddress, int destPanId, int destAddress, int quality, byte[] data) {
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
        this.sourcePanId = sourcePanId;
        this.destPanId = destPanId;
        this.quality = quality;
        this.data = data;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public int getDestAddress() {
        return destAddress;
    }

    public int getSourcePanId() {
        return sourcePanId;
    }

    public int getDestPanId() {
        return destPanId;
    }

    public int getQuality() {
        return quality;
    }

    public String getData() {
        return Base64.getEncoder().encodeToString(data);
    }
}
