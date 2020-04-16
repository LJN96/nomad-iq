
package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.Task;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

public class METAEntry {
	private Tuple lastKnownLocation;
	private Task task;
	private Well well;
	private int fuel;
	private int hash;
	private int water;
	
	public METAEntry(int hash, Tuple lastKnownLocation, int water, int fuel) {
		this.setHash(hash);
		this.setLastKnownLocation(lastKnownLocation);
		this.setFuel(fuel);
		this.setWaste(water);
	}

	public int getWaste() {
		return water;
	}

	public void setWaste(int water) {
		this.water = water;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setWell(Well well) {
		this.well = well;
	}

	public Tuple getLastKnownLocation() {
		return lastKnownLocation;
	}

	public void setLastKnownLocation(Tuple lastKnownLocation) {
		this.lastKnownLocation = lastKnownLocation;
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}
	
	public String toString() {
		return "hash:: " + hash + "  location::" + lastKnownLocation.toString() + "  water::" +  water + "  fuel:: " + fuel;
	}

}
