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
package de.htw.ai.loz.gpan.lpan.imp.transform;

import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;
import de.htw.ai.loz.gpan.lpan.transform.PacketToFrame;


/**
 * Implementation of {@code PacketToFrame}, using a subset of <a href="https://tools.ietf.org/html/rfc6282"> RFC 68282 </a>
 * as specification.
 * @author Miles Lorenz
 * @version 1.0
 */
public class FrameComposer implements PacketToFrame {

    private static final int maxFrameSize = 96;
    @Override
    public FragmentedPacket toCompressedFragmented(ComposedPacket incoming) {
        try {
            return new Ipv6toLowPan().toCompressedFragmented(incoming, maxFrameSize);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            FragmentedPacket errorGram = new FragmentedPacket();
            errorGram.setDatagramTag(incoming.getDatagramTag());
            errorGram.setResponse("ERROR: " + e.getMessage());
            errorGram.setLinkHeader(incoming.getLinkHeader());
            return errorGram;
        }
    }
}
