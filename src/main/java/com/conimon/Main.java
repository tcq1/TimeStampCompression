package com.conimon;

import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TimeStampGeneration timeStamps = new TimeStampGeneration(0.000001, 10000, 10);
        int m = timeStamps.getTimeStamps().size();
        BitSet concatenated = Compression.compress(timeStamps.getTimeStamps(), m);
        List<BitSets> deconcatenated = BitSets.deconcatenate(concatenated, m);
        List<BitSets> test = Compression.toBitSets(timeStamps.getTimeStamps(), m);
        System.out.println(timeStamps.getTimeStamps() + "\n--------------------------------------");
        System.out.println(concatenated + "\n--------------------------------------");
        System.out.println(deconcatenated);
        System.out.println(test);
        System.out.println(test.equals(deconcatenated));
    }
}
