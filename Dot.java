/**
 * A very simple implementation of a class implementing the Point2D interface
 * Called it a "Dot" to distinguish from Point
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 */
public class Dot implements Point2D {
	private double x, y;

	public Dot(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String toString() {
		return "("+x+","+y+")";
	}
}
