package de.htw.ai.loz.gpan.lpan.header;

import com.fasterxml.jackson.databind.ObjectMapper;
import inet.ipaddr.AddressStringException;

import java.io.IOException;

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

    public static void bitByBit(byte[] bytes) {
        for (byte b : bytes) {
            for (int i = 7; i > -1; i--) {
                System.out.print(((b >> i) & 1) + " | ");
            }
            System.out.println();
        }
        System.out.println(isNextHeaderCompressed(bytes, 0));
        System.out.println(hopLimit(bytes, 0));
        System.out.println(isContextIdentifierExtensionUsed(bytes, 0));
        System.out.println(isSourceAddressCompressionStateful(bytes, 0));
        System.out.println(sourceAddressMode(bytes, 0));
        System.out.println(isMulticast(bytes, 0));
        System.out.println(isDestAddressCompressionStateful(bytes, 0));
        System.out.println(destAddressMode(bytes, 0));
    }

    public static boolean isNextHeaderCompressed(byte[] payload, int headerPosition) {

        return (payload[headerPosition] & 0b0000_0100) > 0;
    }

    public static int hopLimit(byte[] payload, int headerPosition) {

        return (payload[headerPosition] & 0b0000_0011);
    }

    public static boolean isContextIdentifierExtensionUsed(byte[] payload, int headerPosition) {

        return (payload[headerPosition + 1] & 0b1000_0000) > 0;
    }

    public static boolean isSourceAddressCompressionStateful(byte[] payload, int headerPosition) {

        return (payload[headerPosition + 1] & 0b0100_0000) > 0;
    }

    public static int sourceAddressMode(byte[] payload, int headerPosition) {

        return ((payload[headerPosition + 1] & 0b0011_0000) >> 4);
    }

    public static boolean isMulticast(byte[] payload, int headerPosition) {

        return (payload[headerPosition + 1] & 0b0000_1000) > 0;
    }

    public static boolean isDestAddressCompressionStateful(byte[] payload, int headerPosition) {

        return (payload[headerPosition + 1] & 0b0000_0100) > 0;
    }

    public static int destAddressMode(byte[] payload, int headerPosition) {

        return (payload[headerPosition + 1] & 0b0000_0011);
    }


    public static boolean isCompressedExtensionHeader(int compressed) {
        return (
                compressed == NEXT_HEADER_COMPRESSED_HOP_BY_HOP
                        || compressed == NEXT_HEADER_COMPRESSED_ROUTING
                        || compressed == NEXT_HEADER_COMPRESSED_DESTINATION
                        || compressed == NEXT_HEADER_COMPRESSED_MOBILITY
        );
    }

    public static boolean isCompressedUpperLayer(int compressed) {
        return ((compressed & 0b1111_0000) == NEXT_HEADER_COMPRESSED_UDP);
    }

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

    public static boolean isUnCompressedExtensionHeader(int unCompressed) {
        return (
                unCompressed == NEXT_HEADER_HOP_BY_HOP
                        || unCompressed == NEXT_HEADER_ROUTING
                        || unCompressed == NEXT_HEADER_DESTINATION
                        || unCompressed == NEXT_HEADER_MOBILITY
        );
    }
}
