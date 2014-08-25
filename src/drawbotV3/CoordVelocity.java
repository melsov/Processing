package drawbotV3;

import drawbotV3.Pointt;

public class CoordVelocity {

	private Pointt coord;
	private Pointt velocity;
	
	public CoordVelocity() {
		coord = new Pointt();
		velocity = new Pointt();
	}
	public CoordVelocity (Pointt _coord, Pointt _vel) {
		coord = _coord; velocity = _vel;
	}
	public Pointt getCoord() { return coord.copy(); }
	public Pointt getVelocity() { return velocity.copy(); }
}
