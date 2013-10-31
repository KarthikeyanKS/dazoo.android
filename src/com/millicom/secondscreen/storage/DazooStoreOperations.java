package com.millicom.secondscreen.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.millicom.secondscreen.content.model.Broadcast;
import com.millicom.secondscreen.content.model.Channel;
import com.millicom.secondscreen.content.model.Guide;
import com.millicom.secondscreen.content.model.Tag;
import com.millicom.secondscreen.content.model.TvDate;

public class DazooStoreOperations {
	
	public void saveChannels(ArrayList<Channel> channels){
		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
		ArrayList<String> channelIds = new ArrayList<String>();
		
		int size = channels.size();
		for(int i=0; i < size; i++){
			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
			channelIds.add(channels.get(i).getChannelId());
		}
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setChannels(channelsMap);
		dazooStore.setChannelIds(channelIds);
	}
	
	public void saveMyChannels(ArrayList<Channel> channels){
		HashMap<String, Channel> channelsMap = new HashMap<String, Channel>();
		ArrayList<String> channelIds = new ArrayList<String>();
		
		int size = channels.size();
		for(int i=0; i < size; i++){
			channelsMap.put(channels.get(i).getChannelId(), channels.get(i));
			channelIds.add(channels.get(i).getChannelId());
		}
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setMyChannels(channelsMap);
		dazooStore.setMyChannelIds(channelIds);
	}
	
	public void saveTvDates(ArrayList<TvDate> tvDates){
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setTvDates(tvDates);
	}
	
	public void setTags(ArrayList<Tag> tags){
		DazooStore dazooStore = DazooStore.getInstance();
		dazooStore.setTags(tags);
	}
	
	public void saveGuide(Guide guide, TvDate tvDate, String channelId){
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<GuideKey, Guide> guides = dazooStore.getGuides();
		
		GuideKey guideKey = new GuideKey();
		guideKey.setDate(tvDate);
		guideKey.setChannelId(channelId);
		guides.put(guideKey, guide);
		dazooStore.setGuides(guides);
	}
	
	public void saveTaggedBroadcast(TvDate date, Tag tag, ArrayList<Broadcast> broadcasts){
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<BroadcastKey, ArrayList<Broadcast>> broadcastsList = dazooStore.getBroadcastsList();
		
		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);
		
		broadcastsList.put(broadcastKey, broadcasts);
		dazooStore.setBroadcastsList(broadcastsList);
	}
	
	public void saveMyTaggedBroadcast(TvDate date, Tag tag, ArrayList<Broadcast> broadcasts){
		DazooStore dazooStore = DazooStore.getInstance();
		HashMap<BroadcastKey, ArrayList<Broadcast>> broadcastsList = dazooStore.getMyBroadcastsList();
		
		BroadcastKey broadcastKey = new BroadcastKey();
		broadcastKey.setDate(date);
		broadcastKey.setTag(tag);
		
		broadcastsList.put(broadcastKey, broadcasts);
		dazooStore.setMyBroadcastsList(broadcastsList);
	}

}
