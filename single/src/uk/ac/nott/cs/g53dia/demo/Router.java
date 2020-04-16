package uk.ac.nott.cs.g53dia.demo;
import uk.ac.nott.cs.g53dia.library.Action;
import uk.ac.nott.cs.g53dia.library.MoveAction;
import uk.ac.nott.cs.g53dia.library.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.library.Point;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

class Router extends Observable { //tanker rout actions
    private boolean started;
    private Queue<Number> routSchedule;
    private Point marker;
    public Router() {
        routSchedule = new LinkedList<>();
        marker = null;
    }
    public Action process(Point gps) { //process roaming behaviour
        Action move;
        if (marker != null) {
            setChanged();
            notifyObservers(-1);
            move = labyrinth(gps);
            //if (move != null) return move;
        }
        if (routSchedule.isEmpty()) {
            routSchedule = pattern(); //again to roam
        }
        setChanged();
        notifyObservers(routSchedule.peek());
        return new MoveAction(routSchedule.poll().intValue());
    }
    public void abandon(Point marker) {
        if (marker != null) {
            this.marker = marker; //if still active break roam sequence
        }
    }
    private Action labyrinth(Point gps) { //retraces back to original position
        Action move = null;
        if (gps.equals(marker)) { //break completed request
            marker = null;
        } else {
            move = new MoveTowardsAction(marker);
        }
        return move;
    }
    public Queue<Number> pattern() {
        Queue<Number> queue = new LinkedList<>();
        int clicks = 10;
        if (!started) {
            for (int i = 0; i < clicks; i++) {
                queue.add(MoveAction.NORTHWEST);
                started = true;
            }
        }
        new MoveTowardsAction(marker);
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.NORTHEAST);
        }
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.EAST);
        }
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.SOUTHEAST);
        }
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.SOUTH);
        }
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.SOUTHWEST);
        }
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.WEST);
        }
        for (int i = 0; i < clicks; i++) {
            queue.add(MoveAction.NORTHWEST);
        }
        return queue;
    }
}
class transposition {
    public static coordinateWrapper transform(int next, coordinateWrapper previous) {
        coordinateWrapper transform = transposition.vec(next);
        return new coordinateWrapper(previous.getX() + transform.getX(), previous.getY() + transform.getY());
    }
    private static coordinateWrapper vec(int next) {
        coordinateWrapper trans;
        switch (next) {
            case MoveAction.NORTH:
                trans = new coordinateWrapper(0, 1);
                break;
            case MoveAction.SOUTH:
                trans = new coordinateWrapper(0, -1);
                break;
            case MoveAction.WEST:
                trans = new coordinateWrapper(-1, 0);
                break;
            case MoveAction.EAST:
                trans = new coordinateWrapper(1, 0);
                break;
            case MoveAction.NORTHEAST:
                trans = new coordinateWrapper(1, 1);
                break;
            case MoveAction.SOUTHEAST:
                trans = new coordinateWrapper(1, -1);
                break;
            case MoveAction.SOUTHWEST:
                trans = new coordinateWrapper(-1, -1);
                break;
            case MoveAction.NORTHWEST:
                trans = new coordinateWrapper(-1, +1);
                break;
            default:
                trans = new coordinateWrapper(0, 0);
                break;
        }
        return trans;
    }
}
class coordinateWrapper {
    private int x;
    private int y;
    public coordinateWrapper(int x, int y) {
        this.setX(x);
        this.setY(y);
    }
    public int getX() {
        return x;
    }
    private void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    private void setY(int y) {
        this.y = y;
    }
    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof coordinateWrapper)) return false;
        coordinateWrapper t = (coordinateWrapper) obj;
        return (getX() == t.getX() && getY() == t.getY());
    }
}
