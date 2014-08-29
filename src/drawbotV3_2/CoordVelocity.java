package drawbotV3_2;

import drawbotV3_2.Pointt;

public class CoordVelocity {

	private Pointt coord;
	private Pointt velocity;
	public boolean goNowhere;
	
	public CoordVelocity() {
		coord = new Pointt();
		velocity = new Pointt();
	}
	public static CoordVelocity GoNowhereCoordVelocity() {
		CoordVelocity result = new CoordVelocity();
		result.goNowhere = true;
		return result;
	}
	public CoordVelocity (Pointt _coord, Pointt _vel) {
		coord = _coord; velocity = _vel;
	}
	public Pointt getCoord() { return coord.copy(); }
	public Pointt getVelocity() { return velocity.copy(); }
	
	public CoordVelocity multiply(float f ) {
		return new CoordVelocity(coord.multiply(f), velocity.multiply(f));
	}
	@Override
	public String toString() {
		return "CoVeL: coord: " + coord.toString() + " vel: " + velocity.toString();
	}
}
