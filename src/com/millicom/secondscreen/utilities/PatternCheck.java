package com.millicom.secondscreen.utilities;

import java.util.regex.Pattern;

/**
 * Class to check if inputed email is in accordance to the rules and does not contain forbidden characters
 *
 */
public class PatternCheck {
	public final static Pattern	EMAIL_ADDRESS_PATTERN	= Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
			+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	public static boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

}
