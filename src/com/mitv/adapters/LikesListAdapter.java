
package com.mitv.adapters;



import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millicom.mitv.activities.base.BaseActivity;
import com.millicom.mitv.enums.FetchRequestResultEnum;
import com.millicom.mitv.enums.LikeTypeResponseEnum;
import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.enums.RequestIdentifierEnum;
import com.millicom.mitv.interfaces.ActivityCallbackListener;
import com.millicom.mitv.models.UserLike;
import com.millicom.mitv.utilities.DialogHelper;
import com.mitv.R;



public class LikesListAdapter 
	extends BaseAdapter
	implements ActivityCallbackListener
{
	private static final String	TAG	= LikesListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private BaseActivity activity;
	private ArrayList<UserLike>	userLikes;
	private int	currentPosition;

	
	
	public LikesListAdapter(BaseActivity activity, ArrayList<UserLike> userLikes) 
	{
		this.userLikes = userLikes;
		this.activity = activity;		
		this.currentPosition = -1;
	}

	
	
	@Override
	public int getCount() 
	{
		if (userLikes != null) 
		{
			return userLikes.size();
		} 
		else
		{
			return 0;
		}
	}

	
	
	@Override
	public UserLike getItem(int position)
	{
		if (userLikes != null)
		{
			return userLikes.get(position);
		} 
		else
		{
			return null;
		}
	}

	
	
	@Override
	public long getItemId(int arg0)
	{
		return -1;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View rowView = convertView;

		if (rowView == null)
		{
			layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewHolder viewHolder = new ViewHolder();
			
			rowView = layoutInflater.inflate(R.layout.row_likes, null);
			
			viewHolder.headerContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_header_container);
			viewHolder.headerTv = (TextView) rowView.findViewById(R.id.row_likes_header_textview);
			viewHolder.informationContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_text_container);
			viewHolder.programTitleTv = (TextView) rowView.findViewById(R.id.row_likes_text_title_tv);
			viewHolder.programTypeTv = (TextView) rowView.findViewById(R.id.row_likes_text_details_tv);
			viewHolder.buttonContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_button_container);
			viewHolder.buttonIcon = (ImageView) rowView.findViewById(R.id.row_likes_button_iv);
			viewHolder.buttonContainer.setTag(Integer.valueOf(position));
			viewHolder.dividerView = (View) rowView.findViewById(R.id.row_likes_header_divider);
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final UserLike userLike = getItem(position);

		if (userLike != null) 
		{
			holder.programTitleTv.setText(userLike.getTitle());

			final LikeTypeResponseEnum likeType = userLike.getLikeType();

			switch (likeType)
			{
				case PROGRAM:
				{
					ProgramTypeEnum programType = userLike.getProgramType();
					
					switch (programType)
					{
						case MOVIE:
						{
							holder.programTypeTv.setText(activity.getResources().getString(R.string.movie) + " " + userLike.getYear());
							break;
						}
						
						case OTHER:
						{
							holder.programTypeTv.setText(userLike.getCategory());
							break;
						}
						
						default:
						{
							// TODO - Log this
							break;
						}
					}
					break;
				}
				
				case SERIES:
				{
					if(userLike.getYear() != 0) 
					{
						holder.programTypeTv.setText(activity.getResources().getString(R.string.tv_series) + " " + userLike.getYear() + "-");
					} 
					else
					{
						holder.programTypeTv.setText(activity.getResources().getString(R.string.tv_series));
					}
					break;
				}
				
				case SPORT_TYPE:
				{
					holder.programTypeTv.setText(activity.getResources().getString(R.string.sport));
					break;
				}
				
				default:
				{
					break;
				}
			}
			
			holder.buttonContainer.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					currentPosition = (Integer) v.getTag();

					DialogHelper.showRemoveLikeDialog(activity, confirmProcedure(activity), null);
				}
			});
		}

		return rowView;
	}
	
	
	
	@Override
	public void onResult(FetchRequestResultEnum fetchRequestResult, RequestIdentifierEnum requestIdentifier)
	{
		// TODO Auto-generated method stub
	}

	
	
	private Runnable confirmProcedure(BaseActivity activity)
	{
		return new Runnable()
		{
			public void run() 
			{
				if(currentPosition >= 0 && 
				   currentPosition < userLikes.size()) 
				{

					UserLike userLike = getItem(currentPosition);

					// TODO
					//ContentManager.sharedInstance().removeUserLike(activity, userLike.getLikeType(), userLike.getContentId());

					notifyDataSetChanged();
				}
				else
				{
					Log.e(TAG, "Current position is out of bounds.");
				}
			}
		};
	}


	
	private static class ViewHolder 
	{
		public RelativeLayout headerContainer;
		public TextView	headerTv;
		public RelativeLayout informationContainer;
		public TextView programTitleTv;
		public TextView programTypeTv;
		public RelativeLayout buttonContainer;
		public ImageView buttonIcon;
		public View	dividerView;
	}
}