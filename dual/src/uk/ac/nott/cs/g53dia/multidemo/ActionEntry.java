package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.DefaultCell;

// Data struct for queing actions
public class ActionEntry {
	private final DefaultCell cell;
	private final Action action;
	private final Tuple location;
	
	public ActionEntry(DefaultCell cell, Action action, Tuple location) {
		this.cell = cell;
		this.action = action;
		this.location = location;
	}

	public DefaultCell getCell() {
		return cell;
	}
	
	public Action getAction() {
		return action;
	}

	public Tuple getLocation() {
		return location;
	}

}
