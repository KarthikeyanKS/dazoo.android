package com.millicom.secondscreen.manager;

import com.millicom.secondscreen.content.SSPage;

public class DazooCoreGetResult {

	private SSPage	mPage	= null;

	public DazooCoreGetResult() {
	}

	public DazooCoreGetResult(SSPage page) {
		setPage(page);
	}

	public SSPage getPage() {
		return mPage;
	}

	public void setPage(SSPage page) {
		mPage = page;
	}
}
