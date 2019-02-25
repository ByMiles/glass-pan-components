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
package de.htw.ai.loz.gpan.lpan.header;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Wraps all parameter necessary for 6lowPAN compression.
 * <p>
 *     (Traffic class and Flow label are not supported).
 * </p>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpV6Header {

    private String sourceAddress;
    private String destAddress;
    private int hopLimit;

    public IpV6Header() {
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public int getHopLimit() {
        return hopLimit;
    }

    public void setHopLimit(int hopLimit) {
        this.hopLimit = hopLimit;
    }
}
