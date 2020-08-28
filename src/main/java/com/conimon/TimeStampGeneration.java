package com.conimon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

public class TimeStampGeneration {
    @Getter private List<Long> timeStamps;

    public TimeStampGeneration(int size, long step_size, long delta) {
        this.timeStamps = this.generateTimeStamps(size, step_size, delta);
    }

    public List<Long> generateTimeStamps(int size, long step_size, long delta) {
        /**
         * Generates an instance of a time stamps list (as 8 byte longs).
         * Parameters:
         *      int size: Number of time stamps
         *      long stepSize: Determines time difference between two time stamps (in nano seconds)
         *      long delta: Determines standard deviation (gaussian distribution) for time differences (in nano seconds)
         */

        List<Long> timeStamps = new ArrayList<Long>();
        timeStamps.add((long) 0);

        // create random object for delta calculation
        Random random = new Random();
        for (int i = 1; i < size; i++) {
            // use random sign and gaussian distribution
            long random_value = random.nextBoolean() ?
                    (long) (random.nextGaussian()*delta) : (long) (random.nextGaussian()*delta) * -1;

            timeStamps.add(timeStamps.get(i-1) + step_size + random_value);
        }

        return timeStamps;
    }
}
