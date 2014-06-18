
package com.mitv.enums;

import com.mitv.Constants;



public enum DeploymentEndpointTypeEnum 
{
	PRODUCTION(0, Constants.BACKEND_PRODUCTION_ENVIRONMENT_DOMAIN, Constants.FRONTEND_PRODUCTION_ENVIRONMENT_DOMAIN),
	TEST(1, Constants.BACKEND_TEST_ENVIRONMENT_DOMAIN, Constants.FRONTEND_TEST_ENVIRONMENT_DOMAIN);
	
	
	
	private final int id;
	private final String backendDomainURL;
	private final String frontendDomainURL;
	
	
	
	DeploymentEndpointTypeEnum(int id, String backendDomainURL, String frontendDomainURL) 
	{
		this.id = id;
		this.backendDomainURL = backendDomainURL;
		this.frontendDomainURL = frontendDomainURL;
	}
	
	
	
	public int getId()
	{
		return id;
	}
	
	
	
	public String getBackendDomainURL()
	{
		return backendDomainURL;
	}
	
	
	
	public String getFrontendDomainURL()
	{
		return frontendDomainURL;
	}
}
