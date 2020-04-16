package uk.ac.nott.cs.g53dia.demo;
//import com.hybhub.util.concurrent.ConcurrentSetBlockingQueue;
import uk.ac.nott.cs.g53dia.library.*;
import java.util.*;
//import java.util.concurrent.BlockingQueue;

abstract class UniqueQueue<T> implements Queue<T> {
    private final Queue<T> queue = new LinkedList<T>();
    public final Set<T> set = new HashSet<T>();
    public boolean add(T t) {
        // Only add element if does not contain the specified element.
        if (set.add(t)) {
            queue.add(t);
        }
        return true;
    }
    public T remove() throws NoSuchElementException {
        T ret = queue.remove();
        set.remove(ret);
        return ret;
    }
}
class Process extends Observable implements Observer {
    private static final int valW = 0;
    private static final int valS = 1;
    private final ArrayList<Refresh> priorities;
    private Queue<Perform> aegis;

    public Process(Point gas) {
        aegis = new LinkedList<>();
        priorities = new ArrayList<>(2);
        priorities.add(valW, new ControlWell());
        priorities.add(valS, new ControlStation());
    }
    public Action update(xyPos xy, int waste, int fuel) {
        Topography(xy.pos_get());
        return modality(waste, xy, fuel);
    }
    private void Topography(coordinateWrapper position) {
        for (Refresh routines : priorities) routines.regen(position);
    }
    private boolean range(coordinateWrapper insPoint, coordinateWrapper position, int fuel) {
        int venture = Proximity.howfar(insPoint, position);
        int error_margin = 5;
        int ventureServices = Proximity.howfar(insPoint, new coordinateWrapper(0, 0));
        int total = venture + ventureServices;
        return (total < (fuel - error_margin));
    }
    public ControlStation getControlStation() {
        return (ControlStation) priorities.get(valS);
    }
    public ControlWell getControlWell() {
        return (ControlWell) priorities.get(valW);
    }
    @Override
    public void update(Observable o, Object arg) {
        aegis.clear();
    }
    private Action modality(int waste, xyPos xy, int fuel) {
        if (!idleCheck()) return null;
        Perform foo;
        foo = ScheduleLoads(fuel, xy.pos_get(), waste).peek();
        foo = (foo != null) ? foo : ScheduleDisposals(waste, xy.pos_get(), fuel).peek();
        foo = (done(xy.getter())) ? aegis.poll() : foo;
        return (foo != null) ? foo.getAction() : null;
    }
    private boolean done(Point OurPosition) {
        if (aegis.peek() == null) return false;
        if (aegis.peek().getCell() instanceof Well) {
            if (OurPosition.equals(getControlWell().getQueue().peek().returnInstance().getWell().getter())) {
                setChanged();
                notifyObservers(getControlStation().getQueue().peek().pos_get());
                aegis.poll();
                getControlWell().getQueue().poll();
                return true;
            }
        } else if (aegis.peek().getCell() instanceof Station) {
            if (OurPosition.equals(getControlStation().getQueue().peek().returnInstance().getStation().getter())) {
                setChanged();
                notifyObservers(getControlStation().getQueue().peek().pos_get());
                aegis.poll();
                getControlStation().getQueue().remove();
                return true;
            }
        }
        return false;
    }
    private boolean idleCheck() {
        return (getControlWell().getQueue().size() >= 1 & getControlStation().getQueue().size() >= 1);
    }
    private Queue<Perform> ScheduleDisposals(int waste, coordinateWrapper position, int fuel) {
        Queue<Perform> WastePending;
        if (aegis.peek() == null & waste != 0 & range(getControlWell().getQueue().peek().pos_get(), position, fuel)) {
            Well best;
            WastePending = new LinkedList<>();
            Set<Perform> set = new HashSet<>();
            best = getControlWell().getQueue().peek().returnInstance().getWell();
            WastePending.offer(new Perform(best, new MoveTowardsAction(best.getter())));
            WastePending.offer(new Perform(best, new DisposeWasteAction()));
            aegis = WastePending;
        }
        return aegis;
    }
    private Queue<Perform> ScheduleLoads(int fuel, coordinateWrapper position, int waste) {
        if (aegis.peek() == null & waste == 0) optimiseLoad(position, fuel);
        return aegis;
    }
    private int count = 0;

    private void optimiseLoad(coordinateWrapper position, int fuel) {
        //final BlockingQueue<Perform> disSched = new ConcurrentSetBlockingQueue<>(25);
        Queue<Perform> disSched = new LinkedList<Perform>();
        Queue<Perform> disSchedCopy = new LinkedList<Perform>();
        Station optimal;
        if (count == 0) {
            //optimal = getControlStation().getQueue().peek().returnInstance().getStation();
            while (!getControlStation().getQueue().isEmpty() & aegis.peek() == null) {
                optimal = getControlStation().getQueue().peek().returnInstance().getStation();
                if (range(getControlStation().getQueue().peek().pos_get(), position, fuel)) {
                    disSched.offer(new Perform(optimal, new MoveTowardsAction(optimal.getter())));
                    disSched.offer(new Perform(optimal, new LoadWasteAction(optimal.getTask())));
                    aegis = disSched;
                    count = 1;
                } else {
                    disSchedCopy.add(disSched.peek());
                    getControlStation().getQueue().poll(); //next best
                    count = 1;
                }
            }
        }
        else {
            if (disSched.peek() == disSchedCopy.peek()){
                optimal = getControlStation().getQueue().peek().returnInstance().getStation();
                    disSched.poll();
                    getControlStation().getQueue().poll(); //next best
                    //disSchedCopy.poll();

                    count = 0;

            }
        }
    }
}

