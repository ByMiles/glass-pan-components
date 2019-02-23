package de.htw.ai.loz.gpan.lpan.header;

import static de.htw.ai.loz.gpan.mac.macCop.defs.MacCoPStatic.toHex;

public class FragHeader {


    public static boolean isFragmented(byte[] fragment) {
        return isFirstFragment(fragment)
                || isFollowingFragment(fragment);
    }


    public static boolean isFirstFragment(byte[] fragment) {
        return ((fragment[0] & 0xff) >> 3) == 0b11000;
    }

    public static boolean isFollowingFragment(byte[] fragment) {
        return ((fragment[0] & 0xff) >> 3) == 0b11100;
    }

    public static int getDatagramTag(byte[] fragment) {
        return ((fragment[2] & 0xff) << 8) + (fragment[3] & 0xff);
    }

    public static int getDatagramSize(byte[] fragment) {
        return ((fragment[0] & 0b0000_0111) << 8) + ((fragment[1] & 0xff));
    }

    public static int getOffset(byte[] fragment) {
        return fragment[4] * 8;
    }
}
