package com.conimon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

/**
 * Generates a list of timestamps
 */
public class TimeStampGeneration {
    @Getter @Setter private List<Long> timeStamps;

    public TimeStampGeneration(long startTime, double maxTime, double frequency, long delta) {
        this.timeStamps = this.generateTimeStamps(startTime, maxTime, frequency, delta);
    }

    /**
     * Generates an instance of a time stamps list (as 8 byte longs).
     * @param startTime: start time of the sample
     * @param maxTime: Sampling time in seconds
     * @param frequency: Sampling frequency in kHz
     * @param delta: Determines standard deviation for time differences (in nano seconds)
     * @return List of timestamp values
     */
    public List<Long> generateTimeStamps(long startTime, double maxTime, double frequency, long delta) {
        List<Long> timeStamps = new ArrayList<>();
        timeStamps.add(startTime);

        boolean limitReached = false;

        // transform maxTime to nano seconds
        maxTime = (long) (maxTime * Math.pow(10, 9) + startTime);

        // calculate step size in nano seconds
        long stepSize = (long) ((1/frequency) * Math.pow(10, 6));

        Random random = new Random();

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
