
package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.DefaultCell;

public class CompletionEntry {
	public static final char WELL_COMPLETITION_SIGNAL = 'w';
	public static final char DELIVERY_SIGNAL = 'd';
	private final int hash;
	private final char signal;
	private final DefaultCell entity;
	private final boolean willComplete;
	private final Tuple position;
	
	
	// Just for well completition
	public CompletionEntry(int hash, Tuple position) {
		this.hash = hash;
		this.signal = WELL_COMPLETITION_SIGNAL;
		this.entity = null;
		this.willComplete = true;
		this.position = position;
	}
	
	// Just for tasks
	public CompletionEntry(int hash, DefaultCell task, boolean willComplete, Tuple position) {
		this.hash = hash;
		this.signal = CompletionEntry.DELIVERY_SIGNAL;
		this.entity = task;
		this.willComplete = willComplete;
		this.position = position;
	}

	public int getHash() {
		return hash;
	}

	public char getSignal() {
		return signal;
	}

	public DefaultCell geEntity() {
		return entity;
	}

	public boolean willComplete() {
		return willComplete;
	}

	public Tuple getLocation() {
		return position;
	}

}
