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
package de.htw.ai.loz.gpan.mac.adaptation;
import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.broker.EventSubscriber;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.msg.MacId;
/**
 * Represents a transceiver interface that is able to communicate over IEEE 802.15.4
 * @author Miles Lorenz
 * @version 1.0
 */
public interface MacAdaptation {

    ChannelDataInd takeUnresolvedChannelDataIndication() throws InterruptedException;
    boolean isUnresolvedChannelDataIndicationAvailable();

    ConfirmationResult startMac(Channel channel, MacId macId);
    ConfirmationResult stopMac();
    boolean stopIfInActive(int allowedIdleSeconds);
    ConfirmationResult sendDataCmd (MacDataCmd cmd);
    ConfirmationResult subscribeDataInd (EventSubscriber subscriber);

    MacId getMacId();
}
