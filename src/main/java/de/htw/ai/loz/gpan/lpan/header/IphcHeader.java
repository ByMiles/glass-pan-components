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

import com.fasterxml.jackson.databind.ObjectMapper;
import inet.ipaddr.AddressStringException;

import java.io.IOException;
/**
 * Contains constants and static methods to process IPv6 packets in a 6lowPAN environment.
 * ( @link https://tools.ietf.org/html/rfc6282 ).
 * 
 * <ul>
 *     <li>handling iphc dispatches</li>
 *     <li>handling extension header dispatches</li>
 *     <li>handling udp dispatches</li>
 * </ul>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public class IphcHeader {

    public static final int HOP_LIMIT_CARRIED_IN_LINE = 0;
    public static final int HOP_LIMIT_COMPRESSED_1 = 1;
    public static final int HOP_LIMIT_COMPRESSED_64 = 2;
    public static final int HOP_LIMIT_COMPRESSED_255 = 3;


    public static final int ADDRESS_MODE_UNSPECIFIED = 0;
    public static final int ADDRESS_MODE_128 = 0;
    public static final int ADDRESS_MODE_64 = 1;
    public static final int ADDRESS_MODE_48 = 1;
    public static final int ADDRESS_MODE_32 = 2;
    public static final int ADDRESS_MODE_16 = 2;
    public static final int ADDRESS_MODE_8 = 3;
    public static final int ADDRESS_MODE_0 = 3;

    public static final int NEXT_HEADER_UDP = 17;
    public static final int NEXT_HEADER_ICMP = 58;
    public static final int NEXT_HEADER_HOP_BY_HOP = 0;
    public static final int NEXT_HEADER_ROUTING = 43;
    public static final int NEXT_HEADER_FRAGMENT = 44;
    public static final int NEXT_HEADER_DESTINATION = 60;
    public static final int NEXT_HEADER_MOBILITY = 135;

    public static final int NEXT_HEADER_COMPRESSED_UDP = 0b11110000;
    public static final int NEXT_HEADER_COMPRESSED_HOP_BY_HOP = 0b11100001;
    public static final int NEXT_HEADER_COMPRESSED_ROUTING = 0b11100011;
    public static final int NEXT_HEADER_COMPRESSED_FRAGMENT = 0b11100101;
    public static final int NEXT_HEADER_COMPRESSED_DESTINATION = 0b11100111;
    public static final int NEXT_HEADER_COMPRESSED_MOBILITY = 0b11101001;


    public static final boolean CONTEXT_NOT_SUPPORTED = false;
    public static final int NO_FLOW_TRAFFIC_STUB = 0b0111_1000;
    public static final int UDP_COMPRESSED_CHECKSUM_INLINE = 0b11110100;
    public static final int UDP_THRESHOLD_FOR_BIG_COMPRESSION = 0xf0b;
    public static final int UDP_THRESHOLD_FOR_SMALL_COMPRESSION = 0xf0;
    public static final int UDP_COMPRESSION_MODE_BIG = 3;
    public static final int UDP_COMPRESSION_MODE_SMALL_SOURCE = 2;
    public static final int UDP_COMPRESSION_MODE_SMALL_DEST = 1;
    public static final int UDP_COMPRESSION_MODE_ONLY_LENGTH = 0;


    /**
     * Generates an iphc dispatch from the given parameters and used constants.
     * 
     * @param isNextHeaderCompressed True if next header is compressed
     * @param hopLimit On of the constants representing a valid hop limit compression.
     * @param isContextIdentifierExtensionUsed True if context is used
     * @param isSourceAddressCompressionStateful True if context is used for compressing the source address
     * @param sourceAddressMode The mode used for compressing the source address
     * @param isMulticast True if the destination address is a multicast address
     * @param isDestAddressCompressionStateful True if context is used for compressing the destination address 
     * @param destAddressMode The mode used for compressing the destination address
     * @return The generated iphc dispatch
     */
    public static byte[] generateCompressedHeader(
            boolean isNextHeaderCompressed,
            int hopLimit,
            boolean isContextIdentifierExtensionUsed,
            boolean isSourceAddressCompressionStateful,
            int sourceAddressMode,
            boolean isMulticast,
            boolean isDestAddressCompressionStateful,
            int destAddressMode) {

        return new byte[]{
                (byte) (NO_FLOW_TRAFFIC_STUB +
                        (isNextHeaderCompressed ? 4 : 0) +
                        hopLimit),
                (byte) ((isContextIdentifierExtensionUsed ? 128 : 0) +
                        (isSourceAddressCompressionStateful ? 64 : 0) +
                        (sourceAddressMode << 4) +
                        (isMulticast ? 8 : 0) +
                        (isDestAddressCompressionStateful ? 2 : 0) +
                        (destAddressMode))
        };
    }

    /**
     * Extracts the compression of the next header from the iphc dispatch.
     * @param packet A compressed not fragmented data unit
     * @param headerPosition The position of the iphc dispatch
     * @return True if the next header is compressed
     */
    public static boolean isNextHeaderCompressed(byte[] packet, int headerPosition) {

        return (packet[headerPosition] & 0b0000_0100) > 0;
    }

    /**
     * Extracts the constant for the hop limit from the iphc dispatch.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc header.
     * @return The constant representing the compressed hop limit
     */
    public static int hopLimit(byte[] packet, int headerPosition) {

        return (packet[headerPosition] & 0b0000_0011);
    }

    /**
     * Checks for usage of context for address compression.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc header.
     * @return True if context is used for address compression.
     */
    public static boolean isContextIdentifierExtensionUsed(byte[] packet, int headerPosition) {

        return (packet[headerPosition + 1] & 0b1000_0000) > 0;
    }

    /**
     * Checks for usage of context for source address compression.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc header.
     * @return True if context is used for source address compression.
     */
    public static boolean isSourceAddressCompressionStateful(byte[] packet, int headerPosition) {

        return (packet[headerPosition + 1] & 0b0100_0000) > 0;
    }

    /**
     * Extracts the used mode for source address compression from the iphc dispatch.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc dispatch.
     * @return The mode used for source address compression
     */
    public static int sourceAddressMode(byte[] packet, int headerPosition) {

        return ((packet[headerPosition + 1] & 0b0011_0000) >> 4);
    }

    /**
     * Checks the iphc dispatch, if this the packet is a multicast.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc dispatch.
     * @return True if the packet is a multicast.
     */
    public static boolean isMulticast(byte[] packet, int headerPosition) {

        return (packet[headerPosition + 1] & 0b0000_1000) > 0;
    }
    
    /**
     * Checks for usage of context for destination address compression.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc header.
     * @return True if context is used for destination address compression.
     */
    public static boolean isDestAddressCompressionStateful(byte[] packet, int headerPosition) {

        return (packet[headerPosition + 1] & 0b0000_0100) > 0;
    }

    /**
     * Extracts the used mode for destination address compression from the iphc dispatch.
     * @param packet An compressed not fragmented packet.
     * @param headerPosition The position of the iphc dispatch.
     * @return The mode used for destination address compression
     */
    public static int destAddressMode(byte[] packet, int headerPosition) {

        return (packet[headerPosition + 1] & 0b0000_0011);
    }


    /**
     * Compares the given value to all specified (by RFC 6282) dispatches for extension headers.
     * @param compressed The possibly compressed value of a next header parameter in an IPv6 or extension header.
     * @return True if the compressed equals one of the specified (RFC 6282) dispatches for extension headers.
     */
    public static boolean isCompressedExtensionHeader(int compressed) {
        return (
                compressed == NEXT_HEADER_COMPRESSED_HOP_BY_HOP
                        || compressed == NEXT_HEADER_COMPRESSED_ROUTING
                        || compressed == NEXT_HEADER_COMPRESSED_DESTINATION
                        || compressed == NEXT_HEADER_COMPRESSED_MOBILITY
        );
    }

    /**
     * Checks if the given value equals the udp dispatch (as only supported upper layer protocol).
     * @param compressed The possibly compressed value of a next header parameter in an IPv6 or extension header.
     * @return True if the upper layer is compressed
     */
    public static boolean isCompressedUpperLayer(int compressed) {
        return ((compressed & 0b1111_0000) == NEXT_HEADER_COMPRESSED_UDP);
    }

    /**
     * Compares the given value to all possible dispatches (RFC 6282)
     * and returns the uncompressed value or 255, if none was found.
     * @param compressed The possibly compressed value of a next header parameter in an IPv6 or extension header.
     * @return The uncompressed protocol number corresponding to the dispatch or 255.
     */
    public static int deCompressExtensionProtocol(int compressed) {
        switch (compressed) {
            case NEXT_HEADER_COMPRESSED_HOP_BY_HOP:
                return NEXT_HEADER_HOP_BY_HOP;
            case NEXT_HEADER_COMPRESSED_ROUTING:
                return NEXT_HEADER_COMPRESSED_ROUTING;
            case NEXT_HEADER_COMPRESSED_DESTINATION:
                return NEXT_HEADER_DESTINATION;
            case NEXT_HEADER_COMPRESSED_MOBILITY:
                return NEXT_HEADER_MOBILITY;
            default:
                return 255;
        }
    }

    /**
     * Checks if the given value represents a compressible extension header
     * @param unCompressed the uncompressed protocol number
     * @return True if the protocol number is compressible.
     */
    public static boolean isUnCompressedExtensionHeader(int unCompressed) {
        return (
                unCompressed == NEXT_HEADER_HOP_BY_HOP
                        || unCompressed == NEXT_HEADER_ROUTING
                        || unCompressed == NEXT_HEADER_DESTINATION
                        || unCompressed == NEXT_HEADER_MOBILITY
        );
    }
}
