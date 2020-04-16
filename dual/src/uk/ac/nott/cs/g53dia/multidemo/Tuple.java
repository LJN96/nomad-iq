
package uk.ac.nott.cs.g53dia.multidemo;

// Used for storing locations once calculated
public class Tuple {
	private int x;
	private int y;
	public Tuple(int x, int y) {
		this.setX(x);
		this.setY(y);
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tuple)) return false;

		Tuple t = (Tuple) obj;
		
		return (getX() == t.getX() && getY() == t.getY());
	}

}
