
package uk.ac.nott.cs.g53dia.multidemo;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import uk.ac.nott.cs.g53dia.multilibrary.*;


// Class responsible for dequeue of movements and returning to where it left off once interrupted
public class AgentExplorer extends Observable {
	private Queue<Number> currentExplorerQueue;												// A queue of where we are going next		
	private Point stopPoint;
	private final boolean opposite;
	private MaintenanceGoals maintenance;

	
	public AgentExplorer(boolean opposite) {
		currentExplorerQueue = new LinkedList<Number>();									// A queue of where we are going next
		stopPoint = null;
		this.opposite = opposite;
	}
	
	// Dequeue the explorers route, i am aware of the bug where it does'nt always follow the path specifically
	public Action dequeue(Point currPos) {
		Action action;
		// Checks if we have been stopped and returns us to our previous location
		if(stopPoint != null) {
			setChanged();
			notifyObservers(-1);
			action = returnToStopLocation(currPos);
			if(action != null) return action;
		}
		// Regenerates explorer path if empty
		if(currentExplorerQueue.isEmpty()) {
			System.out.println("regenerate explorer");
			currentExplorerQueue = (!opposite) ? generateDefaultPath() : generateOppositePath();
		}
		setChanged();
		notifyObservers(currentExplorerQueue.peek());
		return new MoveAction(currentExplorerQueue.poll().intValue());
	}
	// Interrupts the explorer only if we haven't already been interrupted
	public void interupt(Point stopPoint) {
		if(stopPoint != null) {
			this.stopPoint = stopPoint;
		}
	}
	// Returns the explorer to the position of where it was interrupted
	private Action returnToStopLocation(Point currPos) {
		Action action = null;
		
		// Don't issue an action if we have arrived at the stop point
		if(currPos.equals(stopPoint)) {
			stopPoint = null;
		} else {
			action = new MoveTowardsAction(stopPoint);
		}
		
		return action;
	}
	
	// Generates 3rd route from lecture slides
	private Queue<Number> generateDefaultPath () {
		Queue<Number> queue = new LinkedList<Number>();
		int moveDistance = 25;

		System.out.println("!!!d");
		for (int i = 0; i < moveDistance; i++) {
			queue.add(MoveAction.NORTHEAST);
		}
		
		for (int i = 0; i < moveDistance * 2; i++) {
			queue.add(MoveAction.SOUTH);
		}
		
		for (int i = 0; i < moveDistance; i++ ) {
			queue.add(MoveAction.NORTHWEST);
		}
		
		for (int i = 0; i < moveDistance; i++ ) {
			queue.add(MoveAction.NORTHWEST);
		}
		
		for (int i = 0; i < moveDistance * 2; i++) {
			queue.add(MoveAction.SOUTH);
		}
		
		for (int i = 0; i < moveDistance; i++) {
			queue.add(MoveAction.NORTHEAST);
		}

		return queue;
	}
	
	// Generates 3rd route from lecture slides
	private Queue<Number> generateOppositePath() {
		Queue<Number> queue = new LinkedList<Number>();
		int moveDistance = 25;

		System.out.println("!!!");
		for (int i = 0; i < moveDistance; i++) {
			queue.add(MoveAction.NORTHWEST);
		}
		
		for (int i = 0; i < moveDistance * 2; i++) {
			queue.add(MoveAction.SOUTH);
		}
		
		for (int i = 0; i < moveDistance; i++ ) {
			queue.add(MoveAction.NORTHEAST);
		}
		
		for (int i = 0; i < moveDistance; i++ ) {
			queue.add(MoveAction.NORTHEAST);
		}
		
		for (int i = 0; i < moveDistance * 2; i++) {
			queue.add(MoveAction.SOUTH);
		}
		
		for (int i = 0; i < moveDistance; i++) {
			queue.add(MoveAction.NORTHWEST);
		}
		
		
		return queue;
	}

}
