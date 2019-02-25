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


import de.htw.ai.loz.gpan.mac.msg.ChannelDataCmd;
import de.htw.ai.loz.gpan.mac.msg.ChannelDataInd;
import de.htw.ai.loz.gpan.mac.msg.ConfirmationResult;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPEventSet;
import de.htw.ai.loz.gpan.mac.macCop.defs.MacCopCommandSet;
import de.htw.ai.loz.gpan.mac.adaptation.StopHandler;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.*;
import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCopStatus.OFFLINE;

public class MacStopper extends MacHandler implements StopHandler {



    public MacStopper() {
        this.macStatus = OFFLINE;
    }

    private void fireResetMsg() {
        commandQueue.add(new ChannelDataCmd(
                generateMessage(
                        MacCopCommandSet.RESET,
                        stubByteTrue
                )));
    }

    @Override
    public ConfirmationResult stop(BlockingQueue<ChannelDataCmd> commandQueue, BlockingQueue<ChannelDataInd> eventQueue) {
        this.commandQueue = commandQueue;
        this.eventQueue = eventQueue;
        LocalTime resettingStart = LocalTime.now();
        fireResetMsg();
        ConfirmationResult result;
        while ((result = awaitEvent()) == null) {
            if (Duration.between(resettingStart, LocalTime.now()).getSeconds() > 3){
                result = ConfirmationResult.TIMEOUT;
                break;
            }
        }
        return result;
    }


    @Override
    protected void handleUnknown(ChannelDataInd unknownInd) {
        System.out.println(name + " Unknown while stopping: " + toHex(unknownInd.getBody()));
    }

    protected ConfirmationResult processEvent(MacCoPEventSet eventType, byte[] body){
        return ConfirmationResult.SUCCESS;
    }
}
