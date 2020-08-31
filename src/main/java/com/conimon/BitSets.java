package com.conimon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class provides a representation of a sequence of bits. It contains a BitSet but enhances it with a length parameter.
 * If not specified the length will be set to the size of the contained BitSet which is 64.
 * The setup of this class allows for bit truncation that is otherwise not possible with the given BitSet class.
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

    /**
     * Generates BitSets from a given long
     * @param value given long values
     * @return BitSets of this value
     */
    public static BitSets fromLong(long value) {
        if(value == 0) {
          return new BitSets(new BitSet());
        }
        return new BitSets(BitSet.valueOf(new long[]{value}));
    }

    /**
     * Wrapper for truncate method. Truncates BitSets instance by removing leading 0 or 1.
     * For details of the algorithm please refer to the paper in the {@link BitSets})
     * @return truncated BitSets
     */
    public BitSets truncate() {
        return truncate(bits);
    }

    /**
     * Truncates given BitSet by removing leading 0 or 1 and creates a new BitSets of it.
     * For details of the algorithm please refer to the paper in the {@link BitSets})
     * @param bs given BitSet
     * @return truncated BitSets
     */
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
        //System.out.println("long: " + BitSets.toLong(bs));
        //System.out.println("original:  " + BitSets.toString(bs)+ "   bitLength: " + bitLength + "    in bits " + BitSets.toString(lengthBitSet));
        //System.out.println("truncated: " + BitSets.toString(truncated));
        //System.out.println("BitSets: " + bitSets.toString() + " total Length: " + bitSets.length);
        //System.out.println("-------------------------------------------");

        return bitSets;
    }

    /**
     * Concatenates a List of BitSets into one BitSet
     * @param bitSetsList given List of BitSets
     * @return concatenated BitSet
     */
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
                if(setBitIndex < 0) {
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

    /**
     * Reverse function of concatenate()
     * @param bs: Concatenated BitSet
     * @param differenceDegree: Chosen DifferenceDegree
     * @return List of BitSets of all long values
     */
    public static List<BitSets> deconcatenate(BitSet bs, int differenceDegree) {
        // TODO: write tests

        List<BitSets> bitSets = new ArrayList<>();

        // get first values
        bitSets = deconcatenateFirstValues(bs, differenceDegree);

        // get truncated values
        bitSets = deconcatenateTruncatedValues(bitSets, bs, differenceDegree);

        return bitSets;
    }

    /**
     * Returns a list with the BitSets of the first #differenceDegree values.
     * Those values are not concatenated and 64 Bit long
     * @param bs: Complete BitSet
     * @param differenceDegree: Chosen differenceDegree
     * @return List of BitSets of first long values
     */
    public static List<BitSets> deconcatenateFirstValues(BitSet bs, int differenceDegree) {
        List<BitSets> values = new ArrayList<>();
        int currentIndex = bs.size() - 1;

        for (int i = 0; i < differenceDegree; i++) {
            BitSet value = calculateDeconcatenatedValue(bs, currentIndex, BYTES_OF_LONG * BITS_OF_BYTE);
            values.add(new BitSets(value));
            currentIndex -= BYTES_OF_LONG * BITS_OF_BYTE;
        }

        return values;
    }

    /**
     * Calculates BitSets of truncated values.
     * Truncated values consist of 6 Bit size information and the values.
     * @param values: List of concatenated BitSets
     * @param bs: Complete BitSet
     * @param differenceDegree: Chosen differenceDegree
     * @return List of deconcatenated BitSets of truncated values
     */
    public static List<BitSets> deconcatenateTruncatedValues(List<BitSets> values, BitSet bs, int differenceDegree) {
        // determine currentIndex
        int currentIndex = bs.size() - 1 - differenceDegree * BYTES_OF_LONG * BITS_OF_BYTE;

        while (bs.previousSetBit(currentIndex) != -1) {
            // read size information
            BitSet nextSizeBs = calculateDeconcatenatedValue(bs, currentIndex, BITS_OF_LENGTH);
            int nextSize = toLong(nextSizeBs).intValue();
            currentIndex -= BITS_OF_LENGTH;

            // calculate BitSet and add to list
            BitSet nextValue = calculateDeconcatenatedValue(bs, currentIndex, nextSize);
            values.add(new BitSets(nextValue));
            currentIndex -= nextSize;
        }

        return values;
    }

    /**
     * Calculates next BitSet
     * @param bs: Complete concatenated BitSet
     * @param currentIndex: Current position in BitSet
     * @param size: Size of next value
     * @return BitSet of a long
     */
    public static BitSet calculateDeconcatenatedValue(BitSet bs, int currentIndex, int size) {
        int lowerBound = currentIndex - size;
        BitSet value = new BitSet(size);

        int setBitIndex = bs.previousSetBit(currentIndex);
        boolean negative = bs.get(currentIndex);

        while (setBitIndex > lowerBound) {
            value.set(setBitIndex - lowerBound - 1);
            setBitIndex = bs.previousSetBit(setBitIndex - 1);
        }

        if (negative) { value = makeNegative(value); }

        return value;
    }

    /**
     * Changes all leading Bits of bs to 1 to get negative long value
     * @param bs: BitSet of a long
     * @return BitSet of a long with leading 1
     */
    public static BitSet makeNegative(BitSet bs) {
         int setBitIndex = bs.size() - 1;
         for (int i = setBitIndex; i > bs.previousSetBit(setBitIndex--); i--) {
             bs.set(i);
         }

         return bs;
    }

    /**
     * Converts bits to long
     * @return TODO
     */
    public Long toLong() {
        return toLong(bits);
    }

    /**
     * Converts bs to long
     * @param bs TODO
     * @return TODO
     */
    public static Long toLong(BitSet bs) {
        if (bs.isEmpty()) {
            return (long) 0;
        }
        return bs.toLongArray()[0];
    }

    /**
     * Wrapper for toString methods. Creates a String from the BitSets instance
     * @return String representation of BitSets instance
     */
    public String toString() {
        return toString(bits, length);
    }

    /**
     * Creates String from a given BitSet
     * @param bs given BitSet
     * @return String representation of BitSet
     */
    public static String toString(BitSet bs) {
        return toString(bs, bs.size());
    }

    /**
     * Creates String from a given BitSet of a specific length. Includes the lowest significant bit to the specified length
     * @param bs given BitSet
     * @param length desired length
     * @return String representation of BitSet
     */
    private static String toString(BitSet bs, int length) {
        final StringBuilder buffer = new StringBuilder(BYTES_OF_LONG * BITS_OF_BYTE);
        IntStream.range(0, length).mapToObj(i -> bs.get(i) ? '1' : '0').forEach(i -> buffer.insert(0, i));
        return buffer.toString();
    }
}
