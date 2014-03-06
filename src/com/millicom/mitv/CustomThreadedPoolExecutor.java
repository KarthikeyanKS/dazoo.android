
package com.millicom.mitv;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.os.AsyncTask;



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
	
	
	
	public void addAndExecuteTask(AsyncTask<String, Void, Void>  task)
	{	
		totalTasks++;
		
		task.executeOnExecutor(this);
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