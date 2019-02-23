package de.htw.ai.loz.gpan.mac.macCop.defs;

public enum MacCopCommandSet {

    RESET((byte) 0x21, (byte) 0x01),
    SUBSCRIBE((byte) 0x27, (byte) 0x06),
    SET_PARAM((byte) 0x22, (byte) 0x09),
    START((byte) 0x22, (byte) 0x03),
    DATA((byte) 0x22, (byte) 0x05);

    private byte cmd0;
    private byte cmd1;

    MacCopCommandSet(byte cmd0, byte cmd1){

        this.cmd0 = cmd0;
        this.cmd1 = cmd1;
    }

    public byte getCmd1() {
        return cmd1;
    }

    public byte getCmd0() {
        return cmd0;
    }
}
