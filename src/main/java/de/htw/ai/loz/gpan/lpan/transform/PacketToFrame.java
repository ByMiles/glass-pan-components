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
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;
/**
 * Transforms a data unit from IPv6 format to IEEE 802.15.4 format
 * using 6lowPAN adaptation specifications.
 *
 * <p>
 *     Functionality contains:
 *     <ul>
 *         <li>Validation</li>
 *         <li>Compression</li>
 *         <li>Fragmentation</li>
 *     </ul>
 * @author Miles Lorenz
 * @version 1.0
 */
public interface PacketToFrame {

    /**
     * Validates, compresses and fragments the IPv6 packet to a bundle of fragments.
     * @param incoming Data unit containing the required IPv6 Packet content.
     * @return A bundle of fragments.
     */
    FragmentedPacket toCompressedFragmented(ComposedPacket incoming);
    }
