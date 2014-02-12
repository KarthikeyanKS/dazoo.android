package com.millicom.mitv;

import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ContentCallback;

public class ContentManager implements ContentCallback {

	@Override
	public void onResult(boolean successful, int httpResponseCode, RequestIdentifierEnum requestIdentifier, Object data) {
	}

}
