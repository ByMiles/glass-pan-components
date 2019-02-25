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
package de.htw.ai.loz.gpan.lpan.imp.transform;

import de.htw.ai.loz.gpan.lpan.header.IphcAddress;
import de.htw.ai.loz.gpan.lpan.header.IphcHeader;
import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;
import de.htw.ai.loz.gpan.lpan.msg.PacketElement;
import inet.ipaddr.AddressStringException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Helper class for {@code PacketComposer}
 * @author Miles Lorenz
 * @version 1.0
 */
public class Ipv6toLowPan {


    private ComposedPacket incoming;
    private FragmentedPacket outgoing;

    private IphcAddress sourceAddress;
    private IphcAddress destAddress;
    private ByteArrayOutputStream frameBuffer;

    private int maxFrameSize;


    private byte[] iphcHeader;
    private byte[] sourceAddressCompressed;
    private byte[] destAddressCompressed;

    private boolean isMulticast;
    private boolean isUpperLayerCompressed;
    private boolean hasExtensionHeader;

    public Ipv6toLowPan() {
        this.frameBuffer = new ByteArrayOutputStream();
    }

    public FragmentedPacket toCompressedFragmented(ComposedPacket incoming, int maxFrameSize) throws Exception {

        this.incoming = incoming;
        this.maxFrameSize = maxFrameSize;

        validatePayload();
        checkExtensionHeaders();
        validateSourceAddress();
        validateDestAddress();
        compressAddresses();
       // tryCompressElements();
        generateIphcHeader();
        composeUnFragmentedFrame();

        fragmentUnFragmentedFrame();

        outgoing.setLinkHeader(incoming.getLinkHeader());
        outgoing.setResponse("SUCCESS");
        return outgoing;
    }

    private void fragmentUnFragmentedFrame() {
        outgoing = new FragmentedPacket();
        byte[] unfragmented = frameBuffer.toByteArray();
        if (unfragmented.length < maxFrameSize) {
            outgoing.setFragmentBytes(new byte[][]{unfragmented});
        } else {
            outgoing.setFragmentBytes(
                    new PacketFragmenter(
                            unfragmented,
                            maxFrameSize,
                            incoming.getDatagramTag())
                            .getFrames()
            );
        }
    }

    private void checkExtensionHeaders() {

        hasExtensionHeader = incoming.getExtensionHeaders() != null
                && incoming.getExtensionHeaders().length > 0;
    }

    private void composeUnFragmentedFrame() throws IOException {

        if (!hasExtensionHeader && !isUpperLayerCompressed)
            composeUncompressedWithoutExtensionHeaders();
        else if (hasExtensionHeader && !isUpperLayerCompressed)
            composeUncompressedWithExtensionHeaders();
        else if (hasExtensionHeader)
            composeCompressedWithExtensionHeaders();
        else
            composeCompressedWithoutExtensionHeaders();
    }

    private void composeUncompressedWithoutExtensionHeaders() throws IOException {

        frameBuffer.write(iphcHeader);
        frameBuffer.write((byte) incoming.getPayload().getProtocol());
        frameBuffer.write((byte) incoming.getV6Header().getHopLimit());
        frameBuffer.write(sourceAddressCompressed);
        frameBuffer.write(destAddressCompressed);
        frameBuffer.write(incoming.getPayload().getPayloadBytes());
    }

    private void composeUncompressedWithExtensionHeaders() throws IOException {
        frameBuffer.write(iphcHeader);

        frameBuffer.write((byte) incoming.getExtensionHeaders()[0].getProtocol());
        frameBuffer.write((byte) incoming.getV6Header().getHopLimit());
        frameBuffer.write(sourceAddressCompressed);
        frameBuffer.write(destAddressCompressed);

        appendExtensionHeaders(incoming.getPayload().getProtocol());
        frameBuffer.write(incoming.getPayload().getPayloadBytes());
    }

    private void composeCompressedWithExtensionHeaders() throws IOException {
        frameBuffer.write(iphcHeader);
        frameBuffer.write(((byte) incoming.getV6Header().getHopLimit()));
        frameBuffer.write((sourceAddressCompressed));
        frameBuffer.write((destAddressCompressed));
        frameBuffer.write(((byte) incoming.getExtensionHeaders()[0].getProtocol()));

        appendExtensionHeaders(incoming.getPayload().getProtocol());
        frameBuffer.write((incoming.getPayload().getPayloadBytes()));
    }

