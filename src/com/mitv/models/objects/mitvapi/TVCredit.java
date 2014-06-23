
package com.mitv.models.objects.mitvapi;



import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVCreditJSON;



public class TVCredit
	extends TVCreditJSON 
	implements GSONDataFieldValidation
{

	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areDataFieldsValid = (getName().isEmpty() == false && getType().isEmpty() == false);
		
		return areDataFieldsValid;
	}
}