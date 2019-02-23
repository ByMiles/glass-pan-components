package de.htw.ai.loz.gpan.lpan.header;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.regex.Pattern;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.toHex;

public class IphcAddress extends IPAddressString {

    public static Pattern MULTICAST_8_PATTERN = Pattern.compile("ff02::..?");
    public static Pattern MULTICAST_32_PATTERN = Pattern.compile("ff..::..?:..?.?.?");
    public static Pattern MULTICAST_48_PATTERN = Pattern.compile("ff..::..?:..?.?.?:..?.?.?");
    public static Pattern UNICAST_16_PATTERN = Pattern.compile("fe80::ff:fe00:..?.?.?");

    public static String MULTICAST_8_STUB = "ff02::";
    public static String UNICAST_16_STUB = "fe80::ff:fe00:";
    private IPAddress address;
    private String addr;
    public IphcAddress(String addr) throws AddressStringException {
        super(addr);
        this.addr = this.toAddress().toCompressedWildcardString();
        System.out.println(this.addr);
        this.address = this.toAddress();
    }

    public boolean isMulticast_8_compressable() {
        return MULTICAST_8_PATTERN.matcher(addr).matches();
    }


    public boolean isMulticast_32_compressable() {
        return MULTICAST_32_PATTERN.matcher(addr).matches();
    }


    public boolean isMulticast_48_compressable() {
        return MULTICAST_48_PATTERN.matcher(addr).matches();
    }

    public boolean isUnicastAutoConfigured() {

        return UNICAST_16_PATTERN.matcher(addr).matches();
    }

    public byte[] getUnicastAutoConfiguredSignificant() {

        byte[] addressBytes = address.getBytes();
        return new byte[] {addressBytes[14], addressBytes[15]};
    }

    public byte[] getMulticast_8_ConfiguredSignificant() {

        return new byte[] {address.getBytes()[15]};
    }


    public static String createLinkLocalUnicastAddress(byte highSignificant, byte lowSignificant) {
        return UNICAST_16_STUB + Integer.toHexString((((highSignificant << 8) & 0xff) + (lowSignificant & 0xff)));
    }

    public static String createLinkLocalMulticastAddress(byte significant) {
        return MULTICAST_8_STUB + Integer.toHexString((significant & 0xff));
    }
}
