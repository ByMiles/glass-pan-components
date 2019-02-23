package de.htw.ai.loz.gpan.lpan.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Base64;

public class PacketElement {

    private int protocol;
    private byte[] payload;

    public String  getPayload() {
        return Base64.getEncoder().encodeToString(payload);
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setPayload(String payload) {
        System.out.println("SET PAYLOAD::: " + payload);
        this.payload = Base64.getDecoder().decode(payload);
    }

    @JsonIgnore
    public void setPayloadBytes(byte[] payloadBytes){
        this.payload = payloadBytes;
    }

    @JsonIgnore
    public byte[] getPayloadBytes() {
        return this.payload;
    }
}
