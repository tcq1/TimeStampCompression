package com.conimon;

import com.google.common.primitives.Longs;

public class Byte {
    public static byte[] toByte(long value) {
        return Longs.toByteArray(value);
    }

    public static byte[] truncate(byte[] bytes) {
        int i = 0;
        while (bytes[i] == 0 && i < 7) {
            i++;
        }
        byte[] truncated = new byte[1];
        System.arraycopy(bytes, i, truncated, 0, bytes.length - i);
        return truncated;
    }
}
