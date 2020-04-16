package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multidemo.AgentKnowledge;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import static uk.ac.nott.cs.g53dia.multidemo.SAMSTaskAllocator.attempted;
import uk.ac.nott.cs.g53dia.multilibrary.*;

public class DeliveryGoals extends Observable {
	private Queue<ActionEntry> currentTaskQueue;
	private Queue<ActionEntry> lastTaskQueue;
	private AgentServices services;
	private int agentHash;
	public static boolean signalReset1 = false;
	public static boolean signalReset2 = false;
	public static Queue<ActionEntry> staticLastTaskQueue;

	public DeliveryGoals(AgentServices services, int agentHash) {
		this.services = services;
		this.agentHash = agentHash;
		addObserver(services);
		currentTaskQueue = new LinkedList<ActionEntry>();
		lastTaskQueue = new LinkedList<ActionEntry>();
		staticLastTaskQueue = new LinkedList<ActionEntry>();
	}

	// Gets called on tick if no maintenance goals have been set
	public Action update(LocationPoint position, int water) {
		Action action = null;
		if (failedToReachWell()) {
			signalReset1 = true;
		}
		if (failedToReachStation()) {
			signalReset2 = true;
		}
		if (currentTaskQueue.size() == 0) {
			Queue<ActionEntry> tasks = services.requestTask(agentHash);
			// Keeps the current task queue allocated
			currentTaskQueue = (tasks == null) ? currentTaskQueue : tasks;
		}
		// Load the action if present
		action = (currentTaskQueue.size() != 0) ? currentTaskQueue.peek().getAction() : null;
		if (action != null) {
			action = (hasArrived(position, water)) ? currentTaskQueue.poll().getAction() : action;
		}
		return action;
	}

	public boolean failedToReachWell() {
		try {
			if (lastTaskQueue.peek().getCell() instanceof Well && currentTaskQueue.peek().getCell() instanceof Well) {
				return true;
			} else
				return false;
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean failedToReachStation() {
		try{
			if (lastTaskQueue.peek().getCell() instanceof Station && currentTaskQueue.peek().getCell() instanceof Station) {
				return true;
			}else
				return false; }
		catch (NullPointerException e){}return false;}

	// Tests if we have arrived at our current queues location
	private boolean hasArrived(LocationPoint currPosition, int water) {
		if(currentTaskQueue.peek() == null) return false;
		if(currentTaskQueue.peek().getCell() instanceof Well) {
			if (currPosition.getPoint().equals(currentTaskQueue.peek().getCell().getPoint())) {
				setChanged();
				notifyObservers(new CompletionEntry(agentHash, currentTaskQueue.peek().getLocation()));
				currentTaskQueue.poll();
				lastTaskQueue.add(currentTaskQueue.peek());
				staticLastTaskQueue = lastTaskQueue;
				return true;
			}
		} else if (currentTaskQueue.peek().getCell() instanceof Station) {
			if (currPosition.getPoint().equals(currentTaskQueue.peek().getCell().getPoint())) {
				Station dequeuedStation = (Station) currentTaskQueue.peek().getCell();
				boolean willComplete = (water >= dequeuedStation.getTask().getWasteRemaining());
				// Notify services of completion status, and use the location then poll queue.
				setChanged();
				notifyObservers(new CompletionEntry(agentHash, dequeuedStation, willComplete, currentTaskQueue.poll().getLocation()));
				lastTaskQueue.add(currentTaskQueue.peek());
				staticLastTaskQueue = lastTaskQueue;
				return true;
			}
		}
		return false;
	}
	public void clearQueue() {
		currentTaskQueue.clear();
	}
	// If joint intention we may get notified an agent has completed common goal
	public boolean removedFromTaskQueue(CompletionEntry entry) {
		if(entry.getHash() == agentHash) return true;
		boolean clearedFlag = false;
		Queue<ActionEntry> tasks = new LinkedList<ActionEntry>(currentTaskQueue);
		while((!tasks.isEmpty() || clearedFlag)) {
			if (tasks.poll().getCell() instanceof Station) {
				Station station = (Station) tasks.peek().getCell();
				if(AgentUtil.taskEquals(((Station) entry.geEntity()).getTask(), station.getTask())){
					currentTaskQueue.clear();
					clearedFlag = true;
				}
				tasks.poll();
			}
		}
		return clearedFlag;
	}
}
