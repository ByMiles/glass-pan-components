package de.htw.ai.loz.gpan.lpan.imp.transform;

import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;
import de.htw.ai.loz.gpan.lpan.transform.PacketToFrame;


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
