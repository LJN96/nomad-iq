package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Task;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;
import static uk.ac.nott.cs.g53dia.multidemo.IntelligentTanker.Fll;

public class AgentUtil extends Tanker {
	public static Tuple pooh;

	@Override
	public int getFuelLevel() {
		return super.getFuelLevel();
	}
	public static int calculateDistanceToLocation(Tuple curr, Tuple entity) {
		//System.out.println("calculateDistanceToLocation");
		return Math.max(Math.abs(entity.getX()-curr.getX()), Math.abs(entity.getY()-curr.getY()));

	}
	public static int calculateDistanceToCentralFuel(Tuple curr, Tuple entity) {
		//System.out.println("calculateDistanceToLocation");
		return Math.max(Math.abs(entity.getX()-curr.getX()), Math.abs(entity.getY()-curr.getY()));
	}
	public static boolean willReachWithCurrentFuel(Tuple entityPoint, Tuple position, int fuel) {
		int distToEntity = AgentUtil.calculateDistanceToLocation(entityPoint, position);
		int distFromEntityToFuelStation = AgentUtil.calculateDistanceToLocation(entityPoint, new Tuple(0,0));
		int total = distToEntity + distFromEntityToFuelStation;
		System.out.println("used willReachWithCurrentFuel calc");
		System.out.println("&&&FUEL PUMP= "+ Fll);
		pooh = position;
		System.out.println(pooh + " POOH 1");
		if (total + 10 < Fll){return true;}return false;}

	public static boolean willReachWithCurrentFuel2(Tuple entityPoint, Tuple position, int fuel) {
		int distToEntity = AgentUtil.calculateDistanceToLocation(entityPoint, position);
		int distFromEntityToFuelStation = AgentUtil.calculateDistanceToLocation(entityPoint, new Tuple(0,0));
		int total = distToEntity + distFromEntityToFuelStation;
		int distToFuelStation = AgentUtil.calculateDistanceToCentralFuel(position, entityPoint);
		System.out.println("used willReachWithCurrentFuel 2 calc");
		System.out.println("disToFuelStation = " + distToFuelStation);
		System.out.println("total is" + total);
		System.out.println("fuel is" + fuel);
		System.out.println("position is =" + position);
		System.out.println("entity position is =" + entityPoint);
		pooh = position;
		System.out.println(pooh + "POOH 2");
		if (total + 10 < fuel){return true;}else return false;}

	public static int TOTES(Tuple entityPoint, Tuple position, int fuel){
		int distToEntity = AgentUtil.calculateDistanceToLocation(entityPoint, position);
		int distFromEntityToFuelStation = AgentUtil.calculateDistanceToLocation(entityPoint, new Tuple(0,0));
		return distToEntity + distFromEntityToFuelStation;
	}
	public static boolean willReachWithWasteRefill(Tuple taskLocation, Tuple wellLocation, Tuple agentLocation, int fuel) {
		int distToTask = AgentUtil.calculateDistanceToLocation(taskLocation, agentLocation);
		int distFromTasktoWell = AgentUtil.calculateDistanceToLocation(taskLocation, wellLocation);
		int distFromWellToFuel = AgentUtil.calculateDistanceToLocation(wellLocation, new Tuple(0,0));
		int total = distToTask + distFromTasktoWell + distFromWellToFuel;
		System.out.println("used willReachWithWasteRefill calc");
		return (total < (fuel -2));
	}
	public static boolean taskEquals(Task t1, Task t2) {
		if (t1 == null || t2 == null) return false;
		
		return t1.equals(t2);
	}
	@Override
	public Action senseAndAct(Cell[][] view, long timestep) {
		return null;
	}
}

