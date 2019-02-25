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

/**
 * Contains static methods to detect and process fragmentation-header
 * ( @link https://tools.ietf.org/html/rfc4944#section-5.3 ).
 * <p>
 *       Helper methods for <br>
 *       <ul>
 *           <li>dispatch</li>
 *           <li>datagram tag</li>
 *           <li>offset</li>
 *       </ul>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public class FragHeader {


    /**
     * Checks the first byte of the given data unit
     * for first and following fragmentation header.
     * @param fragment A possibly fragmented data unit.
     * @return True if this data unit fragmented.
     */
    public static boolean isFragmented(byte[] fragment) {
        return isFirstFragment(fragment)
                || isFollowingFragment(fragment);
    }

    /**
     * Compares the first byte of the given data unit with the dispatch
     * for the first fragment.
     * @param fragment A possibly fragmented data unit.
     * @return True if this data unit is a first fragment.
     */
    public static boolean isFirstFragment(byte[] fragment) {
        return ((fragment[0] & 0xff) >> 3) == 0b11000;
    }

    /**
     * Compares the first byte of the given data unit with the dispatch
     * for a following fragment.
     * @param fragment A possibly fragmented data unit.
     * @return True if this data unit is a following fragment.
     */
    public static boolean isFollowingFragment(byte[] fragment) {
        return ((fragment[0] & 0xff) >> 3) == 0b11100;
    }

    /**
     * Extracts the datagram tag of a fragmented data unit.
     * @param fragment A fragmented data unit.
     * @return The datagram tag as composite of the third and fourth byte.
     */
    public static int getDatagramTag(byte[] fragment) {
        return ((fragment[2] & 0xff) << 8) + (fragment[3] & 0xff);
    }

    /**
     * Extracts the datagram size from the fragmented data unit.
     * @param fragment A fragmented data unit.
     * @return The datagram size as composite of the remainder from the first byte
     * and the second
     */
    public static int getDatagramSize(byte[] fragment) {
        return ((fragment[0] & 0b0000_0111) << 8) + ((fragment[1] & 0xff));
    }

    /**
     * Extractes the offset from a following fragment.
     * @param fragment A fragmented data unit.
     * @return the offset of fragment as product of the fifth byte and 8.
     */
    public static int getOffset(byte[] fragment) {
        return fragment[4] * 8;
    }
}
