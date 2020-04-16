package uk.ac.nott.cs.g53dia.multidemo;
import java.util.Observable;
import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.multilibrary.RefuelAction;
import uk.ac.nott.cs.g53dia.multilibrary.Point;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;
// Class where we check our fuel level
public class MaintenanceGoals extends Observable {
	private final LocationPoint fuelPumpLocation;
	static int fuelPumpLevel;
	static int theDff;
	static LocationPoint fpl;

	public MaintenanceGoals(LocationPoint fuelPumpLocation) {
		this.fuelPumpLocation = fuelPumpLocation;
		fpl = fuelPumpLocation;
		}
	// Will return an action if maintenance is required, else null and we will continue exploring
	public Action update(int fuelLevel, LocationPoint lp, int dff) {
		System.out.println(fuelPumpLevel);
		fuelPumpLevel = fuelLevel;
		return checkFuelConstraints(fuelLevel, lp, dff);
	}
	// Will return an action if we are running out of fuel
	private Action checkFuelConstraints(int fuel, LocationPoint lp, int dff) {
		Action action = null;
		/*if (AgentUtil.calculateDistanceToLocation(lp.getLocation(), fuelPumpLocation.getLocation()) + 10 >= fuel /*|| shouldOpportunisticallyRefuel(lp.getLocation(), fuel)*///) //{
		if (AgentUtil.calculateDistanceToCentralFuel(lp.getLocation(), fuelPumpLocation.getLocation()) + 10 > fuel){
			dff = AgentUtil.calculateDistanceToLocation(lp.getLocation(), fuelPumpLocation.getLocation());
			theDff = dff;
			//System.out.println("fp = " + fuelPumpLocation.getLocation() + " tfp = " + Tanker.FUEL_PUMP_LOCATION);
			System.out.println("location point " + lp.getLocation());

			if (fuelPumpLocation.getPoint().equals(lp.getPoint())) {
				action = new RefuelAction();
				System.out.println("tried to refuel");
				this.setChanged();
				this.notifyObservers();
			} else {
				action = new MoveTowardsAction(fuelPumpLocation.getPoint());
				System.out.println("tried to get to fuel");
			}
		}
		return action;
	}
	// If we are in 5 moves of the fuel station & we have left than a 3rd of tank left
	private boolean shouldOpportunisticallyRefuel(Tuple position, int fuel) {
		return(AgentUtil.calculateDistanceToLocation(position, fuelPumpLocation.getLocation()) <= 5 & fuel < 100 / 2);
	}
}

