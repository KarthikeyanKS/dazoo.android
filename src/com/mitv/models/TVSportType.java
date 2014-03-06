
package com.mitv.models;



import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVSportTypeJSON;
import com.mitv.models.sql.NotificationSQLElement;



public class TVSportType
	extends TVSportTypeJSON 
	implements GSONDataFieldValidation
{

	public TVSportType(NotificationSQLElement item)
	{
		this.sportTypeId = item.getSportTypeId();
		this.name = item.getSportTypeName();
	}
	
	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getSportTypeId()) && !TextUtils.isEmpty(getName()));
		return areDataFieldsValid;
	}
	
}