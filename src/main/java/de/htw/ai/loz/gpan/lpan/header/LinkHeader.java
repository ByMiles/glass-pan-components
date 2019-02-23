package de.htw.ai.loz.gpan.lpan.header;

public class LinkHeader {

    private int channel;
    private int panId;
    private int linkSource;
    private int linkDestination;

    public LinkHeader() {
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

    public int getLinkSource() {
        return linkSource;
    }

    public void setLinkSource(int linkSource) {
        this.linkSource = linkSource;
    }

    public int getLinkDestination() {
        return linkDestination;
    }

    public void setLinkDestination(int linkDestination) {
        this.linkDestination = linkDestination;
    }
}
