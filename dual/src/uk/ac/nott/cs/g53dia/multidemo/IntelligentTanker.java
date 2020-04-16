package uk.ac.nott.cs.g53dia.multidemo;
import java.util.*;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import uk.ac.nott.cs.g53dia.multilibrary.MoveTowardsAction;
import static uk.ac.nott.cs.g53dia.multidemo.MaintenanceGoals.fpl;
import static uk.ac.nott.cs.g53dia.multidemo.MaintenanceGoals.fuelPumpLevel;
import static uk.ac.nott.cs.g53dia.multidemo.SAMSTaskAllocator.attempted;
import uk.ac.nott.cs.g53dia.multidemo.SAMSKnowledge;

public class IntelligentTanker extends Tanker implements Observer {
	private AgentExplorer explorer;
	//private MaintenanceGoals maintenance;
	private DeliveryGoals delivery;
	private Tuple movementVector;
	private AgentServices services;
	private int explorerHeading;
	private boolean forcedRemap; // once at a known location we can reference the environment
	private int[] fuelStationPos = new int[]{0, 0};
	private int exploreStep = 0;
	private int tankerXPos = 0;
	private int tankerYPos = 0;
	private ArrayList<int[]> directionsList = new ArrayList<int[]>();
	private Action signalF;
	private boolean fG;
	private final Point fuelPumpLocation = FUEL_PUMP_LOCATION;
	public static int Fll;

	public IntelligentTanker(boolean opposite) {
		this(new Random());
		services = SAMS.getInstance();
		explorer = new AgentExplorer(opposite);
		//maintenance = new MaintenanceGoals(new LocationPoint(FUEL_PUMP_LOCATION, new Tuple(0, 0)));
		delivery = new DeliveryGoals(services, hashCode());
		movementVector = new Tuple(0, 0);
		explorerHeading = -1;
		forcedRemap = false;
		services.register(this);                                    // receive messages from manager
		//maintenance.addObserver(this);                                // receive maintenance messages
		explorer.addObserver(this);                                    // receive messages from explorer (allow us to calculate movement)
	}
	public IntelligentTanker(Random r) {
		this.r = r;
	}

