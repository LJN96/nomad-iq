package uk.ac.nott.cs.g53dia.multidemo;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import uk.ac.nott.cs.g53dia.multidemo.StationEntryComparator;
import uk.ac.nott.cs.g53dia.multilibrary.Station;
import uk.ac.nott.cs.g53dia.multilibrary.Task;
import uk.ac.nott.cs.g53dia.multilibrary.Well;
// Scalable Agent Management System's Knowledge Base
public class SAMSKnowledge implements AgentKnowledge {
	// Ranks best well and best station for each tanker (Key == Tankers Hash Code)
	private Map<Number, PriorityQueue<WellLocatorEntry>> tankerWellMap;							// PQ for each tanker ranked on distance
	private Map<Number, PriorityQueue<StationLocatorEntry>> tankerTaskMap;						// PQ for each tanker ranked on heurisitc
	private Map<Number, PriorityQueue<StationLocatorEntry>> tankerBufferMap;					// Allows us to dequeue unreachables, w/out loosing reference
	private Map<Number, PriorityQueue<WellLocatorEntry>> tankerBufferMap2;					// Allows us to dequeue unreachables, w/out loosing reference
	private Map<Number, METAEntry> lastMETASubmission;											// Key == Tankers Hash Code
	private Map<Number, METAEntry> lastMETASubmission2;

