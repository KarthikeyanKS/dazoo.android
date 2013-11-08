package com.millicom.secondscreen.utilities;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;

public class MemoryUtils {
	
	private static final String TAG = "MemoryUtils";

	public static void calculateMemoryUsage(Context context){
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		Log.d(TAG,"availableMegs: " + String.valueOf(availableMegs));
		
		
		//Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
		 // Debug.getMemoryInfo(memoryInfo);

		 // String memMessage = String.format("App Memory: Pss=%.2f MB\nPrivate=%.2f MB\nShared=%.2f MB",
		 // memoryInfo.getTotalPss() / 1024.0,
		 // memoryInfo.getTotalPrivateDirty() / 1024.0,
		 // memoryInfo.getTotalSharedDirty() / 1024.0);

		  //Toast.makeText(this,memMessage,Toast.LENGTH_LONG).show();
		  //Log.d("log_tag", memMessage);
		  
		  MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		  activityManager.getMemoryInfo(memoryInfo);

		  Log.i(TAG, " memoryInfo.availMem " + memoryInfo.availMem + "\n" );
		  Log.i(TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n" );
		  Log.i(TAG, " memoryInfo.threshold " + memoryInfo.threshold + "\n" );

		  List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

		  Map<Integer, String> pidMap = new TreeMap<Integer, String>();
		  for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses)
		  {
		      pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
		  }

		  Collection<Integer> keys = pidMap.keySet();

		  for(int key : keys)
		  {
		      int pids[] = new int[1];
		      pids[0] = key;
		      android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
		      for(android.os.Debug.MemoryInfo pidMemoryInfo: memoryInfoArray)
		      {
		          Log.i(TAG, String.format("** MEMINFO in pid %d [%s] **\n",pids[0],pidMap.get(pids[0])));
		          Log.i(TAG, " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");
		          Log.i(TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");
		          Log.i(TAG, " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");
		      }
		  }
	}
}
