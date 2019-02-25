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

public enum Channels24 {
    CHANNEL_NONE (0x00, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,}),
    CHANNEL_11 (0x0b, new byte[]{(byte) 0x00,(byte) 0x08,(byte) 0x00,(byte) 0x00,}),
    CHANNEL_12 (0x0c, new byte[]{(byte) 0x00,(byte) 0x10,(byte) 0x00,(byte) 0x00,}),
    CHANNEL_13 (0x0d, new byte[]{(byte) 0x00,(byte) 0x20,(byte) 0x00,(byte) 0x00,}),
    CHANNEL_14 (0x0e, new byte[]{(byte) 0x00,(byte) 0x40,(byte) 0x00,(byte) 0x00,}),
    CHANNEL_15 (0x0f, new byte[]{(byte) 0x00,(byte) 0x80,(byte) 0x00,(byte) 0x00,}),
    CHANNEL_16 (0x10, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x10,(byte) 0x00,}),
    CHANNEL_17 (0x11, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x20,(byte) 0x00,}),
    CHANNEL_18 (0x12, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x40,(byte) 0x00,}),
    CHANNEL_19 (0x13, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x00,}),
    CHANNEL_20 (0x14, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x10,(byte) 0x00,}),
    CHANNEL_21 (0x15, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x20,(byte) 0x00,}),
    CHANNEL_22 (0x16, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x40,(byte) 0x00,}),
    CHANNEL_23 (0x17, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x00,}),
    CHANNEL_24 (0x18, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01,}),
    CHANNEL_25 (0x19, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x02,}),
    CHANNEL_26 (0x1a, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x04,}),
    CHANNEL_ALL(0xff, new byte[]{(byte) 0x00,(byte) 0xff,(byte) 0xff,(byte) 0x07,});

    private final int logical;
    private final byte[] mask;

    Channels24(int logical, byte[] mask) {

        this.logical = logical;
        this.mask = mask;
    }

    public byte[] getMask() {
        return mask;
    }

    public int getLogical() {
        return logical;
    }

    public static Channels24 resolveToChannel(int logical) {
        for (Channels24 ch : values()) {
            if (logical == ch.logical)
                return ch;
        }
        return CHANNEL_NONE;
    }
}
