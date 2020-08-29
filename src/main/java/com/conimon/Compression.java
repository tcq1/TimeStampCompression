package com.conimon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is an implementation of the algorithm described in "Lossless main.java.com.conimon.Compression of High-volume Numerical Data from Simulations"
 * https://www.researchgate.net/publication/2389424_Lossless_Compression_of_High-volume_Numerical_Data_from_Simulations
 */

public class Compression {

    public List<BitSets> toBitSets(List<Long> numberList) {
        return numberList.stream()
                .map(n -> BitSets.fromLong(n).truncate())
                .collect(Collectors.toList());

        /*
        int n = 1000;
        System.out.println("number: " + n);
        System.out.println(Integer.toBinaryString(n) + '\n');

        BitSet bs = BitSets.fromLong(n).getBits();
        System.out.println("BitSet: " + bs.toString());
        //Bytes.print(Bytes.fromLong(n));
        BitSet truncated = BitSets.truncate(bs).getBits();
        System.out.println("truncated: "+ BitSets.truncate(bs).toString());

        byte[] bytes = BitSets.toByteArray(truncated);        BitSets.print(bytes);

         */
        //System.out.println(bytes.length);
        //BitSet bs2 = Bytes.fromByteArray(bytes);
        //System.out.println(bs2.toString());
        //System.out.println(Bytes.fromBitSetToLong(bs2));
    }

    /*
    public List<byte[]> toByteList(List<Long> numberList) {
        return numberList.stream()
                .map(n -> BitSets.truncate(BitSets.fromLongToByteArray(n), true))
                .collect(Collectors.toList());
    }

    public byte[] toByteArray(List<Long> numberList) {
        List<byte[]> byteList = this.toByteList(numberList);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        byteList.forEach(bytes -> {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return outputStream.toByteArray();
    }

     */

    /**
     *
     * @param differenceDegree noted as m in the paper
     * @return list of differences between numbers
     */
    public List<Long> computeDifferenceList(List<Long> numberList, int differenceDegree) {
        List<Long> differenceList = numberList;
        for (int i=0; i < differenceDegree; i++) {
            differenceList = computeDifferences(differenceList);
        }
        differenceList.addAll(0, numberList.subList(0, differenceDegree));
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
