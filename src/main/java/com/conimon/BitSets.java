package com.conimon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class provides a representation of BitSets. It contains a BitSet but enhanced it with an length parameter.
 * This allows for bit truncation which is otherwise not possible.
 * The truncation algorithm is designed according to the paper "Lossless Compression of High-volume Numerical Data from Simulations"
 * https://www.researchgate.net/publication/2389424_Lossless_Compression_of_High-volume_Numerical_Data_from_Simulations
 * @author herta
 */
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
        this.length = bits.size();
    }

    public static BitSets fromLong(long value) {
        if(value == 0) {
          return new BitSets(new BitSet());
        }
        return new BitSets(BitSet.valueOf(new long[]{value}));
    }

    public static BitSets fromByteArray(byte[] bytes) {
        return new BitSets(BitSet.valueOf(bytes));
    }

    // TODO
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
        return toString(bs, bs.size());
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
            bitLength = bs.previousSetBit(bs.size()-1)+2;
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
        // System.out.println("original:  " + BitSets.toString(bs)+ "   bitLength: " + bitLength + "    in bits " + BitSets.toString(lengthBitSet));
        // System.out.println("truncated: " + BitSets.toString(truncated));
        // System.out.println("BitSets: " + bitSets.toString() + " total Length: " + bitSets.length);
        // System.out.println("-------------------------------------------");
        return bitSets;
    }

    public static BitSet concatenate(List<BitSets> bitSetsList) {
        int totalLength = bitSetsList.stream().mapToInt(BitSets::getLength).sum();
        BitSet bs = new BitSet(totalLength);
        int bitSetSize = bs.size();
        int bitOffset = 0;
        for (BitSets bitSets:bitSetsList) {
            bitOffset += bitSets.length;
            int fromIndex = bs.size() - 1;
            while(true) {
                int setBitIndex = bitSets.getBits().previousSetBit(fromIndex);
                if(setBitIndex <0) {
                    break;
                } else {
                    bs.set(bitSetSize - bitOffset + setBitIndex);
                    fromIndex = setBitIndex-1;
                }
            }
        }
        System.out.println("totalLength: " + totalLength);
        System.out.println("bitset size: " + bitSetSize);
        return bs;
    }
}
