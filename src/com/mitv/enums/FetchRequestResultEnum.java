package com.mitv.enums;



import java.io.Serializable;



public enum FetchRequestResultEnum 
   implements Serializable
{
   SUCCESS(200, "The service was successfully executed."),
 
   SUCCESS_WITH_NO_CONTENT(204, "The service was successfully executed."),
   
   SEARCH_CANCELED_BY_USER(300, "The search was canceled by user"),
   
   BAD_REQUEST(400, "The request is not valid."),
   
   UNAUTHORIZED(401, "The service is temporarily unavailable."),
   
   FORBIDDEN(403, "The service is temporarily unavailable."), 
   
   NOT_FOUND(404, "The service is temporarily unavailable."),
   
   UNPROCESSABLE_ENTITY(422, "An error occured while processing your request. Please try again later."),
   
   UNKNOWN_ERROR(1000, "The service is temporarily unavailable."),
   
   API_VERSION_TOO_OLD(1001, "Your app version is too old."),
           
   INTERNET_CONNECTION_AVAILABLE(1002, "Internet connection available"),

   INTERNET_CONNECTION_NOT_AVAILABLE(1003, "Internet connection available"),
   
   JSON_PARSING_ERROR(1009, "Error parsing json"),
   
   RETRY_COUNT_THRESHOLD_REACHED(1010, "Retry count threshold reached"),
   
   /** 
    * DO NOT change the string on these request enumerations. Those are NOT real errors returned by the server.
    * The string is used in comparison to the response string.
    *
    */
   USER_SIGN_UP_EMAIL_ALREADY_TAKEN(1004, "Email already taken"),
   
   USER_SIGN_UP_EMAIL_IS_INVALID(1005, "Not a real email"),
   
   USER_SIGN_UP_PASSWORD_TOO_SHORT(1005, "Password not secure"),
   
   USER_SIGN_UP_FIRST_NAME_NOT_SUPLIED(1006, "First name not supplied"),
   
   USER_SIGN_UP_EMAIL_NOT_FOUND(1007, "Email not found"), 
   
   USER_RESET_PASSWORD_UNKNOWN_ERROR(1008, "Error unknown, probably wrong email address entered");


   
   private final int statusCode;
   private final String description;



   FetchRequestResultEnum(int statusCode,
		   			String description)
	{
	   this.statusCode = statusCode;
	   this.description = description;
	}


   
   public int getStatusCode()
   {
	   return this.statusCode;
   }


   
   public String getDescription()
   {
	   return this.description;
   }


   
   @Override
   public String toString() 
   {
	   StringBuilder sb = new StringBuilder();
	   sb.append(this.statusCode);
	   sb.append(": ");
	   sb.append(this.description);
	   
	   return sb.toString();
   }


   
   public static FetchRequestResultEnum getFetchRequestResultEnumFromCode(int code)
   {
	   for(FetchRequestResultEnum result: FetchRequestResultEnum.values())
	   {
		   if(result.getStatusCode() == code) 
		   {
			   return result;
		   }
		   // No need for else
	   }

	   return FetchRequestResultEnum.UNKNOWN_ERROR;
   }



   public static FetchRequestResultEnum getFetchRequestResultEnumFromCode(String codeAsString)
   {
	   int value = FetchRequestResultEnum.UNKNOWN_ERROR.getStatusCode();

	   try
	   {
		   value = Integer.parseInt(codeAsString);
	   }
	   catch(NumberFormatException nfex)
	   {
		   return FetchRequestResultEnum.UNKNOWN_ERROR;
	   }

	   return getFetchRequestResultEnumFromCode(value);
   }
   
   
   
   public boolean wasSuccessful() 
   {
	   boolean wasSuccessful = (this == SUCCESS || this == SUCCESS_WITH_NO_CONTENT || this == INTERNET_CONNECTION_AVAILABLE);
	   
	   return wasSuccessful;
   }
   
   
   
   public boolean hasUserTokenExpired() 
   {
	   boolean hasUserTokenExpired = (this == FORBIDDEN);
	   
	   return hasUserTokenExpired;
   }
}
