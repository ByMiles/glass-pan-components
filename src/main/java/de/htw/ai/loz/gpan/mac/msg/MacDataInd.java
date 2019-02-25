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
