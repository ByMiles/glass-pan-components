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

import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;

import java.util.concurrent.BlockingQueue;

/**
 * Wraps a connection to a bidirectional stream of bytes.
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public interface Channel extends Streamable {

    /**
     * Gets the queue containing outgoing messages as access to transmission.
     * @return The queue containing outgoing messages.
     */
    BlockingQueue<ChannelDataCmd> getCommandQueue();

    /**
     * Gets the queue containing incoming messages as access to reception.
     * @return The queue containing incoming messages.
     */
    BlockingQueue<ChannelDataInd> getEventQueue();

    /**
     * Establish a connection to the other side of the stream.
     * @return True, if the stream is ready to use.
     */
    boolean open();

    /**
     * @return The name of this channel as identifier.
     */
    String getName();
}
