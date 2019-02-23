package de.htw.ai.loz.gpan.lpan.msg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.htw.ai.loz.gpan.lpan.header.LinkHeader;

import java.util.Base64;

public class FragmentedPacket {

    private LinkHeader linkHeader;
    private int datagramSize;
    private int datagramTag;
    private byte[][] fragments;
    private String response;

    public FragmentedPacket() {
    }


    public void setFragments(String[] fragmentStrings) {

        if (fragmentStrings != null) {
            fragments = new byte[fragmentStrings.length][];
            for (int i = 0; i < fragmentStrings.length; i++) {
                fragments[i] = Base64.getDecoder().decode(fragmentStrings[i]);
            }
        }
    }

    @JsonIgnore
    public void setFragmentBytes(byte[][] fragments) {

       this.fragments = fragments;
    }


    @JsonIgnore
    public byte[][] getFragmentBytes() {

        return this.fragments;
    }

    public String[] getFragments() {

        if (fragments == null)
            return null;

        String[] asStrings = new String[fragments.length];
        for (int i = 0; i < fragments.length; i++) {
            asStrings[i] = Base64.getEncoder().encodeToString(fragments[i]);
        }

        return asStrings;
    }

    public int getDatagramSize() {
        return datagramSize;
    }

    public int getDatagramTag() {
        return datagramTag;
    }

    public void setDatagramSize(int datagramSize) {
        this.datagramSize = datagramSize;
    }

    public void setDatagramTag(int datagramTag) {
        this.datagramTag = datagramTag;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LinkHeader getLinkHeader() {
        return linkHeader;
    }

    public void setLinkHeader(LinkHeader linkHeader) {
        this.linkHeader = linkHeader;
    }
}
