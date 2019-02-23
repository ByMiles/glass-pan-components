package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;

public interface CommandHandler {

    ConfirmationResult sendData(MacDataCmd cmd);

    void setDataHandler(DataHandler dataHandler);
}
