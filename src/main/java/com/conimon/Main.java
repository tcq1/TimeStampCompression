package com.conimon;

import java.time.Instant;
import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long nowInNanoSeconds = Instant.now().toEpochMilli() * 1000000;
        System.out.println("Now in nanoseconds: " + nowInNanoSeconds);
        TimeStampGeneration timeStampGeneration = new TimeStampGeneration(nowInNanoSeconds, 30, 96, 100);

        int differenceDegree = 2;
        List<Long> timeStamps = timeStampGeneration.getTimeStamps();
        BitSet compressed = Compression.compress(timeStamps, differenceDegree);
        List<Long> decompressed = Decompression.decompress(compressed, differenceDegree);

        double compressedSize = compressed.size();
        double uncompressedSize = timeStampGeneration.getTimeStamps().size() * 64;

        System.out.println("Timestamps: " + String.format("%s, %s, %s, ..., %s",
                timeStamps.get(0), timeStamps.get(1), timeStamps.get(2), timeStamps.get(timeStamps.size()-1)));
        System.out.println("Decompressed == timeStamps? " + decompressed.equals(timeStamps));
        System.out.println("Size of BitSet: " + compressedSize);
        System.out.println("Size of timestamps: " + uncompressedSize);
        System.out.println("Compression rate: " + uncompressedSize / compressedSize);
    }
}
