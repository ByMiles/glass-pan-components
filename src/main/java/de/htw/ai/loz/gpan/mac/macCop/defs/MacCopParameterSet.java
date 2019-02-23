package de.htw.ai.loz.gpan.mac.macCop.defs;


public class MacCopParameterSet {

    private byte[] addrExt;
    private byte[] addrShort;
    private byte[] panId;
    private byte logicalChannel;

    private byte[] addrExtAsParam;
    private byte[] addrShortAsParam;
    private byte[] panIdAsParam;
    private byte[] logicalChannelAsParam;

    public MacCopParameterSet (
            byte[] addrExt,
            byte[] addrShort,
            byte[] panId,
            byte channel
    ) {
        this.addrExt = addrExt;
        this.addrShort = addrShort;
        this.panId = panId;
        this.logicalChannel = channel;

        this.addrExtAsParam = new byte[17];
        this.addrExtAsParam[0] = Parameter.ZMAC_EXTENDED_ADDRESS.getValue();
        for (int i = 0; i < 8; i++) {
            this.addrExtAsParam[i + 1] = this.addrExt[0];
        }

        this.addrShortAsParam = new byte[17];
        this.addrShortAsParam[0] = Parameter.ZMAC_SHORT_ADDRESS.getValue();
        this.addrShortAsParam[1] = this.addrShort[0];
        this.addrShortAsParam[2] = this.addrShort[1];

        this.panIdAsParam = new byte[17];
        this.panIdAsParam[0] = Parameter.ZMAC_PANID.getValue();
        this.panIdAsParam[1] = this.panId[0];
        this.panIdAsParam[2] = this.panId[1];

        this.logicalChannelAsParam = new byte[17];
        this.logicalChannelAsParam[0] = Parameter.ZMAC_LOGICAL_CHANNEL.getValue();
        this.logicalChannelAsParam[1] = this.logicalChannel;
    }

    public byte[] getAddrExt() {
        return addrExt;
    }

    public void setAddrExt(byte[] addrExt) {
        this.addrExt = addrExt;
    }

    public byte[] getAddrShort() {
        return addrShort;
    }

    public void setAddrShort(byte[] addrShort) {
        this.addrShort = addrShort;
    }

    public byte[] getPanId() {
        return panId;
    }

    public void setPanId(byte[] panId) {
        this.panId = panId;
    }

    public byte getLogicalChannel() {
        return logicalChannel;
    }

    public void setLogicalChannel(byte logicalChannel) {
        this.logicalChannel = logicalChannel;
    }

    public byte[] getAddrExtAsParam() {
        return addrExtAsParam;
    }

    public byte[] getAddrShortAsParam() {
        return addrShortAsParam;
    }

    public byte[] getPanIdAsParam() {
        return panIdAsParam;
    }

    public byte[] getLogicalChannelAsParam() {
        return logicalChannelAsParam;
    }
}

