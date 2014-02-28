
package com.millicom.mitv.models;



import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVSportTypeJSON;
import com.millicom.mitv.models.sql.NotificationSQLElement;



public class TVSportType
	extends TVSportTypeJSON implements GSONDataFieldValidation
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