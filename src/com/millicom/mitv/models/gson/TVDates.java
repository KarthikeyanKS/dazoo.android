package com.millicom.mitv.models.gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mitv.Consts;
import com.mitv.utilities.DateUtilities;

public class TVDates {

	private String id;
	private String date;
	private String displayName;
	private transient Date dateObject;
	
	public String getId() {
		return id;
	}

	public Date getDate() {
		if(dateObject == null) {
			SimpleDateFormat dfmInput = DateUtilities.getDateFormat(Consts.TVDATE_DATE_FORMAT);
			try {
				dateObject = dfmInput.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateObject;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}
