package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.Well;
// Class acts as an entry for a well
public class WellEntry {
	private Well entity;
	private double heuristic;
	private int currDistanceToEntity;
	
	public WellEntry(Well entity, int currDistanceToEntity) {
		this.entity = entity;
		this.currDistanceToEntity = currDistanceToEntity;
	}
	public Well getWell() {
		return entity;
	}
	public int getCurrDistanceToEntity() {
		return currDistanceToEntity;
	}
	public void setCurrDistanceToEntity(int currDistanceToEntity) {
		this.currDistanceToEntity = currDistanceToEntity;
	}

	 @Override
	// Override equals for stopping duplicates in data structures
	 public boolean equals(Object obj) {
		 if (!(obj instanceof WellEntry)) return false;
		WellEntry entry = (WellEntry) obj;
		if (this.entity.getPoint().equals(entry.entity.getPoint())) return true;
		return false;
	 }
	public double getHeuristic() {
		// Calculate heuristic and stop a divide error before returning it
		calculateHeuristic(currDistanceToEntity);
		return heuristic;
	}
	// Heuristic = required * relative distance to travel
	private void calculateHeuristic(int currDistanceTo) {
		heuristic = (currDistanceTo);

	}
}
