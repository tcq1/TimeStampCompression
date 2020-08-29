package com.conimon;

import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

@Data
@AllArgsConstructor
public class BitSets {
    public static int BYTES_OF_LONG = 8;
    public static int BITS_OF_BYTE = 8;
    // number of bits required to encode the length
    public static int BITS_OF_LENGTH = 6;

    @NonNull private BitSet bits;
    public int length;

    public BitSets(@NonNull BitSet bits) {
        this.bits = bits;
        this.length = bits.length();
    }

    public static BitSets fromLong(long value) {
        return new BitSets(BitSet.valueOf(new long[]{value}));
    }

    public static BitSets fromByteArray(byte[] bytes) {
        return new BitSets(BitSet.valueOf(bytes));
    }

    //TODO
    public Long toLong() {
        return null;
    }

    public static Long toLong(BitSet bs) {
        return bs.toLongArray()[0];
    }

    public static byte[] toByteArray(BitSet bs) {
        return bs.toByteArray();
    }

    public String toString() {
        return toString(bits, length);
    }

    public static String toString(BitSet bs) {
        return toString(bs, bs.length());
    }

    private static String toString(BitSet bs, int length) {
        final StringBuilder buffer = new StringBuilder(64);
        IntStream.range(0, length).mapToObj(i -> bs.get(i) ? '1' : '0').forEach(i -> buffer.insert(0, i));
        return buffer.toString();
    }

    public BitSets truncate() {
        return truncate(bits);
    }

    public static BitSets truncate(BitSet bs) {
        int bitLength;
        // negative
        if(bs.get(BYTES_OF_LONG*BITS_OF_BYTE-1)) {
            // +1 for getting the index of "1" before first "0"
            bitLength = bs.previousClearBit(bs.size()-1)+2;
        } // positive
        else {
            bitLength = bs.length()+1;
        }
        BitSet truncated = bs.get(0, bitLength);
        BitSet lengthBitSet = BitSet.valueOf(new long[]{bitLength});
        for(int i = 0; i<lengthBitSet.length(); i++) {
            if(lengthBitSet.get(i)) {
                truncated.set(bitLength + i);
            }
        }
        int length = bitLength + BITS_OF_LENGTH;
        BitSets bitSets = new BitSets(truncated, length);
        System.out.println("original: " + BitSets.toString(bs)+ "   bitLength: " + bitLength + "    in bits " + BitSets.toString(lengthBitSet));
        System.out.println("truncated: " + BitSets.toString(truncated));
        System.out.println("BitSets: " + bitSets.toString() + " total Length: " + length);
        System.out.println("-------------------------------------------");
        return bitSets;
    }
}
