package com.millicom.secondscreen.utilities;

public class NumbersUtilities {

	public static int getPercentFromTotal(int subtotal, int total) {
		return (int) (((float) subtotal / (float) total) * 100);
	}

	public static int getPercentageFromLongTotal(long subtotal, long total) {
		return (int) (((float) subtotal / (float) total) * 100);
	}
	
}
