package com.conimon;

import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //TimeStampGeneration timeStamps = new TimeStampGeneration(0.000001, 10000, 10);
        TimeStampGeneration timeStamps = new TimeStampGeneration(30, 96, 10);

        int differenceDegree = 2;
        BitSet compressed = Compression.compress(timeStamps.getTimeStamps(), differenceDegree);
        List<Long> decompressed = Decompression.decompress(compressed, differenceDegree);

        int compressedSize = compressed.size();
        int uncompressedSize = timeStamps.getTimeStamps().size() * 64;

        System.out.println("Decompressed == timeStamps?: " + decompressed.equals(timeStamps.getTimeStamps()));
        System.out.println("Size of BitSet: " + compressedSize);
        System.out.println("Size of timestamps: " + uncompressedSize);
        System.out.println("Compression rate: " + uncompressedSize / compressedSize);
    }
}
