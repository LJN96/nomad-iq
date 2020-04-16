
package uk.ac.nott.cs.g53dia.multidemo;

//Stores information about locating a station.
public class StationLocatorEntry {
	private Tuple location;
	private StationEntry entityEntry;
	
	public StationLocatorEntry(Tuple location, StationEntry entityEntry) {
		this.setLocation(location);
		this.setEntityEntry(entityEntry);
	}

	public Tuple getLocation() {
		return location;
	}

	public void setLocation(Tuple location) {
		this.location = location;
	}

	public StationEntry getEntityEntry() {
		return entityEntry;
	}

	public void setEntityEntry(StationEntry entityEntry) {
		this.entityEntry = entityEntry;
	}
	
	 public boolean equals(Object obj) {
		 if (!(obj instanceof StationLocatorEntry)) return false;
		 StationLocatorEntry locator = (StationLocatorEntry) obj;
		 
		return (locator.getLocation().equals(this.getLocation()));
	 }
}
