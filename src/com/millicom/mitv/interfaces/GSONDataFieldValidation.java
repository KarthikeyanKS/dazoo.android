
package com.millicom.mitv.interfaces;



public interface GSONDataFieldValidation 
{	
	/** 
	 * This method is called from the test classes, in order to verify that the class contains all the required data from the service.
	 * 
	 * Only gson model classes should implement this interface.
	 * 
	 * True if the instance class contains all the required data from the service. False otherwise.
	 */
	public boolean areDataFieldsValid();
}
