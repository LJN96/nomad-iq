
package uk.ac.nott.cs.g53dia.multidemo;

import java.util.Comparator;

// Compares two stations based on their heuristic (lower num = greater priority)
public class StationEntryComparator implements Comparator<StationLocatorEntry> {

	public StationEntryComparator() {}

	@Override
	public int compare(StationLocatorEntry entry1, StationLocatorEntry entry2) {
		// Heuristic = (Req / currWaste) * distance to entity

		if (entry1.getEntityEntry().getCurrDistanceToEntity() < entry2.getEntityEntry().getCurrDistanceToEntity()) return -1;
		if (entry1.getEntityEntry().getCurrDistanceToEntity() > entry2.getEntityEntry().getCurrDistanceToEntity()) return 1;

		return 0;
	}

}
