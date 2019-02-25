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
package de.htw.ai.loz.gpan.lpan.transform;

import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.DataFrame;
/**
 * Transforms a data unit from IEEE 802.15.4 format to IPv6 format
 * using 6lowPAN adaptation specifications.
 *
 *     Functionality contains:
 *     <ul>
 *         <li>Validation</li>
 *         <li>Storing of fragments</li>
 *         <li>Composing of fragments</li>
 *         <li>Decompression of the composed data unit</li>
 *     </ul>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public interface FrameToPacket {

    /**
     * Validates, stores and defragments if needed, and decompresses the given data unit.
     * @param macUrl The key identifying the user.
     * @param fragmentWrapper The possibly fragmented and compressed data unit.
     * @return An empty (if no packet was completed by the given data unit)
     * or the unfragmented decompressed IPv6 Packet.
     */
    ComposedPacket toComposedPacket(String macUrl, DataFrame fragmentWrapper);
    }
