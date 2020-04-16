package uk.ac.nott.cs.g53dia.demo;
import uk.ac.nott.cs.g53dia.library.*;
import java.util.*;

interface Refresh {
    void regen(coordinateWrapper gps);
    void schedule(coordinateWrapper gps, coordinateWrapper insPos, DefaultCell ins);
    PriorityQueue<?> getQueue();
}
class DemoTanker extends Tanker implements Observer {
    private final ArrayList<Cell> stations = new ArrayList<>();
    private final ArrayList<Cell> wells = new ArrayList<>();
    private Process dispose;
    private coordinateWrapper vector;
    private int routArg;
    private boolean deviate;
    private Fueling routine;
    private Router roam;

    public DemoTanker() {
        this(new Random());
        routine = new Fueling(new xyPos(FUEL_PUMP_LOCATION, new coordinateWrapper(0, 0)));
        roam = new Router();
        vector = new coordinateWrapper(0, 0);
        dispose = new Process(FUEL_PUMP_LOCATION);
        routArg = -1;
        deviate = false;
        routine.addObserver(dispose);
        routine.addObserver(this);
        dispose.addObserver(this);
        roam.addObserver(this);
    }
    private DemoTanker(Random r) {
        this.r = r;
    }
    public Action senseAndAct(Cell[][] view, long timestep) {
        Action act;
        hasDeviated(view);
        gridCache(view);
        act = possiblyFueling();
        if (act != null) return act;//deliberation
        act = isDisposing();//enter dispose mode
        if (act != null) return act;
        return roam.process(getPosition());//enter roam mode
    }
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Router) updateRouter(arg);
        if (o instanceof Fueling) updateFueling(arg);
        if (o instanceof Process) updateProcess(arg);
    }
    private void gridCache(Cell[][] view) {
        for (int i = 0; i < view.length; i++) {
            for (int j = 0; j < view[i].length; j++) {
                wellPercepts(view, i, j);
                stationPercepts(view, i, j);
            }
        }
    }
    private void wellPercepts(Cell[][] view, int x, int y) {
        if (view[x][y] instanceof Well) {
            int i, j;
            i = x - 20 + vector.getX();
            j = 20 - y + vector.getY();
            dispose.getControlWell().schedule(pos_get(), new coordinateWrapper(i, j), (DefaultCell) view[x][y]);
        }
    }
    private void stationPercepts(Cell[][] view, int x, int y) {
        if (view[x][y] instanceof Station) {
            int i, j;
            i = x - 20 + vector.getX();
            j = 20 - y + vector.getY();
            dispose.getControlStation().schedule(pos_get(), new coordinateWrapper(i, j), (DefaultCell) view[x][y]);
        }
    }
    private boolean exists(ArrayList<Cell> list, Cell ref) {
        for (Cell aList : list) {
            if (aList.getter() == ref.getter()) {
                return true;
            }
        }
        return false;
    }
    private coordinateWrapper pos_get() {
        return vector;
    }
    private boolean idle() {
        return (!stations.isEmpty() && !wells.isEmpty());
    }
    private Action possiblyFueling() {
        Action satisfyRoutine = routine.update(getFuelLevel(), new xyPos(getPosition(), pos_get()));
        if (satisfyRoutine != null) {
            roam.abandon(getPosition());
        }
        return satisfyRoutine;
    }
    private void updateFueling(Object arg) {
        vector = new coordinateWrapper(0, 0);
        deviate = true;
    }
    private Action isDisposing() {
        Action disposeGoal = dispose.update(new xyPos(getPosition(), pos_get()), getWasteLevel(), getFuelLevel());
        //if (getFuelLevel() < 25){
        //    roam.abandon(getPosition());
        //}
        if (disposeGoal != null) {
            roam.abandon(getPosition());
        }
        return disposeGoal;
    }
    private void updateProcess(Object arg) {
        vector = (coordinateWrapper) arg;
        deviate = true;
    }
    //deviation point for return
    private void updateRouter(Object arg) {
        coordinateWrapper newVectorP;
        routArg = (int) arg;
        newVectorP = transposition.transform(routArg, vector);
        vector = newVectorP;
        deviate = true;
    }
    private void hasDeviated(Cell[][] view) {
        if (!deviate) return;
        gridCache(view);
        deviate = false;
    }
}
class Perform {
    private final DefaultCell cell;
    private final Action action;
    public Perform(DefaultCell cell, Action action) {
        this.cell = cell;
        this.action = action;
    }
    public DefaultCell getCell() {
        return cell;
    }
    public Action getAction() {
        return action;
    }
}
class Proximity {
    public static int howfar(coordinateWrapper foo, coordinateWrapper ins) {
        return Math.max(Math.abs(ins.getX() - foo.getX()), Math.abs(ins.getY() - foo.getY()));
    }
}
class xyPos {
    //convert primitive into object
    private Point point;
    private coordinateWrapper position;

    public xyPos(Point point, coordinateWrapper position) {
        this.setter(point);
        this.pos_set(position);
    }
    public Point getter() {
        return point;
    }
    private void setter(Point point) {
        this.point = point;
    }
    public coordinateWrapper pos_get() {
        return position;
    }
    private void pos_set(coordinateWrapper position) {
        this.position = position;
    }
}
