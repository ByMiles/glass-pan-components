package de.htw.ai.loz.gpan.lpan.imp.transform;

import de.htw.ai.loz.gpan.lpan.header.IpV6Header;
import de.htw.ai.loz.gpan.lpan.header.IphcAddress;
import de.htw.ai.loz.gpan.lpan.header.IphcHeader;
import de.htw.ai.loz.gpan.lpan.msg.ComposedPacket;
import de.htw.ai.loz.gpan.lpan.msg.DataFrame;
import de.htw.ai.loz.gpan.lpan.msg.FragmentedPacket;
import de.htw.ai.loz.gpan.lpan.msg.PacketElement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.toHex;

public class LowPanToIpv6 {

    private ComposedPacket outgoing;

    private byte[] compressedFrame;
    private boolean isUpperLayerCompressed;
    private boolean isMulticast;
    private int nextHeader;

    private int framePointer;


    public ComposedPacket unFragmentedToComposed(DataFrame fragmentWrapper) throws Exception {
        System.out.println("COMPOSE UNFRAGMENTED");
        outgoing = new ComposedPacket();
        outgoing.setLinkHeader(fragmentWrapper.getLinkHeader());
        compressedFrame = fragmentWrapper.getDataBytes();
        return toComposed();
    }

    public ComposedPacket fragmentedToComposed(FragmentedPacket incoming) throws Exception {
        System.out.println("COMPOSE FRAGMENTED");
        outgoing = new ComposedPacket();
        outgoing.setDatagramTag(incoming.getDatagramTag());
        deFragmentFrames(incoming.getFragmentBytes());
        return toComposed();
    }

    private ComposedPacket toComposed() throws Exception {
        this.outgoing = new ComposedPacket();
        framePointer = 0;

        deCompressHeader();
        if (isUpperLayerCompressed) {
            deComposeCompressedExtensionHeaders();
            deComposeCompressedUpperLayer();
        } else {
            deComposeUnCompressedExtensionHeaders();
            deComposeUnCompressedUpperLayer();
       }
        outgoing.setResponse("SUCCESS");
        return outgoing;
    }

    private void deComposeUnCompressedExtensionHeaders() {

        PacketElement extensionHeader;
        List<PacketElement> elements = new LinkedList<>();
        while (IphcHeader.isUnCompressedExtensionHeader(nextHeader)) {
            extensionHeader = new PacketElement();
            extensionHeader.setProtocol(nextHeader);
            nextHeader = (int) compressedFrame[framePointer++];
            addPayloadToExtensionHeader(extensionHeader);
            elements.add(extensionHeader);
        }
        outgoing.setExtensionHeaders(elements.toArray(new PacketElement[0]));
    }

    private void deComposeUnCompressedUpperLayer() {
        PacketElement upperLayer = new PacketElement();
        upperLayer.setProtocol(nextHeader);
        upperLayer.setPayloadBytes(Arrays.copyOfRange(compressedFrame, framePointer, compressedFrame.length));
        System.out.println("OUTGOING: " + toHex(upperLayer.getPayload().getBytes()));
        outgoing.setPayload(upperLayer);
    }

    private void deComposeCompressedExtensionHeaders() {

        PacketElement extensionHeader;
        List<PacketElement> elements = new LinkedList<>();
        while (IphcHeader.isCompressedExtensionHeader(nextHeader)) {
            extensionHeader = new PacketElement();

            extensionHeader.setProtocol(
                    IphcHeader.deCompressExtensionProtocol(nextHeader));

            nextHeader = (int) compressedFrame[framePointer++];
            addPayloadToExtensionHeader(extensionHeader);
            elements.add(extensionHeader);
        }
        outgoing.setExtensionHeaders(elements.toArray(new PacketElement[0]));
    }

