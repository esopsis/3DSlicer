public class Utilities {
	public double simpAngle(double angle) {
		if (angle < 0) {
			return angle + 2 * Math.PI;
		}
		else if (angle > 2 * Math.PI) {
			return angle - 2 * Math.PI;
		}
		else{
			return angle;
		}
	}
}