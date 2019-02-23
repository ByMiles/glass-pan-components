package de.htw.ai.loz.gpan.lpan.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.htw.ai.loz.gpan.lpan.header.IpV6Header;
import de.htw.ai.loz.gpan.lpan.header.LinkHeader;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ComposedPacket {

    private IpV6Header v6Header;
    private LinkHeader linkHeader;
    private PacketElement[] extensionHeaders;
    private PacketElement payload;
    private int datagramTag;
    private String response;

    public ComposedPacket() {
    }

    public IpV6Header getV6Header() {
        return v6Header;
    }

    public void setV6Header(IpV6Header v6Header) {
        this.v6Header = v6Header;
    }

    public PacketElement[] getExtensionHeaders() {
        return extensionHeaders;
    }

    public void setExtensionHeaders(PacketElement[] extensionHeaders) {
        this.extensionHeaders = extensionHeaders;
    }

    public PacketElement getPayload() {
        return payload;
    }

    public void setPayload(PacketElement payload) {
        this.payload = payload;
    }

    public int getDatagramTag() {
        return datagramTag;
    }

    public void setDatagramTag(int datagramTag) {
        this.datagramTag = datagramTag;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LinkHeader getLinkHeader() {
        return linkHeader;
    }

    public void setLinkHeader(LinkHeader linkHeader) {
        this.linkHeader = linkHeader;
    }
}
