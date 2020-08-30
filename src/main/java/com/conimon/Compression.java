package com.conimon;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is an implementation of the algorithm as described in "Lossless Compression of High-volume Numerical Data from Simulations"
 * https://www.researchgate.net/publication/2389424_Lossless_Compression_of_High-volume_Numerical_Data_from_Simulations
 * @author herta
 */
public class Compression {

    /**
     * Compresses a List to a Bitset saving maximum space
     * @param numberList given List to be compressed
     * @param differenceDegree degree to compute differences between values to decrease numbers
     * @return compressed BitSet
     */
    public static BitSet compress(List<Long> numberList, int differenceDegree) {
        List<Long> differenceList = computeDifferenceList(numberList, differenceDegree);
        List<BitSets> bitSetsList = toBitSets(differenceList, differenceDegree);
        return concatenate(bitSetsList);
    }

    /**
     * Converts a List of Longs to a List of BitSets. The first values are not truncated but the following are.
     * @param numberList given List if BitSets
     * @param numberOfNotTruncated number of starting items which are not truncated
     * @return List of BitSets
     */
    public static List<BitSets> toBitSets(List<Long> numberList, int numberOfNotTruncated) {
        List<BitSets> bitSetsList = numberList.subList(0, numberOfNotTruncated).stream()
                .map(BitSets::fromLong)
                .collect(Collectors.toList());
        List<BitSets> differenceBitSetsList = numberList.subList(numberOfNotTruncated, numberList.size()).stream()
                .map(n -> BitSets.fromLong(n).truncate())
                .collect(Collectors.toList());
        bitSetsList.addAll(differenceBitSetsList);
        return bitSetsList;
    }

    /**
     * Concatinates List of BitSets to one BitSet
     * @param bitSetsList given List of BitSets
     * @return concatinated BitSet
     */
    public static BitSet concatenate(List<BitSets> bitSetsList) {
        return BitSets.concatenate(bitSetsList);
    }

    /**
     * Computes differences between values of a given list to minimize numbers
     * @param differenceDegree noted as m in the paper
     * @return list of differences between numbers
     */
    public static List<Long> computeDifferenceList(List<Long> numberList, int differenceDegree) {
        List<Long> differenceList = numberList;
        for (int i=0; i < differenceDegree; i++) {
            differenceList = computeDifferences(differenceList);
        }
        differenceList.addAll(0, numberList.subList(0, differenceDegree));
        return differenceList;
    }

    private static List<Long> computeDifferences(List<Long> numberList) {
        List<Long> difference = new ArrayList<>();
        for (int i=1; i < numberList.size(); i++) {
            difference.add(numberList.get(i) - numberList.get(i-1));
        }
        return difference;
    }
}
