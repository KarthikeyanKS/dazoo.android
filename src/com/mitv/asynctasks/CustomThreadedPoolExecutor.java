
package com.mitv.asynctasks;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;



public class CustomThreadedPoolExecutor 
	extends ThreadPoolExecutor
{
	private int totalTasks;
	private int totalCompletedTasks;
	

	
	public CustomThreadedPoolExecutor(
			int corePoolSize, 
			int maximumPoolSize,
			long keepAliveTime, 
			TimeUnit unit, 
			BlockingQueue<Runnable> workQueue) 
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		
		this.resetTaskCount();
	}
	
	
	
	@SuppressLint("NewApi")
	public void addAndExecuteTask(AsyncTask<String, Void, Void>  task)
	{	
		totalTasks++;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
		{
			task.executeOnExecutor(this);
		}
		else
		{
			task.execute();
		}
	}
	
	
	
	public void incrementCompletedTasks()
	{
		totalCompletedTasks++;
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
	
	
	
	public void resetTaskCount()
	{
		totalTasks = 0;
		totalCompletedTasks = 0;
	}
	
	
	
	public boolean areAllTasksCompleted()
	{
		boolean areAllTasksDone = (totalCompletedTasks >= totalTasks);
		
		return areAllTasksDone;
	}
}