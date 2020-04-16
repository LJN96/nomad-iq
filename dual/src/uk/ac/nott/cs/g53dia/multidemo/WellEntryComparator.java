
package uk.ac.nott.cs.g53dia.multidemo;

import java.util.Comparator;

// Compares two well entries by their relative distance from agent
public class WellEntryComparator implements Comparator<WellLocatorEntry> {

	public WellEntryComparator () {
		
	}
	@Override
	public int compare(WellLocatorEntry entry1, WellLocatorEntry entry2) {
		if (entry1.getEntityEntry().getCurrDistanceToEntity() < entry2.getEntityEntry().getCurrDistanceToEntity()) return - 1;
		if (entry1.getEntityEntry().getCurrDistanceToEntity() > entry2.getEntityEntry().getCurrDistanceToEntity()) return 1;
		
		return 0;
	}

}
