package com.conimon;

import java.util.Arrays;
import java.util.Random;

public class TimeStampCompression {
    public static void main(String[] args) {
        long[] time_stamps = generateTimeStamps(10, 20, 3);
        System.out.println(Arrays.toString(time_stamps));
    }

    /**
     * Generates an array of time stamps (as 8 byte longs).
     * Parameters:
     *      int size: Number of time stamps
     *      long step_size: Determines time difference between two time stamps (in nano seconds)
     *      long delta: Determines maximum deviation for time differences (in nano seconds)
     */

    public static long[] generateTimeStamps(int size, long step_size, long delta) {
        long[] time_stamps = new long[size];
        time_stamps[0] = 0;
        for (int i = 1; i < size; i++) {
            Random random = new Random();
            long random_value = random.nextBoolean() ? (long) (Math.random()*delta) : (long) (Math.random()*delta) * -1;
            time_stamps[i] = time_stamps[i-1] + step_size + random_value;
        }

        return time_stamps;
    }
}
