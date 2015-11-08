public class Tank {
	double myXCo;
	double myYCo;
	double myHeightFactor;
	double xStart;
	double yStart;
	double myAngle;
	double myRadius;
	double angleToYou;
	public Tank(double xCo, double yCo) {
		myXCo = xStart = xCo;
		myYCo = yStart = yCo;
		myAngle = 0;
	}
	public void updateAngleTo() {
		angleToYou = Math.atan2(-myYCo, -myXCo) - myAngle;
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
		updateAngleTo();
	}
	public void setY(double value) {
		myYCo = value;
		updateAngleTo();
	}
	public void incX(double value) {
		myXCo += value;
		updateAngleTo();
	}
	public void incY(double value) {
		myYCo += value;
		updateAngleTo();
	}
	public void decrX(double value) {
		myXCo -= value;
		updateAngleTo();
	}
	public void decrY(double value) {
		myYCo -= value;
		updateAngleTo();
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