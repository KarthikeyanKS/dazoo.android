package com.millicom.mitv.enums;



import java.io.Serializable;



public enum FetchRequestResultEnum 
   implements Serializable
{
   SUCCESS(
           200, 
           "The service was successfully executed."),
 
   NO_CONTENT(
           204,
           "The service was successfully executed."),
   
   BAD_REQUEST(
           400,
           "The parking request is not valid."),
   
   UNAUTHORIZED(
           401, 
           "The service is temporarily unavailable."),
   
   FORBIDDEN(
           403,
           "The service is temporarily unavailable."), 
   
   NOT_FOUND(
           404,
           "The service is temporarily unavailable."),
   
   UNPROCESSABLE_ENTITY(
           422,
           "An error occured while processing your parking request. Please try again later."),
   
   UNKNOWN_ERROR(
           1000,
           "The service is temporarily unavailable."),
   
   API_VERSION_TOO_OLD(
           1001,
           "Your app version is too old.");
   
   

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
	   return this.statusCode + ": " + this.description;
   }


   
   public static FetchRequestResultEnum GetWebserviceResult(int code)
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



   public static FetchRequestResultEnum GetWebserviceResult(String codeAsString)
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

	   return GetWebserviceResult(value);
   }
   
   public boolean wasSuccessful() {
	   boolean wasSuccessful = (this == SUCCESS);
	   return wasSuccessful;
   }
   
}
    
    


