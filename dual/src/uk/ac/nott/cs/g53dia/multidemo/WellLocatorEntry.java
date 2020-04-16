
package uk.ac.nott.cs.g53dia.multidemo;

// Stores information about locating a well
public class WellLocatorEntry {
	
	private Tuple location;
	private WellEntry entityEntry;
	
	public WellLocatorEntry(Tuple location, WellEntry entityEntry) {
		this.setLocation(location);
		this.setEntityEntry(entityEntry);
	}

	public Tuple getLocation() {
		return location;
	}

	public void setLocation(Tuple location) {
		this.location = location;
	}

	public WellEntry getEntityEntry() {
		return entityEntry;
	}

	public void setEntityEntry(WellEntry entityEntry) {
		this.entityEntry = entityEntry;
	}
	
	 public boolean equals(Object obj) {
		 if (!(obj instanceof WellLocatorEntry)) return false;
		 WellLocatorEntry locator = (WellLocatorEntry) obj;
		 
		return (locator.getLocation().equals(this.getLocation()));
	 }
	
	
}
