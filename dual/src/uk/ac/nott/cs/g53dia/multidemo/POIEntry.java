
package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.DefaultCell;

// Class represents an entry for points of interest.
public class POIEntry {
	private final Tuple entityLocation;
	private final DefaultCell entity;
	
	public POIEntry(Tuple entityLocation, DefaultCell entity) {
		this.entityLocation = entityLocation;
		this.entity= entity;
	}

	public DefaultCell getEntity() {
		return entity;
	}

	public Tuple getEntityLocation() {
		return entityLocation;
	}

}
