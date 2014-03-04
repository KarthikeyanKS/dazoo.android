package com.mitv.customviews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.millicom.mitv.ContentManager;
import com.millicom.mitv.activities.BroadcastPageActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.models.sql.NotificationDataSource;
import com.millicom.mitv.models.sql.NotificationSQLElement;
import com.millicom.mitv.utilities.DialogHelper;
import com.millicom.mitv.utilities.NotificationHelper;
import com.millicom.mitv.utilities.ToastHelper;
import com.mitv.Consts;
import com.mitv.R;
import com.mitv.utilities.AnimationUtilities;

public class LikeView extends RelativeLayout implements ActivityCallbackListener, OnClickListener {
	private static final String TAG = LikeView.class.toString();

	private LayoutInflater inflater;
	private ImageView imageView;
	private Activity activity;
	private Context context;
	private TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo;
	private boolean isSet;
	private View containerView;
	private UserLike likeFromBroadcast;

	public LikeView(Context context) {
		super(context);
		setup(context);
	}

	public LikeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
	}

	public LikeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(context);
	}

	private void setup(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.containerView = inflater.inflate(R.layout.element_like_view, this);

		this.imageView = (ImageView) this.findViewById(R.id.element_like_image_View);
		this.context = context;
		this.activity = (Activity) context;

		this.setClickable(true);
		this.setOnClickListener(this);
	}

	public void setBroadcast(TVBroadcastWithChannelInfo broadcast) {
		this.tvBroadcastWithChannelInfo = broadcast;
		this.likeFromBroadcast = UserLike.userLikeFromBroadcast(broadcast);

		if (ContentManager.sharedInstance().isContainedInUserLikes(likeFromBroadcast)) {
			isSet = true;
			setImageToLiked();
		} else {
			isSet = false;
			setImageToNotLiked();
		}

		containerView.setBackgroundResource(R.drawable.background_color_selector);
	}

	@Override
	public void onClick(View v) {
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();

		final boolean isLiked = ContentManager.sharedInstance().isContainedInUserLikes(likeFromBroadcast);

		if (isLoggedIn) {
			if (isLiked) {
				ContentManager.sharedInstance().removeUserLike(this, likeFromBroadcast);
				setImageToNotLiked();
			} else {
				AnimationUtilities.animationSet(this);
				setImageToLiked();
				ContentManager.sharedInstance().addUserLike(this, likeFromBroadcast);
			}
		} else {
			if (BroadcastPageActivity.toast != null) {
				BroadcastPageActivity.toast.cancel();
			}
			DialogHelper.showPromptSignInDialog(activity, yesLikeProc(), noLikeProc());
		}
	}
	
	private void setImageToLiked() {
		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_selected));
	}
	
	private void setImageToNotLiked() {
		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_default));
	}

	public Runnable yesLikeProc() {
		return new Runnable() {
			public void run() {
				setImageToLiked();
				isSet = false;
			}
		};
	}

	public Runnable noLikeProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		
		// TODO NewArc do something here??
		if(fetchRequestResult.wasSuccessful()) {
			switch (requestIdentifier) {
			case USER_ADD_LIKE: {
				Log.d(TAG, "Successfully added like");
				String message = tvBroadcastWithChannelInfo.getProgram().getTitle() + activity.getString(R.string.like_set_text);
				ToastHelper.createAndShowLikeToast(activity, message);
				break;
			}
			case USER_REMOVE_LIKE: {
				Log.d(TAG, "Successfully removed like");
				break;
			}
			}
		} else {
			switch (requestIdentifier) {
			case USER_ADD_LIKE: {
				Log.w(TAG, "Warning, adding of like failed");
				setImageToNotLiked();
				break;
			}
			case USER_REMOVE_LIKE: {
				Log.w(TAG, "Warning, removing of like failed");
				setImageToLiked();
				break;
			}
			}
		}
		
	}
}