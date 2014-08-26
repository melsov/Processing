package drawbotV3OffTrack;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import drawbotV3OffTrack.Pointt;

public class Line {
	
	public Pointt a,b;
	
	public Line(Pointt aa, Pointt bb) {
		a = aa; b = bb;
	}
	public Pointt toVector() {
		return b.minus(a);
	}
	
	public Line2D line2D() {
		return new Line2D.Double(a.point2D(), b.point2D());
	}
	public GeneralPath arrow() {
		GeneralPath line = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
		line.moveTo(a.x, a.y);
		line.lineTo(b.x, b.y);
		Pointt v = this.toVector();
		v = v.unitPointt();
		double arrowLen=this.toVector().distance() * .1;
		Pointt diag = v.multiply(-1).plus(new Pointt(v.y, -v.x)).multiply(arrowLen);
		diag = b.plus(diag);
		line.lineTo(diag.x, diag.y);
		Pointt diag2 = v.multiply(-1).plus(new Pointt(-v.y, v.x)).multiply(arrowLen);
		diag2 = b.plus(diag2);
		line.lineTo(diag2.x, diag2.y);
		line.lineTo(b.x, b.y);
		return line;
		
	}
	

}
