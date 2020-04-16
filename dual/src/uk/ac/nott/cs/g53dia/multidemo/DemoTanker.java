package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import java.util.Random;

/**
 * A simple example Tanker
 *
 * @author Julian Zappala
 */
/*
 *
 * Copyright (c) 2011 Julian Zappala
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class DemoTanker extends Tanker {

    public DemoTanker() {
	this(new Random());
    }

    public DemoTanker(Random r) {
	this.r = r;
    }

    /*
     * The following is a simple demonstration of how to write a
     * tanker. The code below is very stupid and simply moves the
     * tanker randomly until the fuel tank is half full, at which
     * point it returns to a fuel pump to refuel.
     */
    public Action senseAndAct(Cell[][] view, long timestep) {

    	// If fuel tank is low and not at the fuel pump then move
    	// towards the fuel pump
        if ((getFuelLevel() <= MAX_FUEL/2) && !(getCurrentCell(view) instanceof FuelPump)) {
            return new MoveTowardsAction(FUEL_PUMP_LOCATION);
        } else {
            // Otherwise, move randomly
            return new MoveAction(r.nextInt(8));
        }
    }

}
