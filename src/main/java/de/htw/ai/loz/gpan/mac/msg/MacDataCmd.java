package de.htw.ai.loz.gpan.mac.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MacDataCmd {


    private int destAddress;
    private int destPanId;
    private byte[] data;
    private byte handleId;

    public MacDataCmd() {}

    public MacDataCmd(int destPanId, int destAddress, String data) {

        this.destAddress = destAddress;
        this.destPanId = destPanId;
        this.data = data.getBytes(StandardCharsets.UTF_8);
    }

    public int getDestAddress() {
        return destAddress;
    }

    public int getDestPanId() {
        return destPanId;
    }

    public String getData() {
        return Base64.getEncoder().encodeToString(data);
    }

    @JsonIgnore
    public byte[] getBody() {
        return data;
    }

    public void setDestAddress(int destAddress) {
        this.destAddress = destAddress;
    }

    public void setDestPanId(int destPanId) {
        this.destPanId = destPanId;
    }

    public void setData(String data) {
        this.data = Base64.getDecoder().decode(data);
    }

    @JsonIgnore
    public byte getHandleId() {
        return handleId;
    }

    @JsonIgnore
    public void setHandleId(byte handleId) {
        this.handleId = handleId;
    }
}