    private void composeCompressedWithoutExtensionHeaders() throws IOException {
        frameBuffer.write(iphcHeader);
        frameBuffer.write((byte) incoming.getV6Header().getHopLimit());
        frameBuffer.write(sourceAddressCompressed);
        frameBuffer.write(destAddressCompressed);
        frameBuffer.write((byte) incoming.getPayload().getProtocol());
        frameBuffer.write(incoming.getPayload().getPayloadBytes());
    }

    private void appendExtensionHeaders(int upperLayerProtocol) throws IOException {

        for (int i = 0; i < incoming.getExtensionHeaders().length - 1; i++) {
            frameBuffer.write((byte) incoming.getExtensionHeaders()[i + 1].getProtocol());
            frameBuffer.write(incoming.getExtensionHeaders()[i].getPayloadBytes());
        }
        frameBuffer.write((byte) upperLayerProtocol);
        frameBuffer.write(incoming.getExtensionHeaders()[incoming.getExtensionHeaders().length].getPayloadBytes());
    }

    private void validateSourceAddress() throws Exception {

        try {
            sourceAddress = new IphcAddress(incoming.getV6Header().getSourceAddress());
        } catch (AddressStringException e) {
            throw new Exception("INVALID source-address");
        }

        if (!sourceAddress.isUnicastAutoConfigured())
            throw new Exception("INVALID source-address only link-local fe80::ff:fe00:XXXX supported");
    }

    private void validateDestAddress() throws Exception {

        try {
            destAddress = new IphcAddress(incoming.getV6Header().getDestAddress());
        } catch (AddressStringException e) {
            throw new Exception("INVALID destination-address");
        }

        isMulticast = destAddress.isMulticast_8_compressable();
        if (!(destAddress.isUnicastAutoConfigured() || isMulticast))
            throw new Exception("INVALID destination-address only link-local fe80::ff:fe00:XXXX and" +
                    "multicast with ff02::XX are supported");
    }

    private void compressAddresses() {
        sourceAddressCompressed = sourceAddress.getUnicastAutoConfiguredSignificant();

        destAddressCompressed = isMulticast
                ? destAddress.getMulticast_8_ConfiguredSignificant()
                : destAddress.getUnicastAutoConfiguredSignificant();
    }

    private void validatePayload() throws Exception {

        if (incoming.getPayload() == null || incoming.getPayload().getPayload().length() < 8)
            throw new Exception("INVALID no valid payload");

        if (incoming.getPayload().getPayload().length() > 1280) {
            throw new Exception("INVALID payload to long");
        }
    }

    private void tryCompressElements() throws Exception {
        isUpperLayerCompressed = compressUpperLayer();
        if (isUpperLayerCompressed && hasExtensionHeader) {
            if (!compressExtensionHeaders()) {
                throw new Exception("DENIED extension header not supported");
            }
        }
    }

    private boolean compressExtensionHeaders() {

        for (PacketElement extensionHeader : incoming.getExtensionHeaders()) {
            if (!compressExtensionHeader(extensionHeader))
                return false;
        }
        return true;
    }

    private boolean compressExtensionHeader(PacketElement element) {

        switch (element.getProtocol()) {
            case IphcHeader.NEXT_HEADER_HOP_BY_HOP:
                element.setProtocol(IphcHeader.NEXT_HEADER_COMPRESSED_HOP_BY_HOP);
                return true;
            case IphcHeader.NEXT_HEADER_ROUTING:
                element.setProtocol(IphcHeader.NEXT_HEADER_COMPRESSED_ROUTING);
                return true;
            case IphcHeader.NEXT_HEADER_FRAGMENT:
                element.setProtocol(IphcHeader.NEXT_HEADER_COMPRESSED_FRAGMENT);
                return true;
            case IphcHeader.NEXT_HEADER_DESTINATION:
                element.setProtocol(IphcHeader.NEXT_HEADER_COMPRESSED_DESTINATION);
                return true;
            case IphcHeader.NEXT_HEADER_MOBILITY:
                element.setProtocol(IphcHeader.NEXT_HEADER_COMPRESSED_MOBILITY);
                return true;
            default:
                return false;
        }
    }

