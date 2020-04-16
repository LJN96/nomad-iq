package uk.ac.nott.cs.g53dia.demo;
import java.util.ArrayList;
import java.util.Observable;

import uk.ac.nott.cs.g53dia.library.*;

class Fueling extends Observable {
    //public ArrayList<E> fuelMap;
    private final xyPos gas;
    public Fueling(xyPos gas) {
        this.gas = gas;
    }
    public Action update(int level, xyPos xy) {
        return gauge(level, xy);
    }
    private Action gauge(int fuel, xyPos xy) {
        Action action = null;
        if (Proximity.howfar(xy.pos_get(), gas.pos_get()) + 40 >= fuel || closerGas(xy.pos_get(), fuel)) {

            if (gas.getter().equals(xy.getter())) {
                new MoveTowardsAction(gas.getter());
                action = new RefuelAction();
                this.setChanged();
                this.notifyObservers();
            } else {
                action = new MoveTowardsAction(gas.getter());
            }
        }
        return action;
    }
    /*public void fuelFind(Cell[][] view, int x, int y) {
        if(view[x][y] instanceof FuelPump) {
            int a,b;						// Location coordinates
            a = x-20 + movementVector.getX();
            b = 20-y + movementVector.getY();
            fuelMap.add(getLocation(), new Tuple(a, b), (DefaultCell) view[x][y]);
        }*/

    private boolean closerGas(coordinateWrapper position, int fuel) {
        return (Proximity.howfar(position, gas.pos_get()) <= 10 & fuel < 50);
    }

    //private class E {
    //}

}