	public Action senseAndAct(Cell[][] view, long timestep) {
		Action goal = null;
		if (getFuelLevel() < 55){fG = true;}
		System.out.println("fuel= "+ getFuelLevel());
		Fll = getFuelLevel();
		checkForcedRemap(view);
		goal = fuelGoal(view);
		if (goal != null) {
			System.out.println("maintenance");
			return goal;
		}
		goal = checkDisposeGoal();
		if (goal != null) return goal;
		System.out.println("dispose");
		return explorer.dequeue(getPosition());
	}
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof AgentExplorer) handleExplorerUpdate(arg);
		if (o instanceof AgentServices) handleServicesUpdate(arg);
		//if (o instanceof MaintenanceGoals) handleMaintenanceUpdate(arg);
	}
	// Checks if a delivery goal is generated on tick
	private Action checkDisposeGoal() {
		Action deliveryGoal = delivery.update(new LocationPoint(getPosition(), getLocation()), getWasteLevel());
		if(deliveryGoal != null) {
			explorer.interupt(getPosition()); }
		return deliveryGoal;
	}
	private Action fuelGoal(Cell[][] view) {
		if (fG == true) {
			//getFuel(view);
			Action fuelGoal = getFuel(view);
			explorer.interupt(getPosition());
			delivery.clearQueue();
			resignTask();
			return fuelGoal; }
		else{return null;}}

	private Action getFuel(Cell[][] view) {
		Action action = null;
		if ((getFuelLevel() < 75) && getCurrentCell(view) instanceof FuelPump) {
			tankerXPos = 0;
			tankerYPos = 0;
			fG = false;
			action = new RefuelAction();
		} else {
			action = new MoveTowardsAction(fuelPumpLocation); }
		return action;
	}
	private int isInList(ArrayList<int[]> arrayList, int[] intArray) {
		//This function is never needed to find a second instance so returning from the for loop works fine.
		for (int i = 0; i < arrayList.size(); i++) {
			if (Arrays.equals(arrayList.get(i), intArray)) {
				return i;
			} }return -1;
	}
	private Action moveToPos(Cell[][] view, int[] pos) {
		int horiChange = (pos[0] - tankerXPos);
		int vertChange = (pos[1] - tankerYPos);
		int horiMove;int vertMove;
		//horiMove and vertMove could be used to update tanker x and y positions rather than updateTankerPosition
		if (horiChange > 0) { horiMove = 1; } else if (horiChange < 0) { horiMove = -1; } else { horiMove = 0; }if (vertChange > 0) { vertMove = 1; } else if (vertChange < 0) { vertMove = -1; } else { vertMove = 0; }
		int[] movementChange = {horiMove, vertMove};
		int movementIndex = isInList(directionsList, movementChange);
		if (movementIndex >= 0) {
			int fuelLeft = getFuelLevel() - Math.max(Math.abs(horiChange), Math.abs(vertChange));
			int distanceToFuel = Math.max(Math.abs(pos[0]), Math.abs(pos[1]));
			if (fuelLeft < distanceToFuel) {
				fG = true;
				return getFuel(view);
			} else {
				return moveInDirection(movementIndex);
			}
		} else {
			//This should never occur, added during testing
			return moveInDirection(0);
		}
	}
	private Action moveInDirection(int dir){//added to ensure that tanker position is always updated, could probably be condensed.
		updateTankerPosition(dir);
		return new MoveAction(dir);
	}
	private void updateTankerPosition(int dir){
		switch (dir){
			case 0:
				tankerYPos--;
				break;
			case 1:
				tankerYPos++;
				break;
			case 2:
				tankerXPos++;
				break;
			case 3:
				tankerXPos--;
				break;
			case 4:
				tankerXPos++;
				tankerYPos--;
				break;
			case 5:
				tankerXPos--;
				tankerYPos--;
				break;
			case 6:
				tankerXPos++;
				tankerYPos++;
				break;
			case 7:
				tankerXPos--;
				tankerYPos++;
				break;
		}
	}
	// If we know where we are, submit meta data and map area
	private void checkForcedRemap(Cell[][] view) {
		if(!forcedRemap) return;
		submitMETA();
		mapPOI(view);
		forcedRemap = false;
	}
	// Submit meta to the manager
	private void submitMETA() {
		// Only report correct locations
		if (forcedRemap) {
			METAEntry entry = services.getLastMETASubmission(hashCode());

			if (entry == null) {
				entry = new METAEntry(hashCode(), getLocation(), getWasteLevel(), getFuelLevel());
			} else {
				// Leave task persistent
				entry.setFuel(getFuelLevel());
				entry.setWaste(getWasteLevel());
				entry.setLastKnownLocation(getLocation());
			}
			services.submitMETA(entry);
		}
	}
	// Maps points of interest in our view
	private void mapPOI(Cell[][] view) {
		for(int i = 0; i < view.length; i++) {
			for(int j = 0; j < view[i].length; j++) {
				checkForPOI(view, i, j);
			}
		}
	}
	// Submit points of interest in our view to manager
	private void checkForPOI(Cell[][] view, int x, int y) {
		if(view[x][y] instanceof Well || view[x][y] instanceof Station) {
			int a,b;						// Location coordinates
			a = x-12 + movementVector.getX();
			b = 12-y + movementVector.getY();
			services.submitPOI(new POIEntry(new Tuple(a, b), (DefaultCell) view[x][y]));
		} }
	// Known location, start mapping
	private void handleExplorerUpdate(Object arg) {
		Tuple newVectorPosition;
		explorerHeading = (int) arg;
		newVectorPosition = VectorTransformer.transform(explorerHeading, movementVector);
		movementVector = newVectorPosition;
		forcedRemap = true;
	}
	// Known location, start mapping
	private void handleMaintenanceUpdate(Object arg) {
		movementVector = new Tuple(0,0);
		forcedRemap = true;
	}
	// Services broadcasted message
	private void handleServicesUpdate(Object arg) {
		if (arg instanceof CompletionEntry) {
			CompletionEntry entry = (CompletionEntry) arg;
			// Check message is for us
			if(entry.getHash() == hashCode()) {
				movementVector = entry.getLocation();
				forcedRemap = true;
				// Service manager wants us to resign task
				if(delivery.removedFromTaskQueue(entry)) {
				} } } }
	private void resignTask() {
		METAEntry meta = services.getLastMETASubmission(hashCode());
		meta.setTask(null);
		services.submitMETA(meta);
	}
	// Returns our current location - should only be used at specific locations
	private Tuple getLocation() {
		return movementVector;
	}
}
