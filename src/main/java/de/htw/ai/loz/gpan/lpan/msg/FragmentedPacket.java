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