    private void deComposeCompressedUpperLayer() throws Exception {

        int compressedNextHeader = compressedFrame[framePointer++];
        if (((compressedNextHeader & 0xff) >> 3) != 0b11110)
            throw new Exception("INVALID Compression for upper layer protocol not supported");

        if ((compressedNextHeader & 0b0000_0100) == 0)
            throw new Exception("INVALID UDP without checksum is not supported");

        switch (compressedNextHeader & 0b0000_0011) {
            case IphcHeader.UDP_COMPRESSION_MODE_ONLY_LENGTH:
                decompressUdpOnlyLength();
                break;
            case IphcHeader.UDP_COMPRESSION_MODE_SMALL_DEST:
                deCompressUdpSmallDestination();
                break;
            case IphcHeader.UDP_COMPRESSION_MODE_SMALL_SOURCE:
                deCompressUdpSmallSource();
                break;
            case IphcHeader.UDP_COMPRESSION_MODE_BIG:
                deCompressUpdBigCompression();
                break;
        }

    }

    private void decompressUdpOnlyLength() throws IOException {

        int length = compressedFrame.length
                - framePointer // source(2) dest(2) checksum(2)
                - 6;

        PacketElement payload = new PacketElement();
        payload.setProtocol(IphcHeader.NEXT_HEADER_UDP);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(compressedFrame[0]); // sourcePort
        buffer.write(compressedFrame[1]); // sourcePort
        buffer.write(compressedFrame[2]); // sourcePort
        buffer.write(compressedFrame[3]); // sourcePort
        buffer.write((byte) (length >> 8));
        buffer.write((byte) length);
        buffer.write(Arrays.copyOfRange(compressedFrame, 6, compressedFrame.length));
        payload.setPayloadBytes(buffer.toByteArray());

        outgoing.setPayload(payload);
    }

    private void deCompressUdpSmallDestination() throws IOException {
        int length = compressedFrame.length
                - framePointer // source(2) dest(1) checksum(2)
                - 5;

        int destPort =
                (compressedFrame[framePointer + 2] & 0xff)
                        + IphcHeader.UDP_THRESHOLD_FOR_SMALL_COMPRESSION;


        PacketElement payload = new PacketElement();
        payload.setProtocol(IphcHeader.NEXT_HEADER_UDP);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(compressedFrame[0]); // sourcePort
        buffer.write(compressedFrame[1]); // sourcePort
        buffer.write((byte) (destPort >> 8));
        buffer.write((byte) destPort);
        buffer.write((byte) (length >> 8));
        buffer.write((byte) length);
        buffer.write(Arrays.copyOfRange(compressedFrame, 3, compressedFrame.length));
        
        payload.setPayloadBytes(buffer.toByteArray());

        outgoing.setPayload(payload);
    }

    private void deCompressUdpSmallSource() throws IOException {
        int length = compressedFrame.length
                - framePointer // source(1) dest(2) checksum(2)
                - 5;


        int sourcePort =
                (compressedFrame[framePointer] & 0xff)
                        + IphcHeader.UDP_THRESHOLD_FOR_SMALL_COMPRESSION;

        PacketElement payload = new PacketElement();
        payload.setProtocol(IphcHeader.NEXT_HEADER_UDP);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write((byte) (sourcePort >> 8));
        buffer.write((byte) sourcePort);
        buffer.write(compressedFrame[0]); // destPort
        buffer.write(compressedFrame[1]); // destPort
        buffer.write((byte) (length >> 8));
        buffer.write((byte) length);
        buffer.write(Arrays.copyOfRange(compressedFrame, 3, compressedFrame.length));

        outgoing.setPayload(payload);
    }

    private void deCompressUpdBigCompression() throws IOException {
        int length = compressedFrame.length
                - framePointer // source(0.5) dest(0.5) checksum(2)
                - 3;

        String lengthString = twoBytesToTwoChars(length);

        int sourcePort =
                (compressedFrame[framePointer] >> 4)
                        + IphcHeader.UDP_THRESHOLD_FOR_BIG_COMPRESSION;

        int destPort =
                (compressedFrame[framePointer] & 0b0000_1111)
                        + IphcHeader.UDP_THRESHOLD_FOR_BIG_COMPRESSION;

        PacketElement payload = new PacketElement();
        payload.setProtocol(IphcHeader.NEXT_HEADER_UDP);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write((byte) (sourcePort >> 8));
        buffer.write((byte) sourcePort);
        buffer.write((byte) (destPort >> 8));
        buffer.write((byte) destPort);
        buffer.write((byte) (length >> 8));
        buffer.write((byte) length);
        buffer.write(Arrays.copyOfRange(compressedFrame, 1, compressedFrame.length));
        payload.setPayloadBytes(buffer.toByteArray());

        outgoing.setPayload(payload);
    }

