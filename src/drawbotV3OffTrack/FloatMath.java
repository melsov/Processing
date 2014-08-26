package drawbotV3OffTrack;

import static java.lang.Math.*;

public class FloatMath {

	public static boolean Equal(double a, double b) {
		return Equal(a,b,.01);
	}
	public static boolean Equal(float a, float b) {
		return Equal(a,b,.01);
	}
	public static boolean Equal(float a, float b, float epsilon) {
		return abs(a - b) < epsilon;
	}
	
	public static boolean Equal(double a, double b, double epsilon) {
		return abs(a - b) < epsilon;
	}
}
