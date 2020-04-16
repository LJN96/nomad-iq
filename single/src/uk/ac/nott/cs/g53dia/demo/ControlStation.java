package uk.ac.nott.cs.g53dia.demo;
import uk.ac.nott.cs.g53dia.library.DefaultCell;
import uk.ac.nott.cs.g53dia.library.Station;
import java.util.*;

public class ControlStation implements Refresh {
    private final PriorityQueue<MapStation> pq;
    private Comparator<MapStation> comp;
    public ControlStation() {
        Comparator<MapStation> comp = new best();
        pq = new PriorityQueue<>(50, comp);
    }
    public void schedule(coordinateWrapper gps, coordinateWrapper insPos, DefaultCell ins) {//schedule collections
        Station station = (Station) ins;
        if (station.getTask() == null) return;
        distStation distStation = new distStation((Station) ins, Proximity.howfar(gps, insPos));
        MapStation locator = new MapStation(insPos, distStation);
        if (pq.contains(locator)) return;
        pq.add(locator);
    }
    @Override
    public void regen(coordinateWrapper gps) { //regenerate queue
        PriorityQueue<MapStation> auxiliary = new PriorityQueue<>(pq);
        pq.clear();
        while (!auxiliary.isEmpty()) {
            MapStation foo = auxiliary.poll();
            foo.returnInstance().setProximity(Proximity.howfar(gps, foo.pos_get()));
            pq.add(foo); //reassign
        }
    }
    public PriorityQueue<MapStation> getQueue() {
        return pq;
    }
}
class distStation {
    private Station station;
    private double rule;
    private int int_proximity;
    public distStation(Station station, int int_proximity) {
        this.station = station;
        preference(int_proximity);
    }
    private void preference(int relative_proximity) {
        rule = (relative_proximity != 0) ? station.getTask().getWasteRemaining() * relative_proximity : rule;
    }
    public Station getStation() {
        return station;
    }
    public int getProximity() {
        return int_proximity;
    }
    public void setProximity(int int_proximity) {
        this.int_proximity = int_proximity;
        preference(int_proximity);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof distStation)) return false;
        distStation foo = (distStation) obj;
        if (!this.station.getter().equals(foo.getStation().getter())) return false;
        if (!foo.getStation().getTask().equals(this.station.getTask())) foo.station = this.station;
        return true;
    }
    public double apply() {
        return rule;
    }
}
class best implements Comparator<MapStation> {
    public best() {
    }
    @Override
    public int compare(MapStation bar, MapStation foo2) {
        if (bar.returnInstance().getProximity() > foo2.returnInstance().apply()) return 1;
        if (bar.returnInstance().getProximity() > foo2.returnInstance().apply()) return -1;
        return 0;
    }
}
class MapStation {
    private coordinateWrapper position;
    private distStation instance;
    public MapStation(coordinateWrapper position, distStation instance) {
        this.pos_set(position);
        this.giveInstance(instance);
    }
    public coordinateWrapper pos_get() {
        return position;
    }
    private void pos_set(coordinateWrapper position) {
        this.position = position;
    }
    public distStation returnInstance() {
        return instance;
    }
    private void giveInstance(distStation instance) {
        this.instance = instance;
    }
    public boolean equals(Object obj) {
        if (!(obj instanceof MapStation)) return false;
        MapStation locator = (MapStation) obj;
        return (locator.pos_get().equals(this.pos_get()));
    }
}