	public SAMSKnowledge() {
		tankerWellMap = new HashMap<Number, PriorityQueue<WellLocatorEntry>>();					// Stores each tankers well priority queue
		tankerTaskMap = new HashMap<Number, PriorityQueue<StationLocatorEntry>>();				// Stores each tankers task priority queue
		tankerBufferMap = new HashMap<Number, PriorityQueue<StationLocatorEntry>>();			// Subset of tanker task, that are free and reachable
		tankerBufferMap2 = new HashMap<Number, PriorityQueue<WellLocatorEntry>>();			// Subset of tanker task, that are free and reachable
		lastMETASubmission = new HashMap<Number, METAEntry>();									// Keep 1 meta submission for each agent
		lastMETASubmission2 = new HashMap<Number, METAEntry>();
	}
	@Override
	public void add(int hash) {
		if(tankerWellMap.get(hash) == null) tankerWellMap.put(hash, instantiateNewWellMap());
		if(tankerTaskMap.get(hash) == null) tankerTaskMap.put(hash, instantiateNewTaskMap());
	}
	@Override
	public void add(POIEntry entry) {
		if(entry.getEntity() instanceof Well) {
			addWellToKnowledgeBase(entry);	
		} else if (entry.getEntity() instanceof Station) {
			addTaskToKnowledgeBase(entry);
		}
	}
	@Override
	public void add(METAEntry entry) {
		lastMETASubmission.put(entry.getHash(), entry);
		maintain(entry.getHash());
	}
	@Override
	public void remove(Task t) {
		for(Number hash : tankerTaskMap.keySet()) {
			PriorityQueue<StationLocatorEntry> tmp = instantiateNewTaskMap();
			while(!tankerTaskMap.get(hash).isEmpty()) {
				StationLocatorEntry sle = tankerTaskMap.get(hash).poll();
				if(!AgentUtil.taskEquals(t, sle.getEntityEntry().getStation().getTask())){
					tmp.add(sle);
				}
			}
			tankerTaskMap.put(hash, tmp);
		}
	}
	@Override
	public Map<Number, PriorityQueue<WellLocatorEntry>> getWellKnowledge() { return tankerWellMap; }
	@Override
	public Map<Number, PriorityQueue<StationLocatorEntry>> getTaskKnowledge() { return tankerTaskMap; }
	@Override
	public Map<Number, PriorityQueue<StationLocatorEntry>> getTaskBufferQueue() { return tankerBufferMap; }
	@Override
	public Map<Number, PriorityQueue<WellLocatorEntry>> getWellBufferQueue() { return tankerWellMap; }
	@Override
	public void loadTaskBufferQueue(int hash) { tankerBufferMap.put(hash, new PriorityQueue<StationLocatorEntry>(tankerTaskMap.get(hash))); }
	@Override
	public void loadWellBufferQueue(int hash) { tankerWellMap.put(hash, new PriorityQueue<WellLocatorEntry>(tankerWellMap.get(hash))); }
	@Override
	public Map<Number, METAEntry> getMETAKnowledge() { return lastMETASubmission; }
	@Override
	public Map<Number, METAEntry> getMETAKnowledge2() { return lastMETASubmission2; }
	// Add the found well to all tankers priority queue for assessment	
	private void addWellToKnowledgeBase(POIEntry entry) {
		for(Number hash : tankerWellMap.keySet()) {
			WellEntry wellEntry = new WellEntry((Well) entry.getEntity(), AgentUtil.calculateDistanceToLocation(lastMETASubmission.get(hash).getLastKnownLocation(), entry.getEntityLocation()));
			WellLocatorEntry locator = new WellLocatorEntry(entry.getEntityLocation(), wellEntry);
			if(tankerWellMap.get(hash).contains(locator)) return;
			tankerWellMap.get(hash).add(locator);
		}
	}
	// Add the found task to all tankers priority queue for assessment
	private void addTaskToKnowledgeBase(POIEntry entry) {
		for(Number hash : tankerTaskMap.keySet()) {
			Station station = (Station) entry.getEntity();
			StationEntry stationEntry;
			StationLocatorEntry locator;
			if(station.getTask() == null) return;
			stationEntry = new StationEntry(station, AgentUtil.calculateDistanceToLocation(lastMETASubmission.get(hash).getLastKnownLocation(), entry.getEntityLocation()),lastMETASubmission.get(hash).getWaste());
			locator = new StationLocatorEntry(entry.getEntityLocation(), stationEntry);
			if(tankerTaskMap.get(hash).contains(locator)) return;
			tankerTaskMap.get(hash).add(locator);
		} }
	// Updates the relative distances from each tanker to each point of interest
	private void maintain(int hash) {
		updateWellPriorityQueue(hash);
		updateStationPriorityQueue(hash);
	}
	// Re-order the well based on new meta
	private void updateWellPriorityQueue(int hash) {
		PriorityQueue<WellLocatorEntry> tmpPq = new PriorityQueue<WellLocatorEntry>(tankerWellMap.get(hash));
		tankerWellMap.get(hash).clear();
		// Update distances and then add them back
		while(!tmpPq.isEmpty()) {
			WellLocatorEntry entry = tmpPq.poll();
			entry.getEntityEntry().setCurrDistanceToEntity(AgentUtil.calculateDistanceToLocation(lastMETASubmission.get(hash).getLastKnownLocation(), entry.getLocation()));
			tankerWellMap.get(hash).add(entry);
		} }
	// Re-order the station based on new meta
	private void updateStationPriorityQueue(int hash) {
		PriorityQueue<StationLocatorEntry> tmpPq = new PriorityQueue<StationLocatorEntry>(tankerTaskMap.get(hash));
		tankerTaskMap.get(hash).clear();
		// Update entr w/ curr water and curr distance for heuristic to be up to date (Req / Wat) * Distance
		while(!tmpPq.isEmpty()) {
			StationLocatorEntry entry = tmpPq.poll();
			entry.getEntityEntry().setCurrDistanceToEntity(AgentUtil.calculateDistanceToLocation(lastMETASubmission.get(hash).getLastKnownLocation(), entry.getLocation()));
			entry.getEntityEntry().setCurrWaste(lastMETASubmission.get(hash).getWaste());
			tankerTaskMap.get(hash).add(entry);
		} }
	private PriorityQueue<WellLocatorEntry> instantiateNewWellMap() {
		return new PriorityQueue<WellLocatorEntry>(new WellEntryComparator());
	}
	private PriorityQueue<StationLocatorEntry> instantiateNewTaskMap() {
		return new PriorityQueue<StationLocatorEntry>(new StationEntryComparator());
	}
}
