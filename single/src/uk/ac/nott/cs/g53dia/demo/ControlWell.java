package uk.ac.nott.cs.g53dia.demo;
import uk.ac.nott.cs.g53dia.library.DefaultCell;
import uk.ac.nott.cs.g53dia.library.Well;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ControlWell implements Refresh {
    private final PriorityQueue<MapWell> pq;
    public ControlWell() {
        Comparator<MapWell> comparator = new ClosestWell();
        pq = new PriorityQueue<>(25, comparator);
    }
    public void regen(coordinateWrapper gps) {
        PriorityQueue<MapWell> auxiliary = new PriorityQueue<>(pq);
        pq.clear();
        while (!auxiliary.isEmpty()) {
            MapWell quantum = auxiliary.poll();
            quantum.returnInstance().setProximity(Proximity.howfar(gps, quantum.pos_get()));
            pq.add(quantum);
        }
    }
    @Override
    public void schedule(coordinateWrapper gps, coordinateWrapper insPos, DefaultCell ins) {
        DistanceWell DistanceWell = new DistanceWell((Well) ins, Proximity.howfar(gps, insPos));
        MapWell locator = new MapWell(insPos, DistanceWell);
        if (pq.contains(locator)) return;
        pq.add(locator);
    }
    public PriorityQueue<MapWell> getQueue() {
        return pq;
    }
}
class DistanceWell {
    private final Well ins;
    private int int_proximity;
    public DistanceWell(Well ins, int int_proximity) {
        this.ins = ins;
        this.int_proximity = int_proximity;
    }
    public Well getWell() {
        return ins;
    }
    public int getProximity() {
        return int_proximity;
    }
    public void setProximity(int int_proximity) {
        this.int_proximity = int_proximity;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DistanceWell)) return false;
        DistanceWell quantum = (DistanceWell) obj;
        if (this.ins.getter().equals(quantum.ins.getter())) return true;
        return false;
    }
}
class ClosestWell implements Comparator<MapWell> {
    public ClosestWell() {
    }
    @Override
    public int compare(MapWell quantum1, MapWell quantum2) {
        return Integer.compare(quantum1.returnInstance().getProximity(), quantum2.returnInstance().getProximity());
    }
}
class MapWell {
    private coordinateWrapper position;
    private DistanceWell insEntry;
    public MapWell(coordinateWrapper position, DistanceWell insEntry) {
        this.pos_set(position);
        this.giveInstance(insEntry);
    }
    public coordinateWrapper pos_get() {
        return position;
    }
    private void pos_set(coordinateWrapper position) {
        this.position = position;
    }
    public DistanceWell returnInstance() {
        return insEntry;
    }
    private void giveInstance(DistanceWell insEntry) {
        this.insEntry = insEntry;
    }
    public boolean equals(Object obj) {
        if (!(obj instanceof MapWell)) return false;
        MapWell locator = (MapWell) obj;
        return (locator.pos_get().equals(this.pos_get()));
    }
}