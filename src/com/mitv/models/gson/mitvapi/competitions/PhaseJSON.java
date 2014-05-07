
package com.mitv.models.gson.mitvapi.competitions;



public class PhaseJSON 
{
	protected long competitionId;
	protected long phaseId;
	protected String stage;
	protected String phase;
	protected boolean table; 
	protected boolean current;
	protected boolean currents;
	protected String dateStart;
	protected String dateEnd;
	
	
	
	public PhaseJSON(){}



	public long getPhaseId() 
	{
		return phaseId;
	}



	public String getStage() 
	{
		return stage;
	}



	public long getCompetitionId() {
		return competitionId;
	}



	public String getPhase() {
		return phase;
	}



	public boolean isTable() {
		return table;
	}



	public boolean isCurrent() {
		return current;
	}



	public boolean isCurrents() {
		return currents;
	}



	public String getStartDate() {
		return dateStart;
	}



	public String getEndDate() {
		return dateEnd;
	}
}
