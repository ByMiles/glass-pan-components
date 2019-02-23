package de.htw.ai.loz.gpan.mac.msg;

public class MacId {

    private int channel;
    private int panId;
    private int address;

    public MacId(){}

    public MacId(int channel, int panId, int address) {
        this.channel = channel;
        this.panId = panId;
        this.address = address;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getPanId() {
        return panId;
    }

    public void setPanId(int panId) {
        this.panId = panId;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String toString(){
        return channel + "/" + panId + "/" + address;
    }
}
