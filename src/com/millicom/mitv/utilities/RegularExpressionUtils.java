
package com.millicom.mitv.utilities;



import java.util.regex.Pattern;

import com.mitv.Consts;



public abstract class RegularExpressionUtils 
{
	@SuppressWarnings("unused")
	private static final String TAG = RegularExpressionUtils.class.getName();
	
	private final static Pattern EMAIL_ADDRESS_PATTERN	= Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
			+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	private final static Pattern INVALID_PASSWORD_CHARACTERS_PATTERN = Pattern.compile("[%,#/|<>]+");
	
	
	
	public static boolean checkEmail(String email) 
	{
		boolean isValid = (email != null) &&
						  EMAIL_ADDRESS_PATTERN.matcher(email).matches();
		
		return isValid;
	}
	
	
	
	public static boolean checkPassword(String password)
	{
		boolean isValid = (password != null) && 
			    	      password.length() >= Consts.PASSWORD_LENGTH_MIN &&
			    	      password.length() <= Consts.PASSWORD_LENGTH_MAX && 
			    	      INVALID_PASSWORD_CHARACTERS_PATTERN.matcher(password).matches() == false;
		
		return isValid;
	}
}