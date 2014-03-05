
package com.millicom.mitv;



import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;



public class CustomThreadedPoolExecutor 
	extends ThreadPoolExecutor
{
	HashMap<String, AsyncTask<String, Void, Void> > pendingTasks;
	HashMap<String, AsyncTask<String, Void, Void> > runningTasks;
	HashMap<String, AsyncTask<String, Void, Void> > completedTasks;
	

	
	public CustomThreadedPoolExecutor(
			int corePoolSize, 
			int maximumPoolSize,
			long keepAliveTime, 
			TimeUnit unit, 
			BlockingQueue<Runnable> workQueue) 
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		
		this.pendingTasks = new HashMap<String, AsyncTask<String, Void, Void> >();
		this.runningTasks = new HashMap<String, AsyncTask<String, Void, Void> >();
		this.completedTasks = new HashMap<String, AsyncTask<String, Void, Void> >();
	}
	
	
	
	public void addRunnable(String runnableID, AsyncTask<String, Void, Void>  task)
	{	
		pendingTasks.put(runnableID, task);
	}
	
	
	
	@Override
	public void beforeExecute(Thread t, Runnable r)
	{
		super.beforeExecute(t, r);
	}
	
	
	
	@Override
	public void afterExecute(Runnable r, Throwable t)
	{
		super.afterExecute(r, t);
	}
	
	
	
	public boolean areAllTasksDone()
	{
		boolean areAllTasksDone = (this.getCompletedTaskCount() >= this.getTaskCount());
		
		return areAllTasksDone;
	}
}