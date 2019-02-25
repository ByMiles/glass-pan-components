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
package de.htw.ai.loz.gpan.conf;



import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Loads a set of parameters from properties.
 * <p>
 *     The "Demo.properties" file needs to be exposed in a "config" folder at the root.<br>
 *     If no file was found default parameters will be loaded.
 * </p>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public class DemoConfiguration {

    private static final int[] defaultExtAddr = new int[8];
    private static final int defaultShortAddr = 0;
    private static final int defaultPanId = 0;
    private static final int defaultChannel = 11;
    private static final int defaultThreadLimit = 3;
    private static final int defaultTimeOutMillis = 3000;
    private static final int defaultMaxOutFrames = 3;
    private static final int[] defaultPorts = new int[]{8887, 8889};
    private static final int defaultBaudRate = 38400;

    private int[] extAddr;
    private int panId;
    private int shortAddr;
    private int logicalChannel;
    private int[] ports;
    private int baudrate;
    private int threadLimit;
    private int timeOutMillis;
    private int maxOutFrames;

    public DemoConfiguration() {
        String filePath = "config/Demo.properties";
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(filePath)));
            loadFromProps(properties);
        } catch (Exception e) {
            System.out.println("Properties not loaded");
            e.printStackTrace();
            loadFromDefault();
        }
    }

    private void loadFromDefault() {

        extAddr = defaultExtAddr;
        panId = defaultPanId;
        logicalChannel = defaultChannel;
        ports = defaultPorts;
        baudrate = defaultBaudRate;
        threadLimit = defaultThreadLimit;
        timeOutMillis = defaultTimeOutMillis;
        maxOutFrames = defaultMaxOutFrames;
    }

    private void loadFromProps(Properties props) {
        try {
            this.baudrate = Integer.parseInt(props.getProperty("baudrate"));
        } catch (Exception e) {
            System.out.println("baudrate not loaded");
            this.baudrate = defaultBaudRate;
            e.printStackTrace();
        }
        try {
            this.extAddr = fromHexString(props.getProperty("extAddr"), 8);
        } catch (Exception e) {
            System.out.println("extended address not loaded");
            this.extAddr = defaultExtAddr;
            e.printStackTrace();
        }
        try {
            this.panId = summate(fromHexString(props.getProperty("panId"), 2));
        } catch (Exception e) {
            System.out.println("pan id not loaded");
            this.panId = defaultPanId;
            e.printStackTrace();
        }
        try {
            this.shortAddr = summate(fromHexString(props.getProperty("shortAddr"), 2));
        } catch (Exception e) {
            System.out.println("short addr not loaded");
            this.shortAddr = defaultShortAddr;
            e.printStackTrace();
        }
        try {
            this.logicalChannel = Integer.parseInt(props.getProperty("logicalChannel"));
        } catch (Exception e) {
            System.out.println("logical channel not loaded");
            this.logicalChannel = defaultChannel;
            e.printStackTrace();
        }
        try {
            String[] portStrings = props.getProperty("ports").split(" ");

            this.ports = new int[portStrings.length];
            for (int i = 0; i < portStrings.length; i++) {
                ports[i] = Integer.parseInt(portStrings[i]);
            }
        } catch (Exception e) {
            System.out.println("ports not loaded");
            this.ports = defaultPorts;
            e.printStackTrace();
        }
    }

    private int summate(int[] intsAsBytes) {
        int toShift = intsAsBytes.length - 1;
        int sum = 0;
        for (int intAsByte : intsAsBytes) {
            sum += (intAsByte << (toShift-- * 8));
        }
        return sum;
    }

    public int[] getExtAddr() {
        return extAddr;
    }

    public int getPanId() {
        return panId;
    }

    public int getLogicalChannel() {
        return logicalChannel;
    }

    public int getBaudRate() {
        return baudrate;
    }

    public int[] getPossiblePorts() {
        return ports;
    }

    public int getThreadLimit() {
        return threadLimit;
    }

    public int getTimeoutMillis() {
        return timeOutMillis;
    }

    public int getMaxOutFrames() {
        return maxOutFrames;
    }

    public int getShortAddr() {
        return shortAddr;
    }

    public int[] fromHexString(String hexString, int length) throws Exception {
        if (hexString.substring(0, 1).equals(" "))
            hexString = hexString.substring(1);
        String[] hexes = hexString.split(" ");

        if (hexes.length != length) throw new Exception();

        int[] ints = new int[length];
        for (int i = 0; i < length; i++)
            ints[i] = Integer.parseInt(hexes[i], 16);
        return ints;
    }
}
