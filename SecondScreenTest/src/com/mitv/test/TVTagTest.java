package com.mitv.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.models.gson.TVChannel;
import com.millicom.mitv.models.gson.TVTag;
import com.mitv.Consts;

public class TVTagTest extends Tests {
	
	private List<TVTag> tvTag;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String jsonString = super.deserializeJson(Consts.URL_TAGS_PAGE);
		tvTag = Arrays.asList(new Gson().fromJson(jsonString, TVTag[].class));
	}

	@Test
	public void testNotNull() {
		Assert.assertNotNull(tvTag);
		Assert.assertFalse(tvTag.isEmpty());
	}
	
	@Test
	public void testAllVariablesNotNull() {
		for(TVTag tvTags : tvTag) {
			Assert.assertNotNull(tvTag);
			
			Assert.assertNotNull(tvTags.getId());
			Assert.assertFalse(TextUtils.isEmpty(tvTags.getId()));
			
			Assert.assertNotNull(tvTags.getDisplayname());
			Assert.assertFalse(TextUtils.isEmpty(tvTags.getDisplayname()));
		}
	}

}
