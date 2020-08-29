package com.conimon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

public class TimeStampGeneration {
    @Getter private List<Long> timeStamps;
    private Random random;

    public TimeStampGeneration(double maxTime, double frequency, long delta) {
        this.random = new Random();
        this.timeStamps = this.generateTimeStamps(maxTime, frequency, delta);
    }

    public List<Long> generateTimeStamps(double maxTime, double frequency, long delta) {
        /**
         * Generates an instance of a time stamps list (as 8 byte longs).
         * Parameters:
         *      maxTime: Sampling time in seconds
         *      stepSize: Sampling frequency in kHz
         *      delta: Determines standard deviation for time differences (in nano seconds)
         */

        List<Long> timeStamps = new ArrayList<>();
        timeStamps.add((long) 0);

        boolean limitReached = false;

        // transform maxTime to nano seconds
        maxTime = (long) (maxTime * Math.pow(10, 9));

        // calculate step size in nano seconds
        long stepSize = (long) ((1/frequency) * Math.pow(10, 6));

        while (!limitReached) {
            // calculate random value based on gaussian distribution
            long randomValue = random.nextBoolean() ?
                    (long) (random.nextGaussian()*delta) : (long) (random.nextGaussian()*delta) * -1;

            timeStamps.add(timeStamps.get(timeStamps.size() - 1) + stepSize + randomValue);

            // check if time limit exceeded
            limitReached = timeStamps.get(timeStamps.size() - 1) > maxTime;
            if (limitReached) { timeStamps.remove(timeStamps.size() - 1); }
        }

        return timeStamps;
    }
}
