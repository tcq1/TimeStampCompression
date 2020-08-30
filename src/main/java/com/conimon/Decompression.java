package com.conimon;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This class is an implementation of the algorithm as described in "Lossless Compression of High-volume Numerical Data from Simulations"
 * https://www.researchgate.net/publication/2389424_Lossless_Compression_of_High-volume_Numerical_Data_from_Simulations
 * @author Trung
 */
public class Decompression {
    /**
     * Decompresses a compressed list of timestamps and returns a list of timestamps as long values.
     * @param compressedList: Compressed list of timestamps as BitSet
     * @param differenceDegree: Chosen differenceDegree
     * @return List with decompressed timestamp values
     */
    public static List<Long> decompress(BitSet compressedList, int differenceDegree) {
        List<BitSets> deconcatenated = BitSets.deconcatenate(compressedList, differenceDegree);
        List<Long> longValues = getLongValues(deconcatenated);

        List<Long> decompressed = calculateTimestamps(longValues, differenceDegree, differenceDegree);

        return decompressed;
    }

    /**
     * Takes a list of BitSets and returns the first #differenceDegree values
     * @param bitSetsValues: BitSets list with deconcatenated values
     * @return List values converted to Long
     */
    public static List<Long> getLongValues(List<BitSets> bitSetsValues) {
        List<Long> longValues = new ArrayList<>();

        for (int i = 0; i < bitSetsValues.size(); i++) {
            longValues.add(bitSetsValues.get(i).toLong());
        }

        return longValues;
    }

    /**
     * Recursive function to calculate the next difference list until original timestamps calculated
     * @param currentDifferences: Current differences list (initial: long values of deconcatenated BitSets)
     * @param differenceDegree: Chosen differenceDegree
     * @param currentDegree: Current differenceDegree, decreases in every step (initial: differenceDegree)
     * @return List with timestamp values
     */
    public static List<Long> calculateTimestamps(List<Long> currentDifferences, int differenceDegree,
                                                  int currentDegree) {
        List<Long> differenceList = calculateNextDifferences(currentDifferences, differenceDegree);

        return currentDegree == 1 ? differenceList :
                calculateTimestamps(differenceList, differenceDegree, currentDegree - 1);
    }

    /**
     * Calculates the difference list of the level differenceDegree - 1
     * @param currentDifferences: Current differences list (initial: long values of deconcatenated BitSets)
     * @param differenceDegree: Chosen differenceDegree
     * @return List with difference values from the next degree
     */
    public static List<Long> calculateNextDifferences(List<Long> currentDifferences, int differenceDegree) {
        List<Long> nextDifferences = addFirstValues(currentDifferences, differenceDegree);

        for (int i = differenceDegree; i < currentDifferences.size(); i++) {
            long nextValue = currentDifferences.get(i) + nextDifferences.get(i-1);
            nextDifferences.add(nextValue);
        }

        return nextDifferences;
    }

    /**
     * Returns a Long List of the first #differenceDegree values
     * @param values: List of values
     * @param differenceDegree: Chosen differenceDegree
     * @return
     */
    public static List<Long> addFirstValues(List<Long> values, int differenceDegree) {
        List<Long> firstValues = new ArrayList<>();

        for (int i = 0; i < differenceDegree; i++) {
            firstValues.add(values.get(i));
        }

        return firstValues;
    }
}
