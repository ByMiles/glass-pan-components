package de.htw.ai.loz.gpan.lpan.imp.transform;

import java.util.LinkedList;
import java.util.List;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.toHex;

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
        System.out.println("while rein");
        while (offset < unfragmentedFrame.length) {
            fragmentPointer = 0;
            remaining = unfragmentedFrame.length - offset;
            System.out.println(remaining + " " + offset + " " + unfragmentedFrame.length);
            currentFrameSize = availableFrameSize > remaining
                    ? remaining
                    : availableFrameSize;

            System.out.println(remaining + " " + offset + " " + currentFrameSize);
            byte[] fragment = new byte[currentFrameSize + currentHeader.length];
            for (int i = 0; i < currentHeader.length; i++) {
                fragment[fragmentPointer++] = currentHeader[i];
            }
            while (fragmentPointer < fragment.length) {
                fragment[fragmentPointer++] = unfragmentedFrame[offset++];
                System.out.println(fragmentPointer + " / " + fragment.length + " | " + offset + " / " + unfragmentedFrame.length);
            }
            fragments.add(fragment);
            currentHeader = new byte[]{leadingByteInFollowing, datagramSize, datagramTagHigh, datagramTagLow, (byte) (offset / 8)};
        }
        System.out.println("while raus");
        return fragments.toArray(new byte[0][]);
    }
}
