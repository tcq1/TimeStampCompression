package com.conimon;

import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int size = 10;
        int sampleRate = 96000;

        for (int i = 0; i < 7; i++) {
            System.out.println("Size: " + size);

            List<Long> timeStamps = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                double diff = 1e9 * ((double)j / sampleRate);
                timeStamps.add(Math.round(diff));
            }

            int differenceDegree = 2;
            BitSet compressed = Compression.compress(timeStamps, differenceDegree);
            List<Long> decompressed = Decompression.decompress(compressed, differenceDegree);

            double compressedSize = compressed.size();
            double uncompressedSize = timeStamps.size() * 64;

            System.out.println("Timestamps: " + String.format("%s, %s, %s, ..., %s",
                    timeStamps.get(0), timeStamps.get(1), timeStamps.get(2), timeStamps.get(timeStamps.size()-1)));
            System.out.println("Decompressed == timeStamps? " + decompressed.equals(timeStamps));
            System.out.println("Size of BitSet: " + compressedSize);
            System.out.println("Size of timestamps: " + uncompressedSize);
            System.out.println("Compression rate: " + uncompressedSize / compressedSize);

            size *= 10;
        }
    }
}