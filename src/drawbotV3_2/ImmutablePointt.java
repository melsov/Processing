package drawbotV3_2;

import drawbotV3_2.ImmutablePointt;
import drawbotV3_2.Pointt;


public class ImmutablePointt extends Pointt {

	private final float x;
	private final float y;
	
	private ImmutablePointt(float xx, float yy) {
		x = xx; y = yy;
	}
	private ImmutablePointt(double xx, double yy) { this ((float) xx, (float) yy); }
	private ImmutablePointt() { this(0,0); }
	
	public static ImmutablePointt ImmutablePointtZero() {
		return new ImmutablePointt();
	}

	public static ImmutablePointt ImmutablePointtMake(float xx, float yy) {
		return new ImmutablePointt(xx,yy);
	}
	public static ImmutablePointt ImmutablePointtMake(double xx, double yy) {
		return new ImmutablePointt(xx,yy);
	}
	public float getX() {return x; }
	public float getY() {return y; }
	
	public String toString() {
		return "Immutble Point: x: " + x + " y: " + y;
	}

}
