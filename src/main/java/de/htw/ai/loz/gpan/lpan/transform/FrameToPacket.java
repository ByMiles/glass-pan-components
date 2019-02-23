package de.htw.ai.loz.gpan.lpan.transform;

import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.DataFrame;

public interface FrameToPacket {

    ComposedPacket toComposedPacket(String macUrl, DataFrame fragmentWrapper);
    }
