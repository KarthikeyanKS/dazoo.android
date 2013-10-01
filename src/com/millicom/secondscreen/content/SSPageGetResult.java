package com.millicom.secondscreen.content;


public class SSPageGetResult {

	private String			mUri = "";
	private SSPage			mPage			= null;

	public SSPageGetResult() {
	}

	public SSPageGetResult(SSPage page) {
		setPage(page);
	}

	public SSPageGetResult(String uri, SSPage page) {
		setPage(page);
		setUri(uri);
	}

	public SSPage getPage() {
		return mPage;
	}

	public String getUri() {
		return mUri;
	}

	public void setPage(SSPage page) {
		mPage = page;
	}

	public void setUri(String uri) {
		mUri = uri;
	}

}
