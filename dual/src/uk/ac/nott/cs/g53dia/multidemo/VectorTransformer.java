
package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.MoveAction;


// Static class used in working out where the agent is since we can't see Point we have to transform based on known movement from explorer and known locations
public class VectorTransformer {
	
	public static Tuple transform(int newDirection, Tuple current) {
		Tuple transform = VectorTransformer.getTransformationVector(newDirection);
		
		return new Tuple(current.getX() + transform.getX(), current.getY() + transform.getY());
	}
	
	private static Tuple getTransformationVector(int newDirection) {
		Tuple transformation;
		
		switch(newDirection) {
		case MoveAction.NORTH:
			transformation = new Tuple(0, 1);
			break;
		case MoveAction.SOUTH:
			transformation = new Tuple(0, -1);
			break;
		case MoveAction.WEST:
			transformation = new Tuple(-1, 0);
			break;
		case MoveAction.EAST:
			transformation = new Tuple(1, 0);
			break;
		case MoveAction.NORTHEAST:
			transformation = new Tuple(1, 1);
			break;
		case MoveAction.SOUTHEAST:
			transformation = new Tuple(1, -1);
			break;
		case MoveAction.SOUTHWEST:
			transformation = new Tuple(-1, -1);
			break;
		case MoveAction.NORTHWEST:
			transformation = new Tuple(-1, +1);
			break;
		default:									// Identity Matrix
			transformation = new Tuple(0, 0);
			break;
		}
		
		return transformation;
	}

}
