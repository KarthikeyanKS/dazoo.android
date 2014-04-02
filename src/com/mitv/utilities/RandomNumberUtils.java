package com.mitv.utilities;

import java.util.Random;

public class RandomNumberUtils {
	/**
	 * Returns a pseudo-random number between min and max, exclusive.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, exclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randomIntegerInRange(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    int randomNum = rand.nextInt(max - min) + min;

	    return randomNum;
	}
}
