package de.htw.ai.loz.gpan.lpan.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.htw.ai.loz.gpan.lpan.header.LinkHeader;

import java.util.Base64;

public class DataFrame {

    private LinkHeader linkHeader;

    private byte[] dataBytes;
    private String data;

    public DataFrame() {
    }

    public String getData() {
        return Base64.getEncoder().encodeToString(dataBytes);
    }

    public void setData(String data) {
        System.out.println("data: " + data);
        this.dataBytes =  Base64.getDecoder().decode(data);
    }

    public LinkHeader getLinkHeader() {
        return linkHeader;
    }

    public void setLinkHeader(LinkHeader linkHeader) {
        System.out.println("set header");
        this.linkHeader = linkHeader;
    }

    @JsonIgnore
    public void setDataBytes(byte[] dataBytes){
        this.dataBytes = dataBytes;
    }

    @JsonIgnore
    public byte[] getDataBytes() {
        return this.dataBytes;
    }
}
