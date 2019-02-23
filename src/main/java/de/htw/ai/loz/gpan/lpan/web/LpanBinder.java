package de.htw.ai.loz.gpan.lpan.web;

import de.htw.ai.loz.gpan.lpan.transform.FrameToPacket;
import de.htw.ai.loz.gpan.lpan.transform.PacketToFrame;
import de.htw.ai.loz.gpan.lpan.imp.transform.FrameComposer;
import de.htw.ai.loz.gpan.lpan.imp.transform.PacketComposer;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

public class LpanBinder extends AbstractBinder {


    @Override
    protected void configure() {
        bind(PacketComposer.class).to(FrameToPacket.class).in(Singleton.class);
        bind(FrameComposer.class).to(PacketToFrame.class).in(Singleton.class);
    }
}
