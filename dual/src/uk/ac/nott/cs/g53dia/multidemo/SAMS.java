
package uk.ac.nott.cs.g53dia.multidemo;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import uk.ac.nott.cs.g53dia.multilibrary.Station;

// My 'Scalable Agent Management System' which utilises composed SAMS classes
public class SAMS extends Observable implements AgentServices, Observer {
	private static SAMS instance = null;
	private final AgentKnowledge knowledgeBase;
	private SAMSTaskAllocator taskAllocator;

	protected SAMS() {
		knowledgeBase = new SAMSKnowledge();
		taskAllocator = new SAMSTaskAllocator(knowledgeBase);
	}
	public static SAMS getInstance() {
		if(instance == null) {
			instance = new SAMS();
		}
		return instance;
	}
	@Override
	public void register(Observer agent) {
		addObserver(agent);
		knowledgeBase.add(agent.hashCode());
		submitMETA(new METAEntry(agent.hashCode(), new Tuple(0, 0), 0, 100));
	}
	@Override
	public void submitPOI(POIEntry entry) {
		knowledgeBase.add(entry);
	}
	@Override
	public void submitMETA(METAEntry entry) {	
		knowledgeBase.add(entry);
	}
	@Override
	public Queue<ActionEntry> requestTask(int hash) {
		return taskAllocator.requestTask(hash);
	}
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DeliveryGoals) {
			handleCompletion((CompletionEntry) arg);
		}
	}
	@Override
	public int getFleetNumber(int hash) {
		int fleetNumber = 1;
		
		for (Number h : knowledgeBase.getMETAKnowledge().keySet()) {
			if(hash == (int) h) {
				return fleetNumber;
			}
			fleetNumber++;
		}
		return -1;
	}
	@Override
	public METAEntry getLastMETASubmission(int hash) {
		return knowledgeBase.getMETAKnowledge().get(hash);
	}

	private void handleCompletion(CompletionEntry entry) {
		switch(entry.getSignal()) {
		case CompletionEntry.WELL_COMPLETITION_SIGNAL:
			// tell the tanker they are at a known location
			setChanged();
			notifyObservers(entry);
			break;
		case CompletionEntry.DELIVERY_SIGNAL:
			// tell the tanker they are at known location and should remove task from action queue
			knowledgeBase.remove((((Station) entry.geEntity()).getTask()));
			setChanged();
			notifyObservers(entry);
			break;
		default:
			break;
		}
	}

}

