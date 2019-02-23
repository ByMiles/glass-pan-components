package de.htw.ai.loz.gpan.mac.macCop.defs;

import java.util.Arrays;

public class MacCoPStatic {

    private final static byte addrModeShort = 2;
    private final static byte txOptionRequireAck = 1;
    public final static byte[] stubByteTrue = new byte[]{1};
    private final static byte noBeaconOrder = 15;

    public static int fetchSOP(byte[] buffer) {

        if (buffer == null) return 0;
        int pointer = 0;

        do {
            if (pointer >= buffer.length - 1) return 0;
        } while ((buffer[pointer++]  & 0xff) == 0xff); // this was sop 254

        return pointer;
    }

    public static byte[] generateMessage(MacCopCommandSet cmd, byte[] data) {

        int dataLength;
        if (data == null)
            dataLength = 0;
        else
            dataLength = data.length;

        byte[] tmpBytes = new byte[dataLength + 5];
        int pointer = 0;
        tmpBytes[pointer++] = (byte) 254; // SOP
        tmpBytes[pointer++] = (byte) dataLength;
        tmpBytes[pointer++] = (byte) cmd.getCmd0();
        tmpBytes[pointer++] = (byte) cmd.getCmd1();

        if (data != null) {
            for (byte dataByte : data) {
                tmpBytes[pointer++] = dataByte;
            }
        }
        tmpBytes[pointer] = calcFCS(tmpBytes);
        return tmpBytes;
    }

    public static boolean validateFCS(byte[] bytes){
        return bytes[bytes.length-1] == calcFCS(bytes);
    }

    public static byte calcFCS(byte[] bytes) {
        byte xorResult;
        xorResult = 0;
        for (int i = 1; i < bytes.length - 1; i++) {
            xorResult = (byte) (xorResult ^ bytes[i]);
        }
        return xorResult;
    }

    public static String toHex(int b) {
        String hex = Integer.toHexString((b & 0xff));
        if (hex.length() == 1)
            hex = "0" + hex;
        hex += " ";
        return hex;
    }

    public static String toHex(byte[] bytes) {
        StringBuilder bString = new StringBuilder();
        for (byte b : bytes) {
            bString.append(toHex(b));
        }
        return bString.toString();
    }

    public static byte[] fromHexString(String hexString) throws Exception {

        try {
            if (hexString.substring(0, 1).equals(" "))
                hexString = hexString.substring(1);
            String[] res = hexString.split(" ");
            byte[] bytes = new byte[res.length];
            for (int i = 0; i < res.length; i++)
                bytes[i] = (byte) Integer.parseInt(res[i], 16);
            return bytes;
        } catch (Exception e){throw new Exception();}
    }


    public static byte[] generateStartMsg(byte[] panId, byte channel){
        byte[] stub = new byte[23];
        stub[4] = panId[0];
        stub[5] = panId[1];
        stub[6] = channel;
        stub[8] = noBeaconOrder;
        stub[9] = noBeaconOrder;
        stub[10] = 1;

        return stub;
    }

    public static byte[] generateDataMsg(int panId, int shortAddr, int handle, byte[] data) {

        byte[] stub = new byte[28 + data.length];
        stub[0] = addrModeShort;
        stub[1] = (byte) (shortAddr >> 8);
        stub[2] = (byte) shortAddr;
        stub[9] = (byte) (panId >> 8);
        stub[10] = (byte) panId;
        stub[11] = addrModeShort;
        stub[12] = (byte) handle;
        stub[13] = txOptionRequireAck;
        stub[27] = (byte) data.length;

        for (int i = 0; i < data.length; i++) {
            stub[28 + i] = data[i];
        }
        return stub;
    }


    public static int resolveDestAddrFromDataInd(byte[] body) {
        return ((body[10] & 0xff) << 8) + (body[11] & 0xff);
    }

    public static int resolveDestPanIdFromDataInd(byte[] body) {
        return ((body[26] & 0xff) << 8) + (body[27] & 0xff);
    }

    public static int resolveSourceAddrFromDataInd(byte[] body) {
        return ((body[1] & 0xff) << 8) + (body[2] & 0xff);

    }

    public static int resolveSourcePanIdFromDataInd(byte[] body) {
        return ((body[24] & 0xff) << 8) + (body[25] & 0xff);
    }

    public static int resolveLinkQualityFromDataInd(byte[] body) {
        return (body[28] & 0xff);
    }

    public static byte[] resolveDataFromDataInd(byte[] body) {
        return Arrays.copyOfRange(body, 44, 44 + body[43]);
    }

    public static byte[] generateExtAddr(int panId, int shortAddr) {
        byte[] extAddr = new byte[8];
        extAddr[0] = (byte)(panId >> 8);
        extAddr[1] = (byte)(panId);
        extAddr[3] = (byte)(0xfe);
        extAddr[4] = (byte)(0xff);
        extAddr[6] = (byte)(shortAddr >> 8);
        extAddr[7] = (byte)(shortAddr);
        return extAddr;
    }
}
