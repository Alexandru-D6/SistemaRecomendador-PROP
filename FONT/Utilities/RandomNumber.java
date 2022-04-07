package Utilities;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumber {
    // Returns a random double in [min, max)
    public static double randomDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Cannot generate random int if min > max!");
        }
        return (Math.random() * (max - min)) + min;
    }
    
    // Returns a random integer in [min, max] (BOTH INCLUDED)
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Cannot generate random int if min > max!");
        }
        if (max == Integer.MAX_VALUE) {
            max = Integer.MAX_VALUE - 1;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
