package com.mitv;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.utilities.GenericUtils;

public class ListenerHolder {
	ViewCallbackListener listener;
	
	public ListenerHolder(ViewCallbackListener listener) {
		this.listener = listener;
	}
	
	public ViewCallbackListener getListener() {
		return listener;
	}
	
	public boolean isListenerAlive() {
		boolean isListenerAlive = false;
		if(listener != null) {
			if(listener instanceof Activity) {
				isListenerAlive = GenericUtils.isActivityNotNullAndNotFinishingAndNotDestroyed(((Activity)listener));
			} else if (listener instanceof Fragment) {
				isListenerAlive = !((Fragment)listener).isRemoving();
			}
		}
		return isListenerAlive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((listener == null) ? 0 : listener.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ListenerHolder other = (ListenerHolder) obj;
		if (listener == null) {
			if (other.listener != null) {
				return false;
			}
		} else if (!listener.equals(other.listener)) {
			return false;
		}
		return true;
	}
	
	
}