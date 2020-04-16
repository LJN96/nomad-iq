package uk.ac.nott.cs.g53dia.multidemo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import static uk.ac.nott.cs.g53dia.multidemo.DeliveryGoals.signalReset1;
import static uk.ac.nott.cs.g53dia.multidemo.DeliveryGoals.signalReset2;
import static uk.ac.nott.cs.g53dia.multidemo.DeliveryGoals.staticLastTaskQueue;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import uk.ac.nott.cs.g53dia.multilibrary.LoadWasteAction;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;
// Allocates task to agents when they request them
public class SAMSTaskAllocator {
	private final AgentKnowledge knowledgeBase;
	public static boolean attempted = false;
	public static Tuple lastStationWeWentTo;
	public SAMSTaskAllocator(AgentKnowledge knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}
	// Agents will request task if no maintenance goals are generated
	public Queue<ActionEntry> requestTask(int hash) {
		if (!achievedExplorerGoals(hash)) return null;
		Queue<ActionEntry> generatedQueue = null;
		Queue<ActionEntry> generatedQueue2 = null;
		// Prepare the agents buffer queue
		knowledgeBase.loadTaskBufferQueue(hash);
		knowledgeBase.loadWellBufferQueue(hash);
		if (signalReset1){try{knowledgeBase.getWellBufferQueue().remove(knowledgeBase.getWellBufferQueue().get(hash).peek().getLocation());System.out.println("SIGNAL 1");}catch (NullPointerException e){}signalReset1=false;}
		if (signalReset2){try{knowledgeBase.getTaskBufferQueue().remove(knowledgeBase.getTaskBufferQueue().get(hash).peek().getLocation()); System.out.println("SIGNAL 2");} catch (NullPointerException e){}signalReset2=false;}
		// May better heuristic but not reachable so we should see if we can find a reachable
		while (!knowledgeBase.getTaskBufferQueue().get(hash).isEmpty() & generatedQueue == null) {
			StationLocatorEntry taskLocator = knowledgeBase.getTaskBufferQueue().get(hash).peek();
			if (shouldWorkOnTask(hash)) {
				updateMETAEntry(hash, taskLocator.getEntityEntry().getStation().getTask());
				generatedQueue = buildTaskQueue(hash);
			}
			knowledgeBase.getTaskBufferQueue().get(hash).poll();
			return generatedQueue;
		}
		while (!knowledgeBase.getWellBufferQueue().get(hash).isEmpty() & generatedQueue == null) {
			WellLocatorEntry wellLocator = knowledgeBase.getWellBufferQueue().get(hash).peek();
			if(shouldWorkOnWell(hash)) {
				updateMETAEntry2(hash, wellLocator.getEntityEntry().getWell());
				generatedQueue2 = buildWellQueue(hash);
			}
			knowledgeBase.getWellBufferQueue().get(hash).poll();
		}
		return generatedQueue2;
	}
	// Decides if agent requesting task should be assigned task at top of their buffer queue
	private boolean shouldWorkOnTask(int hash) {
		StationLocatorEntry locator = knowledgeBase.getTaskBufferQueue().get(hash).peek();
		double hashHeuristic = knowledgeBase.getTaskBufferQueue().get(hash).peek().getEntityEntry().getHeuristic();
		ArrayList<Number> freeAndReachableAgents;
		Tuple taskLocation = knowledgeBase.getTaskBufferQueue().get(hash).peek().getLocation();
		if(getColleaguesWorkingForTask(hash, locator.getEntityEntry().getStation().getTask()).size() > 0) return false;
		if(!willReachTarget(hash, taskLocation)) return false;
		freeAndReachableAgents = findCommonReachableGoals(hash);
		if(freeAndReachableAgents.size() == 0) return true;
		System.out.println("shouldWorkOnTask");
		return (hashHeuristic <= findBestHeuristicInList(freeAndReachableAgents));
	}
	private boolean shouldWorkOnWell(int hash) {
		WellLocatorEntry locator = knowledgeBase.getWellBufferQueue().get(hash).peek();
		double hashHeuristic = knowledgeBase.getWellBufferQueue().get(hash).peek().getEntityEntry().getHeuristic();
		ArrayList<Number> freeAndReachableAgents;
		Tuple wellLocation = knowledgeBase.getWellBufferQueue().get(hash).peek().getLocation();
		//if(getColleaguesWorkingForTask(hash, locator.getEntityEntry().getWell() > 0) return false;
		if(!willReachTarget(hash, wellLocation)) return false;
		//freeAndReachableAgents = findCommonReachableGoals(hash);
		//if(freeAndReachableAgents.size() == 0) return true;
		System.out.println("shouldWorkOnWell");
		//return (hashHeuristic <= findBestHeuristicInList(freeAndReachableAgents));
		return false;
	}
	private double findBestHeuristicInList(ArrayList<Number> hashes) {
		double bestFoundHeuristic = Double.MAX_VALUE;
		for(int i = 0; i < hashes.size(); i++) {
			StationLocatorEntry colleagueTaskHead = knowledgeBase.getTaskBufferQueue().get(hashes.get(i)).peek();
			int colleagueHeuristic = (int) colleagueTaskHead.getEntityEntry().getHeuristic();
			bestFoundHeuristic = (bestFoundHeuristic > colleagueHeuristic) ? colleagueHeuristic : bestFoundHeuristic;
		}
		System.out.println("findBestHeuristicInList");
		return bestFoundHeuristic;
	}
	// Works out if the agent will need to refill before getting to station**well**
	private boolean willReachTarget(int hash, Tuple taskLocation) {
		Tuple wellLocation = knowledgeBase.getWellBufferQueue().get(hash).peek().getLocation();
		//Tuple taskLocation2 = knowledgeBase.getTaskBufferQueue().get(hash).peek().getLocation();
		Tuple agentLocation = knowledgeBase.getMETAKnowledge().get(hash).getLastKnownLocation();
		int fuel = knowledgeBase.getMETAKnowledge().get(hash).getFuel();
		int waste = knowledgeBase.getMETAKnowledge().get(hash).getWaste();
		boolean reachable;
		if(waste == 0) {
			reachable = AgentUtil.willReachWithCurrentFuel2(taskLocation, agentLocation, fuel);
			System.out.println("reachable = AgentUtil.willReachWithCurrentFuel2");
		}else if (waste != 0){
			reachable = AgentUtil.willReachWithCurrentFuel(wellLocation, agentLocation, fuel);
			System.out.println("reachable = sBWQ AgentUtil.willReachWithCurrentFuel");
		}
		else {reachable = false;}
			return reachable;
	}
	// Will check the head to see if they have common goal, and if free will and can reach will add to result
	private ArrayList<Number> findCommonReachableGoals(int hash) {
		ArrayList<Number> freeAndReachable = new ArrayList<Number>();
		StationLocatorEntry locator = knowledgeBase.getTaskBufferQueue().get(hash).peek();
		// Only want to check the heads of the queue i,e what action they would be given next
		for(Number id : knowledgeBase.getTaskKnowledge().keySet()) {
			StationLocatorEntry colleagueTaskHead = knowledgeBase.getTaskKnowledge().get(id).peek();
			if(AgentUtil.taskEquals(locator.getEntityEntry().getStation().getTask(), colleagueTaskHead.getEntityEntry().getStation().getTask()) && (int) id != hash){
				if(agentIsFree(hash) && willReachTarget((int) id, locator.getLocation())) {
					freeAndReachable.add(id);
				}
			}
		}
		return freeAndReachable;
	}
	private boolean agentIsFree(int hash) {
		return (knowledgeBase.getMETAKnowledge().get(hash).getTask() == null);
	}
	private Queue<ActionEntry> buildTaskQueue(int hash) {
		Queue<ActionEntry> waterQueue = buildTaskDisposeQueue(hash);
		Queue<ActionEntry> deliveryQueue = buildWasteCollectQueue(hash);
		return concatenateQueues(waterQueue, deliveryQueue);
	}
	private Queue<ActionEntry> buildWellQueue(int hash) {
		Queue<ActionEntry> deliveryQueue2 = buildWasteCollectQueue(hash);
		return deliveryQueue2;
	}
	private Queue<ActionEntry> buildWasteCollectQueue(int hash) {
		Queue<ActionEntry> deliveryQueue = new LinkedList<ActionEntry>();
		//Queue<ActionEntry> deliveryQueue = null;
		//lastStationWeWentTo = new LinkedList<ActionEntry>();
		if(shouldBuildWasteQueue(hash)) {
				Station nearestStation = knowledgeBase.getTaskBufferQueue().get(hash).peek().getEntityEntry().getStation();
				//Station nearestStation;
				StationLocatorEntry entry = knowledgeBase.getTaskBufferQueue().get(hash).peek();
				Tuple location = knowledgeBase.getTaskBufferQueue().get(hash).peek().getLocation();
				//StationLocatorEntry entry = knowledgeBase.getTaskKnowledge().get(hash).peek();
				deliveryQueue = new LinkedList<ActionEntry>();
				nearestStation = entry.getEntityEntry().getStation();
				deliveryQueue.add(new ActionEntry(nearestStation, new MoveTowardsAction(nearestStation.getPoint()), entry.getLocation()));
				deliveryQueue.add(new ActionEntry(nearestStation, new LoadWasteAction(nearestStation.getTask()), entry.getLocation()));
				try{lastStationWeWentTo = knowledgeBase.getTaskBufferQueue().get(hash).peek().getLocation();}catch (NullPointerException e){};
				System.out.println("load waste actions sent");
		}
		return deliveryQueue;
	}
	private Queue<ActionEntry> buildTaskDisposeQueue(int hash) {
		Queue<ActionEntry> waterQueue = null;
		if(shouldBuildDisposeQueue(hash)) {
		    //Well nearestWell = knowledgeBase.getWellBufferQueue().get(hash).peek().getEntityEntry().getWell();
			Well nearestWell;
			Tuple location = knowledgeBase.getWellBufferQueue().get(hash).peek().getLocation();
			WellLocatorEntry entry = knowledgeBase.getWellKnowledge().get(hash).peek();
			waterQueue = new LinkedList<ActionEntry>();
			nearestWell = entry.getEntityEntry().getWell();
			waterQueue.add(new ActionEntry(nearestWell, new MoveTowardsAction(nearestWell.getPoint()), location));
			System.out.println("movetowards well (buildtaskdisposequeue)");
			waterQueue.add(new ActionEntry(nearestWell, new DisposeWasteAction(), entry.getLocation()));
			try{lastStationWeWentTo = knowledgeBase.getWellBufferQueue().get(hash).peek().getLocation();}catch (NullPointerException e){};
		}
		return waterQueue;
	}
														// When a task is assigned we should update the META storage on behalf of agent
	private void updateMETAEntry(int hash, Task task) {
		METAEntry entry = knowledgeBase.getMETAKnowledge().get(hash);
		entry.setTask(task);
		knowledgeBase.getMETAKnowledge().put(hash, entry);
	}
	private void updateMETAEntry2(int hash, Well well) {
		METAEntry entry = knowledgeBase.getMETAKnowledge2().get(hash);
		entry.setWell(well);
		knowledgeBase.getMETAKnowledge2().put(hash, entry);
	}
	// Checks if water queue should be built for agent
	private boolean shouldBuildDisposeQueue(int hash) {
		try{
			if(lastStationWeWentTo.equals(knowledgeBase.getWellKnowledge().get(hash).peek().getLocation())){knowledgeBase.getWellKnowledge().get(hash).poll(); System.out.println("%%%FUX FUX");}}catch (NullPointerException e){}
		int agentsFuel = knowledgeBase.getMETAKnowledge().get(hash).getFuel();
		int agentsWaste = knowledgeBase.getMETAKnowledge().get(hash).getWaste();
		Tuple agentLocation = knowledgeBase.getMETAKnowledge().get(hash).getLastKnownLocation();
		Tuple wellLocation = knowledgeBase.getWellKnowledge().get(hash).peek().getLocation();
		boolean reachable = AgentUtil.willReachWithCurrentFuel(wellLocation, agentLocation, agentsFuel);
		System.out.println("should build dq");
		return (reachable && agentsWaste > 0);
	}
	// Checks if delivery queue should be built for agent
	private boolean shouldBuildWasteQueue(int hash) {
		try{
			if(lastStationWeWentTo.equals(knowledgeBase.getTaskKnowledge().get(hash).peek().getLocation())){knowledgeBase.getTaskKnowledge().get(hash).poll(); System.out.println("%%%SUCCESS SUCCESS");}}catch (NullPointerException e){}
		int agentsWaste = knowledgeBase.getMETAKnowledge().get(hash).getWaste();
		Tuple agentLocation = knowledgeBase.getMETAKnowledge().get(hash).getLastKnownLocation();
		Tuple taskLocation = knowledgeBase.getTaskKnowledge().get(hash).peek().getLocation();
		Tuple wellLocation = knowledgeBase.getWellKnowledge().get(hash).peek().getLocation();
		int agentsFuel = knowledgeBase.getMETAKnowledge().get(hash).getFuel();
		boolean reachable = AgentUtil.willReachWithCurrentFuel2(taskLocation, agentLocation, agentsFuel);
		System.out.println("should build wq");
		try{System.out.println("£££"+staticLastTaskQueue.peek().getLocation()+" ^^ "+knowledgeBase.getTaskKnowledge().get(hash).peek().getLocation() +" ^^ "+lastStationWeWentTo);}catch (NullPointerException e){}
		return (reachable && agentsWaste == 0);
	}
	// Checks if explorer goals have been achieved by agents
	private boolean achievedExplorerGoals(int hash) {
		// Hash is redundant as we feed all knowledge to both queues but we must index something as hashes are non linear
		return (knowledgeBase.getWellKnowledge().get(hash).size() > 0 && knowledgeBase.getTaskKnowledge().get(hash).size() > 0);
	}
	// Gets the agents who are cooperating for task
	private ArrayList<Number> getColleaguesWorkingForTask(int hash, Task task) {
		ArrayList<Number> cooperatingAgents = new ArrayList<Number>();
															// Loop over the META submissions (1 per agent) to see if an agent has our optimal task.
		for(Number id : knowledgeBase.getMETAKnowledge().keySet()) {
			if (AgentUtil.taskEquals(knowledgeBase.getMETAKnowledge().get(id).getTask(), task)){
				cooperatingAgents.add(knowledgeBase.getMETAKnowledge().get(id).getHash());
			}
		}
		return cooperatingAgents;
	}
																				// Concatenates two queues, only for use when discarding first and second queues
	private static  Queue<ActionEntry> concatenateQueues(Queue<ActionEntry> first, Queue<ActionEntry> second) {
		Queue<ActionEntry> tmp = new LinkedList<ActionEntry>();
		if(first != null) {
			while(!first.isEmpty()) {
				tmp.add(first.poll());
			}
		}
		if(second != null) {
			while(!second.isEmpty()) {
				tmp.add(second.poll());
			}
		}
		return (tmp.isEmpty()) ? null : tmp;
	}
	private class ExceptionA extends Throwable {
		public ExceptionA(String s) {
		}
	}
}
