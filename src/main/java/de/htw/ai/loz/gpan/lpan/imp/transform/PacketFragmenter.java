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

import java.util.LinkedList;
import java.util.List;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.toHex;

/**
 * Helper class for {@code FrameComposer}
 * @author Miles Lorenz
 * @version 1.0
 */
public class PacketFragmenter {

    private List<byte[]> fragments;
    private byte[] unfragmentedFrame;
    private int datagramTag;
    private int availableFrameSize;
    private int offset;

    public PacketFragmenter(byte[] unfragmentedFrame, int maxFrameSize, int datagramTag) {
        this.unfragmentedFrame = unfragmentedFrame;
        this.datagramTag = datagramTag;
        fragments = new LinkedList<>();
        availableFrameSize = maxFrameSize - 5; // fragmentation header size
        availableFrameSize -= availableFrameSize % 8; // only length in octets are allowed
        offset = 0;
    }

    public byte[][] getFrames() {
        byte leadingByteInFirst = (byte) (0b1100_0000 + (unfragmentedFrame.length >> 8));
        byte leadingByteInFollowing = (byte) (0b1110_0000 + (unfragmentedFrame.length >> 8));
        byte datagramSize = (byte) unfragmentedFrame.length;
        byte datagramTagHigh = (byte) (datagramTag >> 8);
        byte datagramTagLow = (byte) datagramTag;
        int offset = 0;
        byte[] currentHeader = new byte[]{leadingByteInFirst, datagramSize, datagramTagHigh, datagramTagLow};
        int currentFrameSize;
        int remaining;
        int fragmentPointer;
        while (offset < unfragmentedFrame.length) {
            fragmentPointer = 0;
            remaining = unfragmentedFrame.length - offset;
            currentFrameSize = availableFrameSize > remaining
                    ? remaining
                    : availableFrameSize;

             byte[] fragment = new byte[currentFrameSize + currentHeader.length];
            for (int i = 0; i < currentHeader.length; i++) {
                fragment[fragmentPointer++] = currentHeader[i];
            }
            while (fragmentPointer < fragment.length) {
                fragment[fragmentPointer++] = unfragmentedFrame[offset++];
            }
            fragments.add(fragment);
            currentHeader = new byte[]{leadingByteInFollowing, datagramSize, datagramTagHigh, datagramTagLow, (byte) (offset / 8)};
        }
       return fragments.toArray(new byte[0][]);
    }
}
