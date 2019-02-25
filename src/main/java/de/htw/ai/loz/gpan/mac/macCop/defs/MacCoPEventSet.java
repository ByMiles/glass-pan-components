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
package de.htw.ai.loz.gpan.mac.macCop.defs;

public enum MacCoPEventSet {

    DATA_RESPONSE(0x62, 0x05, MacCopCommandSet.DATA),
    DATA_CONFIRMATION(0x42, 0x84, MacCopCommandSet.DATA),
    DATA_INDICATION (0x42, 0x85, MacCopCommandSet.DATA),
    START_RESPONSE(0x62, 0x03, MacCopCommandSet.START),
    START_CONFIRMATION(0x42, 0x8e, MacCopCommandSet.START),
    SET_PARAM_CONFIRMATION (0x62, 0x09, MacCopCommandSet.SET_PARAM),
    RESET_CONFIRMATION (0x61, 0x01, MacCopCommandSet.RESET),
    SUBSCRIBE_CONFIRMATION (0x67, 0x06, MacCopCommandSet.SUBSCRIBE);


    MacCoPEventSet(int cmd0, int cmd1, MacCopCommandSet command){

        this.cmd0 = (byte) cmd0;
        this.cmd1 = (byte) cmd1;
        this.command = command;
    }
    private final byte cmd0;
    private final byte cmd1;
    public final MacCopCommandSet command;

    private boolean isNotification(byte cmd0, byte cmd1){

        return (cmd0 == this.cmd0 && cmd1 == this.cmd1);
    }

    public static MacCoPEventSet resolveNotification (byte cmd0, byte cmd1){
        for (int i = 0; i < values().length; i++) {
            if (values()[i].isNotification(cmd0, cmd1))
                return values()[i];
        }
        return null;
    }
}
