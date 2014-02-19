package com.mitv.utilities;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

public class AnimationUtilities {
	
	public static void animationSet(final View v){
		AnimationSet as = new AnimationSet(true);
		ScaleAnimation animationUp = new ScaleAnimation(1, 1, (float) 1.1, (float) 1.1, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
		animationUp.setDuration(30);
		as.addAnimation(animationUp);

		ScaleAnimation animationDown = new ScaleAnimation((float) 1.1, (float)1.3, 1, 1, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);

		animationDown.setDuration(30);
		animationDown.setStartOffset(-3000); /* Think a delay of -3000 ms is the same as 0 ms. Cannot notice any difference. */
		as.addAnimation(animationDown);
		
		v.startAnimation(as);
	}
	
	public static void expand(final View v) {
		v.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		final int targtetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT : (int) (targtetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targtetHeight / (2*v.getContext().getResources().getDisplayMetrics().density)));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / (2*v.getContext().getResources().getDisplayMetrics().density)));
		v.startAnimation(a);
	}
}
