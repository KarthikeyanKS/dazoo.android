
package com.mitv.utilities;



import java.util.regex.Pattern;

import com.mitv.Constants;



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
			    	      password.length() >= Constants.PASSWORD_LENGTH_MIN &&
			    	      password.length() <= Constants.PASSWORD_LENGTH_MAX && 
			    	      INVALID_PASSWORD_CHARACTERS_PATTERN.matcher(password).matches() == false;
		
		return isValid;
	}
	
	
	
	public static boolean checkUserFirstname(String firstname)
	{
		boolean isValid = (firstname != null) && 
						  (firstname.length() >= Constants.USER_FIRSTNAME_LENGTH_MIN);
		
		return isValid;
	}
	
	
	public static boolean checkUserLastname(String lastname)
	{
		boolean isValid = (lastname != null) && 
						  (lastname.length() >= Constants.USER_LASTNAME_LENGTH_MIN);
		
		return isValid;
	}
	
	
	public static String escapeSpaceChars(String stringWithSpaceChars) 
	{
		String stringWithEscapedSpaceChars = stringWithSpaceChars.replaceAll(" ", "%20");
		
		return stringWithEscapedSpaceChars;
	}
	
	public static String decodeEncodedSpaceChars(String stringWithEncodedSpaceChars) 
	{
		String stringWithoutEncodedSpaceChars = stringWithEncodedSpaceChars.replaceAll("%20", " ");
		
		return stringWithoutEncodedSpaceChars;
	}
}