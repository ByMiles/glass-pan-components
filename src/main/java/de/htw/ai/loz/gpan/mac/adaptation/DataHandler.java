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

import de.htw.ai.loz.gpan.mac.msg.*;

import java.util.concurrent.BlockingQueue;

/**
 * Wraps functionalism's to transmit and receive data units over a channel.
 *     <ul>
 *         <li>Administrate access to channel at sending</li>
 *         <li>Mapping synchronous and asynchronous responses to commands</li>
 *         <li>Routing channel events</li>
 *     </ul>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public interface DataHandler extends Streamable {

    /**
     * Sets the queues for incoming and outgoing data units.
     * @param commandQueue The queue where to enqueue commands
     * @param eventQueue The queue where to dequeue events
     * @return this data handler
     */
    DataHandler prepareStreaming(
            BlockingQueue<ChannelDataCmd> commandQueue,
            BlockingQueue<ChannelDataInd> eventQueue);

    /**
     * Validate's, Enqueue's and monitor's the data units to transmit.
     * @param cmd The data unit to transmit.
     * @return A synchronous response for transmitting the data unit. Note, that this
     * response only indicates the reception of a valid data unit on the next layer,
     * the final transmit is confirmed asynchronously.
     */
    ConfirmationResult sendData(MacDataCmd cmd);

    /**
     * Blocks the process till an asynchronously confirmation arrived or a timeout occurred.
     * @param handleId A index to map command and response.
     * @return The result of the transmission.
     */
    ConfirmationResult awaitConfirmation(byte handleId);

    /**
     * Blocks the process till a data indication arrived or a timeout occurred.
     * @return a received data unit.
     * @throws Exception If interrupted.
     */
    MacDataInd awaitIndication() throws Exception;

    /**
     * Requeue's a received data unit.
     * @param ind Received data unit to requeue.
     */
    void reQueueIndication(MacDataInd ind);

    /**
     * Exposes all unresolved channel events in a queue.
     * @return The queue containing the unresolved channel events.
     */
    BlockingQueue<ChannelDataInd> getUnresolvedChannelDataIndications();

    /**
     * Sets the identifying name
     * @param name The name of this data handler.
     */
    void setName(String name);
}
