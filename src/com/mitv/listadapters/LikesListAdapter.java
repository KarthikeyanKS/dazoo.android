
package com.mitv.listadapters;



import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.ContentManager;
import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.UserLike;
import com.mitv.ui.helpers.DialogHelper;



public class LikesListAdapter 
	extends BaseAdapter
{
	private static final String	TAG	= LikesListAdapter.class.getName();

	
	private LayoutInflater layoutInflater;
	private BaseActivity activity;
	private List<UserLike>	userLikes;
	private int	currentPosition;

	
	
	public LikesListAdapter(BaseActivity activity, List<UserLike> userLikes) 
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
			
			viewHolder.programTitleTv = (TextView) rowView.findViewById(R.id.row_likes_text_title_tv);
			viewHolder.programTypeTv = (TextView) rowView.findViewById(R.id.row_likes_text_details_tv);
			viewHolder.buttonContainer = (RelativeLayout) rowView.findViewById(R.id.row_likes_button_container);
			viewHolder.buttonContainer.setTag(Integer.valueOf(position));
			
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
							StringBuilder sb = new StringBuilder();
							sb.append(activity.getString(R.string.movie));
							
							if(userLike.getYear() != 0) 
							{
								sb.append(" ");
								sb.append(userLike.getYear());
							}
							
							holder.programTypeTv.setText(sb.toString());
							
							break;
						}
						
						case OTHER:
						{
							holder.programTypeTv.setText(userLike.getCategory());
							break;
						}
						
						default:
						{
							holder.programTypeTv.setText(activity.getString(R.string.movie));
							
							Log.w(TAG, "Unhandled program type.");
							break;
						}
					}
					break;
				}
				
				case SERIES:
				{
					holder.programTypeTv.setText(activity.getString(R.string.tv_series));
					break;
				}
				
				case SPORT_TYPE:
				{
					holder.programTypeTv.setText(activity.getString(R.string.sport));
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unhandled like type.");
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
	

	
	private Runnable confirmProcedure(final BaseActivity activity)
	{
		return new Runnable()
		{
			public void run() 
			{
				if(currentPosition >= 0 && 
				   currentPosition < userLikes.size()) 
				{
					UserLike userLike = getItem(currentPosition);
					
					ContentManager.sharedInstance().removeUserLike(activity, userLike);

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
		public TextView programTitleTv;
		public TextView programTypeTv;
		public RelativeLayout buttonContainer;
	}
}