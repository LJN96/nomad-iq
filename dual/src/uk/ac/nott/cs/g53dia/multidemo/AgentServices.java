
package uk.ac.nott.cs.g53dia.multidemo;

import java.util.Observer;
import java.util.Queue;

public interface AgentServices extends Observer{

	// Register agent with service
	public void register(Observer observer);
	
	// Submit a point of interest to the knowledege sets
	public void submitPOI(POIEntry entry);
	
	// Submits an agents META (data) entry to keep the manager informed
	public void submitMETA(METAEntry entry);
	
	// Returns the agents last submission
	public METAEntry getLastMETASubmission(int hash);
	
	// Asks manager for a task queue, must provide the agents hashcode
	public Queue<ActionEntry> requestTask(int hash);
	
	// @@ REMOVE DEBUG ONLY
	public int getFleetNumber(int hash);
}