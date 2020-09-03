package com.conimon;

import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TimeStampGeneration timeStamps = new TimeStampGeneration(0, 30, 96, 100);

        int differenceDegree = 2;
        BitSet compressed = Compression.compress(timeStamps.getTimeStamps(), differenceDegree);
        List<Long> decompressed = Decompression.decompress(compressed, differenceDegree);

        double compressedSize = compressed.size();
        double uncompressedSize = timeStamps.getTimeStamps().size() * 64;

        System.out.println("Decompressed == timeStamps? " + decompressed.equals(timeStamps.getTimeStamps()));
        System.out.println("Size of BitSet: " + compressedSize);
        System.out.println("Size of timestamps: " + uncompressedSize);
        System.out.println("Compression rate: " + uncompressedSize / compressedSize);
    }
}
