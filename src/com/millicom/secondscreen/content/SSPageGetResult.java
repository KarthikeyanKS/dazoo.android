package com.millicom.secondscreen.content;

import com.millicom.secondscreen.session.SSResponseCode;

public class SSPageGetResult {

	private String			mUri = "";
	private SSResponseCode	mResponseCode	= null;
	private SSPage			mPage			= null;

	public SSPageGetResult() {
		setResponseCode(new SSResponseCode());
	}

	public SSPageGetResult(SSPage page, SSResponseCode responseCode) {
		setPage(page);
		setResponseCode(responseCode);
	}

	public SSPageGetResult(String uri, SSPage page, SSResponseCode responseCode) {
		setPage(page);
		setResponseCode(responseCode);
		setUri(uri);
	}

	public SSPage getPage() {
		return mPage;
	}

	public SSResponseCode getResponseCode() {
		return mResponseCode;
	}

	public String getUri() {
		return mUri;
	}

	public void setPage(SSPage page) {
		mPage = page;
	}

	public void setResponseCode(SSResponseCode responseCode) {
		mResponseCode = responseCode;
	}

	public void setUri(String uri) {
		mUri = uri;
	}

}
