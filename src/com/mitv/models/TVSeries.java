
package com.mitv.models;



import android.text.TextUtils;

import com.mitv.interfaces.GSONDataFieldValidation;
import com.mitv.models.gson.TVSeriesJSON;
import com.mitv.models.sql.NotificationSQLElement;


public class TVSeries
	extends TVSeriesJSON 
	implements GSONDataFieldValidation
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