    private void addPayloadToExtensionHeader(PacketElement extensionHeader) {
        int headerEnd;
        headerEnd = framePointer + ((compressedFrame[framePointer] & 0xff) * 8 + 8);
        extensionHeader.setPayloadBytes(Arrays.copyOfRange(compressedFrame, framePointer, headerEnd));
        framePointer = headerEnd;
    }

    private void deCompressHeader() throws Exception {

        outgoing.setV6Header(new IpV6Header());

        byte[] iphcHeader = new byte[] {compressedFrame[framePointer++], compressedFrame[framePointer++]};

        if ((iphcHeader[0] >> 5) != 0b011)
            throw new Exception("INVALID header type is not supported");

        if ((iphcHeader[0] >> 3) != 0b01111)
            throw new Exception("INVALID flow and traffic are not supported");

        isUpperLayerCompressed = ((iphcHeader[0] >> 2) & 0b1) == 1;

        if ((iphcHeader[0] & 0b0000_0011) != IphcHeader.HOP_LIMIT_CARRIED_IN_LINE)
            throw new Exception("INVALID hop limit must be carried inline");

        if ((iphcHeader[1] & 0b1100_0100) > 0)
            throw new Exception("INVALID context is not supported");

        if (((iphcHeader[1] >> 4) & 0b11) != IphcHeader.ADDRESS_MODE_16)
            throw new Exception("INVALID source address mode not supported");

        isMulticast = ((iphcHeader[1] >> 3) & 0b1) > 0;

        if (isMulticast) {
            if ((iphcHeader[1] & 0b11) != IphcHeader.ADDRESS_MODE_8)
                throw new Exception("INVALID multicast address mode not supported");
        } else {
            if ((iphcHeader[1] & 0b11) != IphcHeader.ADDRESS_MODE_16)
                throw new Exception("INVALID destination address mode not supported");
        }

        if (isUpperLayerCompressed) {
            outgoing.getV6Header().setHopLimit((int) compressedFrame[framePointer++]);
            generateAddresses();
            nextHeader = compressedFrame[framePointer++];

        } else {
            nextHeader = compressedFrame[framePointer++];
            outgoing.getV6Header().setHopLimit((int) compressedFrame[framePointer++]);
            generateAddresses();
        }
    }

    private void generateAddresses() {
       outgoing.getV6Header().setSourceAddress(
                IphcAddress.createLinkLocalUnicastAddress(
                        (byte) compressedFrame[framePointer++],
                        (byte) compressedFrame[framePointer++]
                ));

        outgoing.getV6Header().setDestAddress(
                isMulticast
                        ? IphcAddress.createLinkLocalMulticastAddress(
                        (byte) compressedFrame[framePointer++])
                        : IphcAddress.createLinkLocalUnicastAddress(
                        (byte) compressedFrame[framePointer++],
                        (byte) compressedFrame[framePointer++]
                ));
    }

    private void deFragmentFrames(byte[][] fragments) throws IOException {
        ByteArrayOutputStream unFragmentedBuilder = new ByteArrayOutputStream();
                unFragmentedBuilder.write(Arrays.copyOfRange(fragments[0], 4, fragments[0].length));
        System.out.println(" defragmenting: " + unFragmentedBuilder.toString());
        for (int i = 1; i < fragments.length; i++) {
            unFragmentedBuilder.write(Arrays.copyOfRange(fragments[i], 5, fragments[i].length));
            System.out.println(" defragmenting: " + unFragmentedBuilder.toString());
        }

        compressedFrame = unFragmentedBuilder.toByteArray();
    }

    private String twoBytesToTwoChars(int bytesAsInt) {

        return new String(
                new byte[]{
                        (byte) (bytesAsInt >> 8),
                        (byte) bytesAsInt
                });
    }
}
