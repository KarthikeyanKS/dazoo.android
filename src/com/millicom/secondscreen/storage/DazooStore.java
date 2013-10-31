package com.millicom.secondscreen.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

public class DazooStore {
	private static final String		TAG			= "DazooStore";

	private static DazooStore	mInstance	= null;
	
	private ArrayList<TvDate> mTvDates;
	private HashMap<String, Channel> mChannels;
	private ArrayList<Tag> mTags;
	private HashMap<GuideKey, Guide> mGuides;
	private HashMap<BroadcastKey, ArrayList<Broadcast>> mTaggedBroadcasts;

	// restrict the constructor from being instantiated
	private DazooStore(){};
	
	public static DazooStore getInstance() {
		if(mInstance == null){
			mInstance = new DazooStore();
		}
		return mInstance;
	}
	
	// tags
	public void setTags(ArrayList<Tag> tags){
		this.mTags = tags;
	}
	
	public ArrayList<Tag> getTags(){
		return this.mTags;
	}
	
	// dates
	public void setTvDates(ArrayList<TvDate> tvDates){
		this.mTvDates = tvDates;
	}
	
	public ArrayList<TvDate> getTvDates(){
		return this.mTvDates;
	}
	
	// channels
	public void setChannels(HashMap<String, Channel> channels){
		this.mChannels = channels;
	}
	
	public HashMap<String, Channel> getChannels(){
		return this.mChannels;
	}
	
	public Channel getChannel(String channelId){
		for (Entry<String, Channel> entry : mChannels.entrySet()){
			if (entry.getKey().equals(channelId)){
				// found the channel by id
				return entry.getValue();
			}
		}
		return null;
	}
	
	// guide
	public void setGuides(HashMap<GuideKey, Guide> guides){
		this.mGuides = guides;
	}
	
	public HashMap<GuideKey, Guide> getGuides(){
		return this.mGuides;
	}
	
	public Guide getChannelGuide(TvDate tvDate, String channelId){
		GuideKey currentKey = new GuideKey();
		currentKey.setDate(tvDate);
		currentKey.setChannelId(channelId);
		
		for (Entry<GuideKey, Guide> entry : mGuides.entrySet()){
			if(entry.getKey().equals(currentKey)){
				// found the guide by date and tag
				return entry.getValue();
			}
		}
		return null;
	}
	
	// broadcasts
	public void setBroadcastsList(HashMap<BroadcastKey, ArrayList<Broadcast>> taggedBroadcasts){
		this.mTaggedBroadcasts = taggedBroadcasts;
	}
	
	public HashMap<BroadcastKey, ArrayList<Broadcast>> getBroadcastsList(){
		return this.mTaggedBroadcasts;
	}
	
	public ArrayList<Broadcast> getTaggedBroadcasts(TvDate date, Tag tag){
		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);
		
		for(Entry<BroadcastKey, ArrayList<Broadcast>> entry: mTaggedBroadcasts.entrySet()){
			if(entry.getKey().equals(broadcastKey)){
				return entry.getValue();
			}
		}
		return null;
	}
	
	public Broadcast getBroadcast(TvDate date, String channelId, long beginTimeInMillis ){
		Guide channelGuide = getChannelGuide(date, channelId);
		ArrayList<Broadcast> channelBroadcasts = channelGuide.getBroadcasts();
		int size = channelBroadcasts.size();
		
		for(int i = 0; i< size; i++){
			long temp = channelBroadcasts.get(i).getBeginTimeMillis();
			if (beginTimeInMillis == temp){
				return channelBroadcasts.get(i);
			}
		}
		return null;
	}
	
}
