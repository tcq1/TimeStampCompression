package com.conimon;

import java.util.BitSet;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TimeStampGeneration timeStamps = new TimeStampGeneration(0.000001, 10000, 10);
        int differenceDegree = 2;
        BitSet compressed = Compression.compress(timeStamps.getTimeStamps(), differenceDegree);
        List<Long> decompressed = Decompression.decompress(compressed, differenceDegree);

        System.out.println(timeStamps.getTimeStamps());
        System.out.println(compressed);
        System.out.println(decompressed);
        System.out.println(decompressed.equals(timeStamps.getTimeStamps()));
    }
}
