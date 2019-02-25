/*
Copyright 2019 Miles Lorenz

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
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
