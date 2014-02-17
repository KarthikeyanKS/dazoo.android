package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.AppVersionDataPart;
import com.mitv.Consts;

public class AppVersionDataGSONTest extends Tests {
	
	private List<AppVersionDataPart> appVersionData;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_API_VERSION);
		appVersionData = Arrays.asList(new Gson().fromJson(jsonString, AppVersionDataPart[].class));
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(appVersionData);
		Assert.assertFalse(appVersionData.isEmpty());
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(AppVersionDataPart appVersionDataParts : appVersionData) {
			Assert.assertNotNull(appVersionData);
			
			Assert.assertNotNull(appVersionDataParts.getName());
			Assert.assertFalse(TextUtils.isEmpty(appVersionDataParts.getName()));
			
			Assert.assertNotNull(appVersionDataParts.getValue());
			Assert.assertFalse(TextUtils.isEmpty(appVersionDataParts.getValue()));

			if(appVersionDataParts.getExpires() != null) {
				Assert.assertTrue(appVersionDataParts.getExpires().getTime() > TIMESTAMP_OF_YEAR_2000);
			}
		}
	}

}
