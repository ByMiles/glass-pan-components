package de.htw.ai.loz.gpan.lpan.transform;

import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;

public interface PacketToFrame {

    FragmentedPacket toCompressedFragmented(ComposedPacket incoming);
    }
