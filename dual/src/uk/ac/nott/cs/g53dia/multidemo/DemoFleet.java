package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;

import java.util.*;

public class DemoFleet extends Fleet {

    /**
     * Number of tankers in the fleet
     */
    private static int FLEET_SIZE = 1;
    Tanker t;
    public DemoFleet() {
	// Create the tankers
	for (int i=0; i<FLEET_SIZE; i++) {
        t = new IntelligentTanker((i & 1) == 0 );
        this.add(t);
	}
    }
}
