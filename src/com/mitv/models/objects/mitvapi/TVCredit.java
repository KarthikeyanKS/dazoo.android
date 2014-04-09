
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVCreditJSON;



public class TVCredit
	extends TVCreditJSON 
	implements GSONDataFieldValidation
{

	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getName()) && !TextUtils.isEmpty(getType()));
		return areDataFieldsValid;
	}
	
}