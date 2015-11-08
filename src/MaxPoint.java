public class MaxPoint {
	double myXCo;
	double myYCo;
	double myHeightFactor;
	double xStart;
	double yStart;
	double myAngle;
	double myRadius;
	public MaxPoint(double xCo, double yCo, double heightFactor) {
		myXCo = xStart = xCo;
		myYCo = yStart = yCo;
		myHeightFactor = heightFactor;
		myAngle = 0;
		myRadius = 0;
	}
	public double getX() {
		return myXCo;
	}
	public double getY() {
		return myYCo;
	}
	public double getHeightFactor() {
		return myHeightFactor;
	}
	public double getXStart() {
		return xStart;
	}
	public double getYStart() {
		return yStart;
	}
	public void setX(double value) {
		myXCo = value;
	}
	public void setY(double value) {
		myYCo = value;
	}
	public void incX(double value) {
		myXCo += value;
	}
	public void incY(double value) {
		myYCo += value;
	}
	public void decrX(double value) {
		myXCo -= value;
	}
	public void decrY(double value) {
		myYCo -= value;
	}
	public void setAngle(double angle) {
		myAngle = angle;
	}
	public double getAngle() {
		return myAngle;
	}
	public void setRadius(double radius) {
		myRadius = radius;
	}
	public double getRadius() {
		return myRadius;
	}
	public void incAngle(double value) {
		myAngle += value;
	}
	public void decrAngle(double value) {
		myAngle -= value;
	}
}