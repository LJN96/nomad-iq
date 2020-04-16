/*package uk.ac.nott.cs.g53dia.multidemo;
import uk.ac.nott.cs.g53dia.multilibrary.*;
import java.util.*;
import java.util.Arrays;
import java.nio.file.Files;


public class NewFuelClass extends Tanker {
    private int[] fuelStationPos = new int[]{0, 0};
    private int exploreStep = 0;
    private int tankerXPos = 0;
    private int tankerYPos = 0;
    private ArrayList<int[]> directionsList = new ArrayList<int[]>();


    private Action getFuel(Cell[][] view) {
        if (getCurrentCell(view) instanceof FuelPump) {
            //should never change the x and y values - included for peace of mind.
            tankerXPos = 0;
            tankerYPos = 0;
            return new RefuelAction();
        } else {
            return moveToPos(view, fuelStationPos);
        }
    }

    private int isInList(ArrayList<int[]> arrayList, int[] intArray) {
        //This function is never needed to find a second instance so returning from the for loop works fine.
        for (int i = 0; i < arrayList.size(); i++) {
            if (Arrays.equals(arrayList.get(i), intArray)) {
                return i;
            }
        }
        return -1;
    }

    private Action moveToPos(Cell[][] view, int[] pos) {
        int horiChange = (pos[0] - tankerXPos);
        int vertChange = (pos[1] - tankerYPos);
        int horiMove;
        int vertMove;
        //horiMove and vertMove could be used to update tanker x and y positions rather than updateTankerPosition
        if (horiChange > 0) {
            horiMove = 1;
        } else if (horiChange < 0) {
            horiMove = -1;
        } else {
            horiMove = 0;
        }
        if (vertChange > 0) {
            vertMove = 1;
        } else if (vertChange < 0) {
            vertMove = -1;
        } else {
            vertMove = 0;
        }
        int[] movementChange = {horiMove, vertMove};
        int movementIndex = isInList(directionsList, movementChange);
        if (movementIndex >= 0) {
            int fuelLeft = getFuelLevel() - Math.max(Math.abs(horiChange), Math.abs(vertChange));
            int distanceToFuel = Math.max(Math.abs(pos[0]), Math.abs(pos[1]));
            if (fuelLeft < distanceToFuel) {
                return getFuel(view);
            } else {
                return moveInDirection(movementIndex);
            }
        } else {
            //This should never occur, added during testing
            return moveInDirection(0);
        }
    }

}*/