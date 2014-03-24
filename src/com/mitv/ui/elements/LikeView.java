
package com.mitv.ui.elements;



import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.mitv.ContentManager;
import com.mitv.GATrackingManager;
import com.mitv.R;
import com.mitv.activities.SignUpSelectionActivity;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.models.TVBroadcastWithChannelInfo;
import com.mitv.models.UserLike;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.AnimationUtils;
import com.mitv.utilities.NetworkUtils;



public class LikeView extends RelativeLayout implements ViewCallbackListener, OnClickListener {
	private static final String TAG = LikeView.class.toString();

	private LayoutInflater inflater;
	private FontTextView iconView;
	private BaseActivity activity;
	private View containerView;
	private UserLike likeFromBroadcast;
	private ViewCallbackListener viewCallbackListener;

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

		this.iconView = (FontTextView) this.findViewById(R.id.element_like_image_View);
		this.activity = (BaseActivity) context;
		this.viewCallbackListener = (ViewCallbackListener) context;

		this.setClickable(true);
		this.setOnClickListener(this);
	}

	
	public void setBroadcast(TVBroadcastWithChannelInfo broadcast) 
	{
		this.likeFromBroadcast = UserLike.userLikeFromBroadcast(broadcast);

		updateImage();

		containerView.setBackgroundResource(R.drawable.background_color_selector);
	}
	
	
	public void updateImage() 
	{
		if (ContentManager.sharedInstance().isContainedInUserLikes(likeFromBroadcast)) 
		{
			setImageToLiked();
		} 
		else 
		{
			setImageToNotLiked();
		}
	}
	
	
	private void removeLike() 
	{
		ContentManager.sharedInstance().removeUserLike(this, likeFromBroadcast);
		
		DialogHelper.showRemoveLikeDialog(activity, yesRemoveLike(), null);
		
		setImageToNotLiked();
		
		GATrackingManager.sharedInstance().sendUserLikesEvent(likeFromBroadcast, true);
	}
	
	
	private void addLike() 
	{
		AnimationUtils.animationSet(this);
		
		setImageToLiked();
		
		ContentManager.sharedInstance().addUserLike(this, likeFromBroadcast);

		GATrackingManager.sharedInstance().sendUserLikesEvent(likeFromBroadcast, false);
	}

	
	@Override
	public void onClick(View v) 
	{
		boolean isLoggedIn = ContentManager.sharedInstance().isLoggedIn();

		final boolean isLiked = ContentManager.sharedInstance().isContainedInUserLikes(likeFromBroadcast);

		if (isLoggedIn) 
		{
			boolean isConnected = NetworkUtils.isConnected();
			
			if(isConnected == false)
			{
				String message = activity.getString(R.string.toast_internet_connection);
				
				ToastHelper.createAndShowLikeToast(activity, message);
			}
			else
			{
				if (isLiked) 
				{
					removeLike();
				} 
				else 
				{
					addLike();
				}
			}
		} 
		else 
		{
			DialogHelper.showPromptSignInDialog(activity, yesLikeProc(), noLikeProc());
		}
	}
	
	private void setImageToLiked() {
		iconView.setTextColor(getResources().getColor(R.color.blue1));
	}
	
	private void setImageToNotLiked() {
		iconView.setTextColor(getResources().getColor(R.color.grey3));
	}
	
	/* Remove like dialog */
	private Runnable yesRemoveLike() {
		return new Runnable() {
			public void run() {
				ContentManager.sharedInstance().removeUserLike(activity, likeFromBroadcast);
				setImageToNotLiked();
			}
		};
	}
	

	/* Sign in dialog */
	private Runnable yesLikeProc() {
		return new Runnable() {
			public void run() {
				/* We are not logged in, but we want the Like to be added after we log in, so set it
				 * After login is complete the ContentManager will perform the adding of the like to backend */
				ContentManager.sharedInstance().setLikeToAddAfterLogin(likeFromBroadcast);
				
				Intent intent = new Intent(activity, SignUpSelectionActivity.class);			
				activity.startActivity(intent);
			}
		};
	}

	/* Sign in dialog */
	private Runnable noLikeProc() {
		return new Runnable() {
			public void run() {
			}
		};
	}

	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) {
		viewCallbackListener.onResult(fetchRequestResult, requestIdentifier);
		
		if(fetchRequestResult.wasSuccessful()) {
			switch (requestIdentifier) {
			case USER_ADD_LIKE: {
				Log.d(TAG, "Successfully added like");
				
				StringBuilder sb = new StringBuilder();
				sb.append(activity.getResources().getString(R.string.like_set_text_row1));
				sb.append(" ");
				sb.append(activity.getResources().getString(R.string.like_set_text_row2));
				
				ToastHelper.createAndShowLikeToast(activity, sb.toString());
				
				break;
			}
			case USER_REMOVE_LIKE: {
				Log.d(TAG, "Successfully removed like");
				break;
			}
			default:{/* Do nothing */break;}
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
			default:{/* Do nothing */break;}
			}
		}
		
	}
}