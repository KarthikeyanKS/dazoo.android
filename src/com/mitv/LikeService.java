
package com.mitv;



import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.millicom.asynctasks.AddLikeTask;
import com.millicom.asynctasks.DeleteLikeTask;
import com.millicom.asynctasks.GetLikesTask;
import com.mitv.R;
import com.mitv.manager.ApiClient;
import com.mitv.manager.ContentParser;
import com.mitv.model.TVLike;



public class LikeService 
{
	private static final String	TAG	= "LikeService";

	
	public static String getLikeType(String programType) 
	{
		if (programType.equals(Consts.PROGRAM_TYPE_TV_EPISODE)) 
		{
			return Consts.LIKE_TYPE_SERIES;
		} 
		else if (programType.equals(Consts.PROGRAM_TYPE_SPORT)) 
		{
			return Consts.LIKE_TYPE_SPORT_TYPE;
		} 
		else 
		{
			return Consts.LIKE_TYPE_PROGRAM;
		}
	}

	
	
	public static boolean isLiked(String token, String programId) 
	{
		ArrayList<TVLike> likesList = new ArrayList<TVLike>();
		
		likesList = LikeService.getLikesList(token);
		
		ArrayList<String> likeEntityIds = new ArrayList<String>();
		
		for (int i = 0; i < likesList.size(); i++)
		{
			String likeType = likesList.get(i).getLikeType();
			
			if (Consts.LIKE_TYPE_SERIES.equals(likeType)) 
			{
				likeEntityIds.add(likesList.get(i).getEntity().getSeriesId());
			} 
			else if (Consts.LIKE_TYPE_PROGRAM.equalsIgnoreCase(likeType))
			{
				likeEntityIds.add(likesList.get(i).getEntity().getProgramId());
			} 
			else if (Consts.LIKE_TYPE_SPORT_TYPE.equals(likeType))
			{
				likeEntityIds.add(likesList.get(i).getEntity().getSportTypeId());
			}
		}

		if (likeEntityIds.contains(programId))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	
	
	public static Toast showSetLikeToast(Activity activity, String likedContentName)
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		
		View layout = inflater.inflate(R.layout.toast_notification_and_like_set, (ViewGroup) activity.findViewById(R.id.notification_and_like_set_toast_container));

		final Toast toast = new Toast(activity.getApplicationContext());

		TextView text = (TextView) layout.findViewById(R.id.notification_and_like_set_toast_tv);
		
		text.setText(likedContentName + activity.getResources().getString(R.string.like_set_text));

		if (android.os.Build.VERSION.SDK_INT >= 13) 
		{
			toast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 5)); //200
		} 
		else 
		{
			toast.setGravity(Gravity.BOTTOM, 0, ((int) activity.getResources().getDimension(R.dimen.bottom_tabs_height) + 5)); //100
		}
		
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
		
		return toast;
	}

	
	
	public static ArrayList<String> getLikeIdsList() 
	{
		ArrayList<String> mitvLikeIdsList = new ArrayList<String>();
		
		GetLikesTask getLikesTask = new GetLikesTask();
		
		String jsonString = "";
		
		try
		{
			jsonString = getLikesTask.execute(SecondScreenApplication.getInstance().getAccessToken()).get();
			
			if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING))
			{
				JSONArray likesListJson = new JSONArray(jsonString);
				
				int size = likesListJson.length();
				
				for (int i = 0; i < size; i++)
				{
					mitvLikeIdsList.add(ContentParser.parseMiTVLikeIds(likesListJson.getJSONObject(i)));
				}
				
				Log.d(TAG,"mitvLikeIdsList: " + mitvLikeIdsList.size());
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return mitvLikeIdsList;
	}

	
	
	public static ArrayList<TVLike> getLikesList(String token) 
	{
		ArrayList<TVLike> mitvLikesList = new ArrayList<TVLike>();
		
		GetLikesTask getLikesTask = new GetLikesTask();
		
		String jsonString = "";
		
		try 
		{
			jsonString = getLikesTask.execute(token).get();
			
			if (jsonString != null && TextUtils.isEmpty(jsonString) != true && !jsonString.equals(Consts.ERROR_STRING)) 
			{
				JSONArray likesListJson = new JSONArray(jsonString);
				
				int size = likesListJson.length();
				
				for (int i = 0; i < size; i++) 
				{
					mitvLikesList.add(ContentParser.parseMiTVLike(likesListJson.getJSONObject(i)));
				}
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return mitvLikesList;
	}
	
	

	public static boolean addLike(String entityId, String likeType)
	{
		AddLikeTask addLikeTask = new AddLikeTask();
		
		int result = 0;
		try 
		{
			result = addLikeTask.execute(SecondScreenApplication.getInstance().getAccessToken(), entityId, likeType).get();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		
		if (Consts.GOOD_RESPONSE == result) 
		{
			return true;
		} 
		else if (Consts.BAD_RESPONSE_PROGRAM_SERIES_NOT_FOUND == result) 
		{
			Log.d(TAG, "Program/Series not found");
			return false;
		} 
		else if (Consts.BAD_RESPONSE_MISSING_TOKEN == result) 
		{
			Log.d(TAG, "Missing token");
			ApiClient.forceLogin();
			return false;
		} 
		else if (Consts.BAD_RESPONSE_INVALID_TOKEN == result) 
		{
			Log.d(TAG, "Invalid token");
			ApiClient.forceLogin();
			return false;
		} 
		else 
		{
			return false;
		}
	}
	
	

	public static boolean removeLike(String entityId, String likeType) 
	{
		DeleteLikeTask deleteLikeTask = new DeleteLikeTask();
		
		int isDeleted = 0;
		
		try 
		{
			isDeleted = deleteLikeTask.execute(SecondScreenApplication.getInstance().getAccessToken(), entityId, likeType).get();
			Log.d(TAG, "delete code: " + isDeleted);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		
		if (Consts.GOOD_RESPONSE_LIKE_IS_DELETED == isDeleted || Consts.GOOD_RESPONSE == isDeleted) 
		{
			return true;
		} 
		else 
		{
			return false;
		}
	}
}
