package com.mitv.test.gson;

import junit.framework.Assert;

import org.junit.Test;

public class AddUserLikeTest extends UserLikeTestBase {
	
	
	@Test
	public void testNotNull() {
		Assert.assertNotNull(receivedData);
	}

	@Test
	public void testAllVariablesNotNullOrEmpty() {
		Assert.assertTrue(receivedData.areDataFieldsValid());
	}
}
