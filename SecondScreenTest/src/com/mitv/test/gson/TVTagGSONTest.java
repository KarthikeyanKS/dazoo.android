
package com.mitv.test.gson;



import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.millicom.mitv.enums.HTTPRequestTypeEnum;
import com.millicom.mitv.http.HTTPCoreResponse;
import com.millicom.mitv.models.TVTag;
import com.mitv.Consts;


/**
 * This class tests the fetched tags. Returns a list of tag objects (id and displayname).
 * 
 * GET /epg/tags/visible
 * 
 * @author atsampikakis
 *
 */
public class TVTagGSONTest 
	extends TestCore 
{
	private List<TVTag> tvTag;

	
	
	@Override
	protected void setUp() 
			throws Exception
	{
		super.setUp();
		
		String url = Consts.URL_TAGS_PAGE;
		
		HTTPCoreResponse httpCoreResponse = executeRequest(HTTPRequestTypeEnum.HTTP_GET, url.toString());
		
		String responseString = httpCoreResponse.getResponseString();
		
		tvTag = Arrays.asList(new Gson().fromJson(responseString, TVTag[].class));
	}

	
	
	@Test
	public void testNotNull()
	{
		Assert.assertNotNull(tvTag);
		Assert.assertFalse(tvTag.isEmpty());
	}
	
	
	
	@Test
	public void testAllVariablesNotNull() 
	{
		for(TVTag tvTags : tvTag) 
		{
			Assert.assertNotNull(tvTag);
			
			Assert.assertNotNull(tvTags.getId());
			Assert.assertFalse(TextUtils.isEmpty(tvTags.getId()));
			
			Assert.assertNotNull(tvTags.getDisplayName());
			Assert.assertFalse(TextUtils.isEmpty(tvTags.getDisplayName()));
		}
	}
}