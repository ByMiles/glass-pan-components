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

/**
 * Wraps all addressing parameter necessary for transmission over IEEE 802.15.4 .
 *
 * @author Miles Lorenz
 * @version 1.0
 */
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
