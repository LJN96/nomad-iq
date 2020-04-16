
package uk.ac.nott.cs.g53dia.multidemo;

import java.util.Map;
import java.util.PriorityQueue;

import uk.ac.nott.cs.g53dia.multilibrary.Task;

public interface AgentKnowledge {
	
	
	// Instantiates knowledge space for agent
	public void add(int hash);
	
	// Adds point of interest to knowledge base
	public void add(POIEntry entry);
	
	// Adds meta entry for agent to knowledge base
	public void add(METAEntry entry);
	
	// Removes task from knowledge base
	public void remove(Task t);
	
	// Returns the map of agent hashes to well priority queues
	public Map<Number, PriorityQueue<WellLocatorEntry>> getWellKnowledge();
	
	// Returns the map of agent hashes to task priority queues 
	public Map<Number, PriorityQueue<StationLocatorEntry>> getTaskKnowledge();
	
	// Returns the queue of tasks that have not been dismissed
	public Map<Number, PriorityQueue<StationLocatorEntry>> getTaskBufferQueue();

	// Returns the queue of tasks that have not been dismissed
	public Map<Number, PriorityQueue<WellLocatorEntry>> getWellBufferQueue();
	 
	// Copy the task priority queue into a buffer priority queue for agent
	public void loadTaskBufferQueue(int hash);

    // Copy the task priority queue into a buffer priority queue for agent
    public void loadWellBufferQueue(int hash);
	
	// Returns the map of agent hashes to last META submissions
	public Map<Number, METAEntry> getMETAKnowledge();

	// Returns the map of agent hashes to last META submissions
	public Map<Number, METAEntry> getMETAKnowledge2();
}