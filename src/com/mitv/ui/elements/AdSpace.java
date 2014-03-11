package com.mitv.ui.elements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class AdSpace extends View {

		@SuppressWarnings("unused")
		private static final String TAG = AdSpace.class.getName();

		public AdSpace(Context context) {
			super(context);
		}

		public AdSpace(Context context, AttributeSet attrs) {
			super(context, attrs);
			setup();
		}

		public AdSpace(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			setup();
		}

		private void setup() {
			
		}
}
