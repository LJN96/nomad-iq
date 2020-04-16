package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Point;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;

public class Fix extends Tanker {
    @Override
    public Action senseAndAct(Cell[][] view, long timestep) {
        return null;
    }

    @Override
    public Point getPosition() {
        return super.getPosition();
    }
}
