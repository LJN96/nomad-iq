
package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.Point;

// Wrapper class that makes paramater passing easier to read
public class LocationPoint {
	private Point point;
	private Tuple location;
	
	public LocationPoint(Point point, Tuple location) {
		this.setPoint(point);
		this.setLocation(location);
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Tuple getLocation() {
		return location;
	}

	public void setLocation(Tuple location) {
		this.location = location;
	}

}
