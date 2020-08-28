package com.conimon;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an implementation of the algorithm described in "Lossless main.java.com.conimon.Compression of High-volume Numerical Data from Simulations"
 * https://www.researchgate.net/publication/2389424_Lossless_Compression_of_High-volume_Numerical_Data_from_Simulations
 */
@Data
public class Compression {

    @NonNull private List<Long> timestamps;

    /**
     *
     * @param differenceDegree noted as m in the paper
     * @return list of differences between numbers
     */
    public List<Long> computeDifferenceList(int differenceDegree) {
        List<Long> differenceList = timestamps;
        for (int i=0; i < differenceDegree; i++) {
            differenceList = computeDifferences(differenceList);
        }
        return differenceList;
    }

    private List<Long> computeDifferences(List<Long> numberList) {
        List<Long> difference = new ArrayList<>();
        for (int i=1; i < numberList.size(); i++) {
            difference.add(numberList.get(i) - numberList.get(i-1));
        }
        return difference;
    }
}
