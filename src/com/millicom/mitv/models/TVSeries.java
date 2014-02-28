
package com.millicom.mitv.models;



import android.text.TextUtils;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.gson.TVSeriesJSON;
import com.millicom.mitv.models.sql.NotificationSQLElement;


public class TVSeries
	extends TVSeriesJSON implements GSONDataFieldValidation
{

	@Override
	public boolean areDataFieldsValid() {
		boolean areDataFieldsValid = (!TextUtils.isEmpty(getSeriesId()) && !TextUtils.isEmpty(getName()));		
		return areDataFieldsValid;
	}
	
	public TVSeries(NotificationSQLElement item)
	{
		this.seriesId = item.getSeriesId(); 
		this.name = item.getSeriesName();
	}
	
}
