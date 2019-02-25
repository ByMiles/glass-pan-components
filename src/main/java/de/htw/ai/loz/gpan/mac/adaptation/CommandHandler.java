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
package de.htw.ai.loz.gpan.mac.adaptation;

import de.htw.ai.loz.gpan.mac.msg.MacDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
/**
 * Exposes a method to send data units using a data handler.
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public interface CommandHandler {

    /**
     * Sends data units using the data handler
     * @param cmd the data unit to send
     * @return The confirmation of the command
     */
    ConfirmationResult sendData(MacDataCmd cmd);

    /**
     * Sets the data handler to use to send data.
     * @param dataHandler The used data handler.
     */
    void setDataHandler(DataHandler dataHandler);
}
