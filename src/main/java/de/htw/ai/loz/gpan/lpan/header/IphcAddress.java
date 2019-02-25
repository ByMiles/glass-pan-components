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
package de.htw.ai.loz.gpan.lpan.header;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.regex.Pattern;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.toHex;

/**
 * Wraps an IPv6 address and provides methods to check it for compatibility
 * ( @link https://tools.ietf.org/html/rfc6282 ).
 *
 * @author Miles Lorenz
 * @version 1.0
 */
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
        this.address = this.toAddress();
    }

    /**
     * Compares the address with the pattern for multicast addresses that are
     * compressible to 1 byte.
     * @return True if the address is multicast and compressible to 1 byte.
     */
    public boolean isMulticast_8_compressable() {
        return MULTICAST_8_PATTERN.matcher(addr).matches();
    }

    /**
     * Compares the address with the pattern for multicast addresses that are
     * compressible to 4 bytes.
     * @return True if the address is multicast and compressible to 4 bytes.
     */
    public boolean isMulticast_32_compressable() {
        return MULTICAST_32_PATTERN.matcher(addr).matches();
    }

    /**
     * Compares the address with the pattern for multicast addresses that are
     * compressible to 6 bytes.
     * @return True if the address is multicast and compressible to 6 bytes.
     */
    public boolean isMulticast_48_compressable() {
        return MULTICAST_48_PATTERN.matcher(addr).matches();
    }

    /**
     * Compares the address with the pattern for unicast addresses that are
     * compressible to 2 bytes. This requires an auto configured address.
     * @return True if the address is unicast and compressible to 2 bytes.
     */
    public boolean isUnicastAutoConfigured() {

        return UNICAST_16_PATTERN.matcher(addr).matches();
    }

    /**
     * Gets the two bytes of the address which represent the short address.
     * @return The two least significant bytes of the address.
     */
    public byte[] getUnicastAutoConfiguredSignificant() {

        byte[] addressBytes = address.getBytes();
        return new byte[] {addressBytes[14], addressBytes[15]};
    }

    /**
     * Gets the byte of the address which represents the multicast group.
     * @return The least significant byte of the address.
     */
    public byte[] getMulticast_8_ConfiguredSignificant() {

        return new byte[] {address.getBytes()[15]};
    }


    /**
     * Composes an IPv6 address by adding the given bytes to a stub.
     * @param highSignificant The higher byte of the short address.
     * @param lowSignificant The lower byte of the short address.
     * @return An auto configured unicast link local IPv6 address
     */
    public static String createLinkLocalUnicastAddress(byte highSignificant, byte lowSignificant) {
        return UNICAST_16_STUB + Integer.toHexString((((highSignificant << 8) & 0xff) + (lowSignificant & 0xff)));
    }

    /**
     * Composes an IPv6 address by adding the given byte to a stub.
     * @param significant The byte representing the multicast group.
     * @return An link local multicast IPv6 address.
     */
    public static String createLinkLocalMulticastAddress(byte significant) {
        return MULTICAST_8_STUB + Integer.toHexString((significant & 0xff));
    }
}
