package com.conimon;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This class is an implementation of the algorithm described in "Lossless main.java.com.conimon.Compression of High-volume Numerical Data from Simulations"
 * https://www.researchgate.net/publication/2389424_Lossless_Compression_of_High-volume_Numerical_Data_from_Simulations
 * @author Trung
 */
public class Decompression {
    public static List<Long> decompress(BitSet compressedList, int differenceDegree) {
        /**
         * Decompresses a compressed list of timestamps and returns a list of timestamps as long values.
         * Parameters:
         *      compressedList: Compressed list of timestamps as BitSet
         *      differenceDegree: Chosen differenceDegree
         */

        List<BitSets> deconcatenated = BitSets.deconcatenate(compressedList, differenceDegree);
        List<Long> longValues = getLongValues(deconcatenated);

        List<Long> decompressed = calculateTimestamps(longValues, differenceDegree, differenceDegree);

        return decompressed;
    }

    public static List<Long> getLongValues(List<BitSets> bitSetsValues) {
        /**
         * Takes a list of BitSets and returns the first #differenceDegree values
         * Parameters:
         *      values: BitSets list with deconcatenated values
         *      differenceDegree: chosen differenceDegree
         */

        List<Long> longValues = new ArrayList<>();

        for (int i = 0; i < bitSetsValues.size(); i++) {
            longValues.add(bitSetsValues.get(i).toLong());
        }

        return longValues;
    }

    public static List<Long> calculateTimestamps(List<Long> currentDifferences, int differenceDegree,
                                                  int currentDegree) {
        /**
         * Recursive function to calculate the next difference list until original timestamps calculated
         * Parameters:
         *      currentDifferences: Current differences list (initial: long values of deconcatenated BitSets)
         *      differenceDegree: Chosen differenceDegree
         *      currentDegree: Current differenceDegree, decreases in every step (initial: differenceDegree)
         */

        List<Long> differenceList = calculateNextDifferences(currentDifferences, differenceDegree);

        return currentDegree == 1 ? differenceList :
                calculateTimestamps(differenceList, differenceDegree, currentDegree - 1);
    }

    public static List<Long> calculateNextDifferences(List<Long> currentDifferences, int differenceDegree) {
        /**
         * Calculates the difference list of the level differenceDegree - 1
         * Parameters: see calculateDifferences()
         */
        List<Long> nextDifferences = addFirstValues(currentDifferences, differenceDegree);

        for (int i = differenceDegree; i < currentDifferences.size(); i++) {
            long nextValue = currentDifferences.get(i) + nextDifferences.get(i-1);
            nextDifferences.add(nextValue);
        }

        return nextDifferences;
    }

    public static List<Long> addFirstValues(List<Long> values, int differenceDegree) {
        /**
         * Returns a Long List of the first #differenceDegree values
         */
        List<Long> firstValues = new ArrayList<>();

        for (int i = 0; i < differenceDegree; i++) {
            firstValues.add(values.get(i));
        }

        return firstValues;
    }
}
