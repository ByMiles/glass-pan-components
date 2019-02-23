package de.htw.ai.loz.gpan.mac.imp;

import de.htw.ai.loz.gpan.mac.adaptation.CommandHandler;
import de.htw.ai.loz.gpan.mac.adaptation.DataHandler;
import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;

public class MacDataCmdHandler implements CommandHandler {

    private DataHandler dataHandler;

    @Override
    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public ConfirmationResult sendData(MacDataCmd cmd)  {

        ConfirmationResult lastResult = dataHandler.sendData(cmd);
        if (lastResult != ConfirmationResult.SUCCESS)
            return lastResult;

        return dataHandler.awaitConfirmation(cmd.getHandleId());
    }
}
