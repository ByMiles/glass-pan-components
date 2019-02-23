package de.htw.ai.loz.gpan.mac.web;

import de.htw.ai.loz.gpan.mac.adaptation.*;
import de.htw.ai.loz.gpan.mac.imp.*;
import de.htw.ai.loz.gpan.mac.adaptation.Channel;
import de.htw.ai.loz.gpan.mac.adaptation.MacAdaptation;
import de.htw.ai.loz.gpan.mac.imp.JSerialChannel;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

public class MacBinder extends AbstractBinder {

    protected void configure() {

        // broker
        bind(de.htw.ai.loz.gpan.mac.imp.MacBroker.class).to(de.htw.ai.loz.gpan.mac.broker.MacBroker.class).in(Singleton.class);
        // channel
        bind(JSerialChannel.class).to(Channel.class);

        // adaptation
        bind(MacLayer.class).to(MacAdaptation.class);

        bind(MacStarter.class).to(StartHandler.class);
        bind(MacStopper.class).to(StopHandler.class);

        bind(MacDataHandler.class).to(DataHandler.class);
        bind(MacDataCmdHandler.class).to(CommandHandler.class);
        bind(MacDataIndHandler.class).to(IndicationHandler.class);
    }
}
