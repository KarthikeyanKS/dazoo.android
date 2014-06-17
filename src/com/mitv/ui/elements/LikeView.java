
package com.mitv.ui.elements;



import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.mitv.R;
import com.mitv.activities.SignUpSelectionActivity;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.FetchRequestResultEnum;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.enums.RequestIdentifierEnum;
import com.mitv.interfaces.ViewCallbackListener;
import com.mitv.managers.ContentManager;
import com.mitv.managers.RateAppManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Competition;
import com.mitv.models.objects.mitvapi.competitions.Team;
import com.mitv.ui.helpers.DialogHelper;
import com.mitv.ui.helpers.ToastHelper;
import com.mitv.utilities.AnimationUtils;
import com.mitv.utilities.NetworkUtils;



public class LikeView 
	extends RelativeLayout 
	implements ViewCallbackListener, OnClickListener
{
	private static final String TAG = LikeView.class.toString();

	
	private BaseActivity activity;
	private LayoutInflater inflater;
	private FontTextView iconView;
	private View containerView;
	private ViewCallbackListener viewCallbackListener;

	private UserLike userLike;
	

	
	public LikeView(Context context) 
	{
		super(context);
		
		setup(context);
	}

	
	
	public LikeView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		setup(context);
	}

	
	
	public LikeView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		
		setup(context);
	}

	
	
	private void setup(Context context) 
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.containerView = inflater.inflate(R.layout.element_like_view, this);
		this.iconView = (FontTextView) this.findViewById(R.id.element_like_image_View);
		this.activity = (BaseActivity) context;
		this.viewCallbackListener = (ViewCallbackListener) context;

		this.setClickable(true);
		
		this.setOnClickListener(this);
	}
	
	
	
	public void setUserLike(Competition competition) 
	{
		userLike = new UserLike(competition);

		updateImage();

		containerView.setBackgroundResource(R.drawable.background_color_selector);
	}
	
	
	
	public void setUserLike(Team team) 
	{
		userLike = new UserLike(team);

		updateImage();

		containerView.setBackgroundResource(R.drawable.background_color_selector);
	}

	
	
	public void setUserLike(TVProgram program) 
	{
		userLike = new UserLike(program);

		updateImage();

		containerView.setBackgroundResource(R.drawable.background_color_selector);
	}
	
	
	
	public void updateImage() 
	{
		if (ContentManager.sharedInstance().getCacheManager().isContainedInUserLikes(userLike)) 
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
		DialogHelper.showRemoveLikeDialog(activity, removeLikeProcedure(), null);
	}
	
	
	
	private void addLike() 
	{
		AnimationUtils.animationSet(this);
		
		setImageToLiked();
		
		ContentManager.sharedInstance().addUserLike(this, userLike);

		TrackingGAManager.sharedInstance().sendUserLikesEvent(userLike, false);
	}

	
	
	@Override
	public void onClick(View v) 
	{
		if(userLike != null)
		{
			RateAppManager.significantEvent(activity);
			
			boolean isLoggedIn = ContentManager.sharedInstance().getCacheManager().isLoggedIn();
	
			final boolean isLiked = ContentManager.sharedInstance().getCacheManager().isContainedInUserLikes(userLike);
	
			if (isLoggedIn)
			{
				boolean isConnected = NetworkUtils.isConnected();
				
				if(isConnected == false)
				{
					ToastHelper.createAndShowNoInternetConnectionToast();
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
				DialogHelper.showPromptSignInDialog(activity, loginBeforeLikeProcedure(), null);
			}
		}
		else
		{
			Log.w(TAG, "UserLike is null");
		}
	}
	
	
	
	private void setImageToLiked() 
	{
		iconView.setTextColor(getResources().getColor(R.color.blue1));
	}
	
	
	
	private void setImageToNotLiked() 
	{
		iconView.setTextColor(getResources().getColor(R.color.grey3));
	}
	
	
	
	/* Remove like dialog */
	private Runnable removeLikeProcedure() 
	{
		return new Runnable() 
		{
			public void run() 
			{
				ContentManager.sharedInstance().removeUserLike(activity, userLike);
				setImageToNotLiked();
				
				setImageToNotLiked();
				TrackingGAManager.sharedInstance().sendUserLikesEvent(userLike, true);
			}
		};
	}
	
	

	/* Sign in dialog */
	private Runnable loginBeforeLikeProcedure() 
	{
		return new Runnable() 
		{
			public void run() 
			{
				/* We are not logged in, but we want the Like to be added after we log in, so set it
				 * After login is complete the ContentManager will perform the adding of the like to backend */
				ContentManager.sharedInstance().setLikeToAddAfterLogin(userLike);
				
				Intent intent = new Intent(activity, SignUpSelectionActivity.class);			
				
				activity.startActivity(intent);
			}
		};
	}


	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier) 
	{
		viewCallbackListener.onResult(fetchRequestResult, requestIdentifier);
		
		ContentManager.sharedInstance().unregisterListenerFromAllRequests(this);
		
		if(fetchRequestResult.wasSuccessful())
		{
			switch (requestIdentifier) 
			{
				case USER_ADD_LIKE:
				{
					Log.d(TAG, "Successfully added like");
					
					StringBuilder sb = new StringBuilder();
					
					LikeTypeResponseEnum likeType = userLike.getLikeType();
					
					switch(likeType)
					{
						case COMPETITION:
						{
							sb.append(activity.getString(R.string.like_set_text_competition));
							break;
						}
						
						case TEAM:
						{
							sb.append(activity.getString(R.string.like_set_text_team));
							break;
						}
						
						case SPORT_TYPE:
						{
							sb.append(activity.getString(R.string.like_set_text_sports));
							break;
						}
						
						case SERIES:
						{
							sb.append(activity.getString(R.string.like_set_text_series));
							break;
						}
						
						case PROGRAM:
						{
							sb.append(activity.getString(R.string.like_set_text_program));
							break;
						}
						
						case UNKNOWN:
						default:
						{
							break;
						}
					}
					
					ToastHelper.createAndShowShortToast(sb.toString());
					
					break;
				}
				
				case USER_REMOVE_LIKE:
				{
					Log.d(TAG, "Successfully removed like");
					break;
				}
				
				default:{/* Do nothing */break;}
			}
		} 
		else 
		{
			switch (requestIdentifier) 
			{
				case USER_ADD_LIKE:
				{
					Log.w(TAG, "Adding of like failed");
					setImageToNotLiked();
					break;
				}
				
				case USER_REMOVE_LIKE:
				{
					Log.w(TAG, "Removing of like failed");
					setImageToLiked();
					break;
				}
				
				default:{/* Do nothing */break;}
			}
		}
	}
}