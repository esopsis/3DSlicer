public class Point {
	double myXCo;
	double myYCo;
	double myHeightFactor;
	double xStart;
	double yStart;
	double myAngle;
	double myRadius;
	public Point(double xCo, double yCo) {
		myXCo = xStart = xCo;
		myYCo = yStart = yCo;
	}
	public double getX() {
		return myXCo;
	}
	public double getMapX() {
		double distance = Math.sqrt(myXCo * myXCo + myYCo * myYCo);
		double totalAng = CurveChange.mapAngle + Math.atan2(myYCo, myXCo);
		if (myYCo == 0 && myXCo < 0) {
			totalAng = Math.PI + CurveChange.mapAngle;
		}
		return CurveChange.sliceXPos + distance * Math.cos(totalAng);
	}
	public double getMapY() {
		double distance = Math.sqrt(myXCo * myXCo + myYCo * myYCo);
		double totalAng = CurveChange.mapAngle + Math.atan2(myYCo, myXCo);
		if (myYCo == 0 && myXCo < 0) {
			totalAng = Math.PI + CurveChange.mapAngle;
		}
		return CurveChange.sliceYPos + distance * Math.sin(totalAng);
	}
	public int xForMap() {
		return (int) Math.round(getMapX() / CurveChange.mapPixelToCo + CurveChange.mapSize / 2) - 1;
	}
	public int yForMap() {
		return (int) CurveChange.ySize - ((int) Math.round(getMapY() / CurveChange.mapPixelToCo + CurveChange.mapSize / 2));
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