
package com.mitv.models.objects.mitvapi;



import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.mitvapi.TVSportTypeJSON;



public class TVSportType
	extends TVSportTypeJSON 
	implements GSONDataFieldValidation
{
	@Override
	public boolean areDataFieldsValid()
	{
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getSportTypeId()) && !TextUtils.isEmpty(getName()));
		return areDataFieldsValid;
	}
	
}