package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.broker.EventPublisher;

public interface IndicationHandler extends Streamable, EventPublisher {

    void setDataHandler(DataHandler transformer);
    boolean isActive(int allowedIdleSeconds);
}
