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

import de.htw.ai.loz.gpan.lpan.header.FragHeader;
import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.DataFrame;
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;
import de.htw.ai.loz.gpan.lpan.imp.store.FragmentQueue;
import de.htw.ai.loz.gpan.lpan.transform.FrameToPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@code FrameToPacket}, using a subset of <a href="https://tools.ietf.org/html/rfc6282"> RFC 68282 </a>
 * as specification.
 * @author Miles Lorenz
 * @version 1.0
 */
public class PacketComposer implements FrameToPacket {

    private final Map<String, FragmentQueue> queueMap;

    public PacketComposer() {
        this.queueMap = new ConcurrentHashMap<>();
    }

    public ComposedPacket toComposedPacket(String macUrl, DataFrame fragmentWrapper) {

        byte[] fragment = fragmentWrapper.getDataBytes();

        try {

            if (!FragHeader.isFragmented(fragment)) {
                return new LowPanToIpv6().unFragmentedToComposed(fragmentWrapper);
            }

            int sourceKey = fragmentWrapper.getLinkHeader().getLinkSource();
            int datagramTag = FragHeader.getDatagramTag(fragment);
            String key = macUrl + "/" + sourceKey + "/" + datagramTag;

            FragmentQueue queue = queueMap.computeIfAbsent(key, s -> new FragmentQueue());
            FragmentedPacket fragmented = queue.tryQueueGetComplete(fragment);

            if (fragmented != null) {
                fragmented.setDatagramTag(datagramTag);
                ComposedPacket composed = new LowPanToIpv6().fragmentedToComposed(fragmented);
                composed.setLinkHeader(fragmentWrapper.getLinkHeader());
                return composed;
            }

            ComposedPacket emptyResponse = new ComposedPacket();
            emptyResponse.setDatagramTag(datagramTag);
            emptyResponse.setResponse("FRAGMENTED");
            emptyResponse.setLinkHeader(fragmentWrapper.getLinkHeader());
            return emptyResponse;

        }catch (Exception e) {
            e.printStackTrace();
            ComposedPacket errorResponse = new ComposedPacket();
            errorResponse.setLinkHeader(fragmentWrapper.getLinkHeader());
            errorResponse.setResponse("ERROR: " + e.getMessage());
            return errorResponse;
        }
    }
}
