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

        System.out.println("SEND DATA!");
        ConfirmationResult lastResult = dataHandler.sendData(cmd);
        if (lastResult != ConfirmationResult.SUCCESS)
            return lastResult;

        return dataHandler.awaitConfirmation(cmd.getHandleId());
    }
}
