package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.Station;
public class StationEntry {
	private Station station;
	private int currDistanceToEntity;
	private double heuristic;
	private int currWaste;

	public StationEntry(Station station, int currDistanceToEntity, int currWaste) {
		this.station = station;
		this.setCurrWaste(currWaste);
		this.currDistanceToEntity = currDistanceToEntity;
	}
	public Station getStation() {
		return station;
	}
	public int getCurrDistanceToEntity() {
		return currDistanceToEntity;
	}
	public void setCurrDistanceToEntity(int currDistanceToEntity) {
		this.currDistanceToEntity = currDistanceToEntity;
	}
	// Override equals for stopping duplicates in data structures
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StationEntry)) return false;
		StationEntry entry = (StationEntry) obj;
		if (!this.station.getPoint().equals(entry.getStation().getPoint())) return false;
		if(!entry.getStation().getTask().equals(this.station.getTask())) entry.station = this.station;
		return true;
	}
	public double getHeuristic() {
		// Calculate heuristic and stop a divide error before returning it
		calculateHeuristic(currDistanceToEntity, (currWaste == 0) ? 1 : currWaste);
		return heuristic;
	}
	// Heuristic = required * relative distance to travel
	private void calculateHeuristic(int currDistanceTo, int currWaste) {
		heuristic = ((station.getTask().getWasteRemaining()) * currDistanceTo);
		//System.out.println("BUG");
	}
	public int getCurrWaste() {
		return currWaste;
	}
	public void setCurrWaste(int currWaste) {
		this.currWaste = currWaste;
	}
}
