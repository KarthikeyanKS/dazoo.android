
package com.mitv.adapters.list;



import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.base.BaseActivity;
import com.mitv.enums.CompetitionCategoryEnum;
import com.mitv.enums.LikeTypeResponseEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.managers.ContentManager;
import com.mitv.managers.TrackingGAManager;
import com.mitv.models.objects.mitvapi.UserLike;
import com.mitv.models.objects.mitvapi.competitions.Competition;
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
			
			viewHolder.title = (TextView) rowView.findViewById(R.id.row_likes_text_title_tv);
			viewHolder.description = (TextView) rowView.findViewById(R.id.row_likes_text_details_tv);
			viewHolder.button = (TextView) rowView.findViewById(R.id.row_likes_button_tv);
			viewHolder.button.setTag(Integer.valueOf(position));
			
			rowView.setTag(viewHolder);
		}

		final ViewHolder holder = (ViewHolder) rowView.getTag();

		final UserLike item = getItem(position);

		if (item != null) 
		{
			holder.title.setText(item.getTitle());

			final LikeTypeResponseEnum likeType = item.getLikeType();

			switch (likeType)
			{
				case PROGRAM:
				{
					ProgramTypeEnum programType = item.getProgramType();
					
					switch (programType)
					{
						case MOVIE:
						{
							StringBuilder sb = new StringBuilder();
							sb.append(item.getGenre());
							
							if(item.getYear() != 0) 
							{
								sb.append(" ");
								sb.append(item.getYear());
							}
							
							holder.description.setText(sb.toString());
							
							break;
						}
						
						case OTHER:
						{
							holder.description.setText(item.getCategory());
							break;
						}
						
						default:
						{
							Log.w(TAG, "Unhandled program type.");
							break;
						}
					}
					break;
				}
				
				case SERIES:
				{
					holder.description.setText(activity.getString(R.string.tv_series));
					break;
				}
				
				case SPORT_TYPE:
				{
					holder.description.setText(activity.getString(R.string.sport));
					break;
				}
				
				case COMPETITION:
				{
					long competitionID = item.getCompetitionId();
					
					Competition competition = ContentManager.sharedInstance().getFromCacheCompetitionByID(competitionID);
					
					CompetitionCategoryEnum competitionCategory = competition.getCompetitionCategory();
					
					String displayCompetitionType;
					
					switch (competitionCategory) 
					{
						case SOCCER:
						{
							displayCompetitionType = activity.getString(R.string.competition_event_like_description);
							break;
						}
						
						default:
						{
							displayCompetitionType = "";
							break;
						}
					}
					
					holder.description.setText(displayCompetitionType);
					break;
				}
				
				case TEAM:
				{
					holder.description.setText(activity.getString(R.string.team_page_team_info_header));
					break;
				}
				
				default:
				{
					Log.w(TAG, "Unhandled like type.");
					break;
				}
			}
			
			holder.button.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					currentPosition = (Integer) v.getTag();

					DialogHelper.showRemoveLikeDialog(activity, confirmLikeRemoveProcedure(activity), null);
				}
			});
		}

		return rowView;
	}
	

	
	private Runnable confirmLikeRemoveProcedure(final BaseActivity activity)
	{
		return new Runnable()
		{
			public void run() 
			{
				if(currentPosition >= 0 && 
				   currentPosition < userLikes.size()) 
				{
					UserLike userLike = getItem(currentPosition);
					
					TrackingGAManager.sharedInstance().sendUserLikesEvent(userLike, true);
					
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
		private TextView title;
		private TextView description;
		private TextView button;
	}
}