    private boolean compressUpperLayer() throws IOException {

        switch (incoming.getPayload().getProtocol()) {
            case IphcHeader.NEXT_HEADER_UDP:
                compressUdp(incoming.getPayload());
                return true;
            case IphcHeader.NEXT_HEADER_ICMP:
                return false;
            default:
                return false;
        }
    }

    private void compressUdp(PacketElement element) throws IOException {

        // UDP header format: sourcePort destPort length checksum
        // GOAL 1: reduce portSize by subtracting default-values
        // GOAL 2: cut the length-field

        int sourcePort = (element.getPayloadBytes()[0] << 8)
                + element.getPayloadBytes()[1];

        int destPort = (element.getPayloadBytes()[2] << 8)
                + element.getPayloadBytes()[3];

        // => the default mode if no compression is possible
        //    cuts the length but all ports are left uncompressed
        // => saved 2 bytes
        int portCompressionMode = IphcHeader.UDP_COMPRESSION_MODE_ONLY_LENGTH;

        if (sourcePort > IphcHeader.UDP_THRESHOLD_FOR_BIG_COMPRESSION
                && destPort > IphcHeader.UDP_THRESHOLD_FOR_BIG_COMPRESSION) {
            // => both ports are compressible to 4 bits each
            // => saved 5 bytes (2 + 3)
            portCompressionMode = IphcHeader.UDP_COMPRESSION_MODE_BIG;
            sourcePort -= IphcHeader.UDP_THRESHOLD_FOR_BIG_COMPRESSION;
            destPort -= IphcHeader.UDP_THRESHOLD_FOR_BIG_COMPRESSION;

        } else if (sourcePort > IphcHeader.UDP_THRESHOLD_FOR_SMALL_COMPRESSION) {
            // => source port is compressible to 1 byte
            // => saved 3 bytes (2 + 1)
            portCompressionMode = IphcHeader.UDP_COMPRESSION_MODE_SMALL_SOURCE;
            sourcePort -= IphcHeader.UDP_THRESHOLD_FOR_SMALL_COMPRESSION;

        } else if (destPort > IphcHeader.UDP_THRESHOLD_FOR_SMALL_COMPRESSION) {
            // => dest port is compressible to 1 byte
            // => saved 3 bytes (2 + 1)
            portCompressionMode = IphcHeader.UDP_COMPRESSION_MODE_SMALL_DEST;
            destPort -= IphcHeader.UDP_THRESHOLD_FOR_SMALL_COMPRESSION;
        }

        // eliding the checksum is not supported
        element.setProtocol(IphcHeader.UDP_COMPRESSED_CHECKSUM_INLINE + portCompressionMode);

        byte[] upperPayload = Arrays.copyOfRange(element.getPayloadBytes(), 6, element.getPayloadBytes().length);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        switch (portCompressionMode) {
            case IphcHeader.UDP_COMPRESSION_MODE_SMALL_DEST:
                buffer.write((byte) (sourcePort >> 8));
                buffer.write((byte) sourcePort);
                buffer.write((byte) destPort);
                break;
            case IphcHeader.UDP_COMPRESSION_MODE_SMALL_SOURCE:
                buffer.write((byte) sourcePort);
                buffer.write((byte) (destPort >> 8));
                buffer.write((byte) destPort);
                break;
            case IphcHeader.UDP_COMPRESSION_MODE_BIG:
                buffer.write((byte) ((sourcePort << 4) + destPort));
                break;
            default:
                buffer.write(Arrays.copyOfRange(element.getPayloadBytes(), 0, 4));
        }
        buffer.write(upperPayload);
        element.setPayloadBytes(buffer.toByteArray());
    }

    private void generateIphcHeader() {
        iphcHeader = IphcHeader.generateCompressedHeader(
                isUpperLayerCompressed,
                IphcHeader.HOP_LIMIT_CARRIED_IN_LINE,
                IphcHeader.CONTEXT_NOT_SUPPORTED,
                IphcHeader.CONTEXT_NOT_SUPPORTED,
                IphcHeader.ADDRESS_MODE_16,
                isMulticast,
                IphcHeader.CONTEXT_NOT_SUPPORTED,
                isMulticast
                        ? IphcHeader.ADDRESS_MODE_8
                        : IphcHeader.ADDRESS_MODE_16
        );
    }
}
