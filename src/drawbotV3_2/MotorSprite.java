package drawbotV3_2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import drawbotV3_2.Motor;
import drawbotV3_2.Pointt;

public class MotorSprite 
{
	Motor motor;
	boolean isRightSideMotor;
	
	StringBuilder sb = new StringBuilder();
	
	public MotorSprite(Motor _motor, boolean rightMotor) {
		motor = _motor;
		isRightSideMotor = rightMotor;
	}
	
	public void drawMotor(Graphics2D g, Pointt loc) {
		Ellipse2D.Double gear = new Ellipse2D.Double(loc.x, loc.y, Settings.spoolDiameter, Settings.spoolDiameter);
		g.setPaint(Color.BLUE);
		g.fill(gear);
		Pointt center = loc.plus(new Pointt(Settings.spoolDiameter, Settings.spoolDiameter).multiply(.5));
		double a = motor.totalRotationRadians();
		if (isRightSideMotor) a *= -1;
		Pointt radarLoc = new Pointt(Settings.spoolDiameter * .5 * Math.cos(a), Settings.spoolDiameter * .5 * Math.sin(a));
		radarLoc = radarLoc.plus(center);
		Line2D.Double radar = new Line2D.Double(center.point2D(), radarLoc.point2D());
		g.setPaint(Color.WHITE);
		g.draw(radar);
		
		int pushDownY = 50;
//		sb.delete(0, sb.length());
//		sb.append("dist to target: ");
//		sb.append(FormatFloat.FloatStringFourDecimals((float) motor.distanceToTarget()));
//		drawTextAt(g, loc, sb.toString(), pushDownY );
//		pushDownY +=20;
		
		sb.delete(0, sb.length());
		sb.append("chord length: ");
		sb.append(FormatFloat.FloatStringOneDecimal((float) motor.stringLength()));
		drawTextAt(g, loc, sb.toString(), pushDownY );
		pushDownY +=20;
		
//		drawTextAt(g, loc, "velocity: " + motor.velocity(), pushDownY );
//		sb.delete(0, sb.length());
		
	}
	
	private void drawTextAt(Graphics2D g, Pointt loc, String str, int pushDownY) {
		char[] dd = str.toCharArray();
		Pointt textO = loc.plus(new Pointt(0, pushDownY));
		textO.x = textO.x > 50 ? textO.x - 150 : textO.x;
		g.drawChars(dd, 0, dd.length, (int) textO.x,(int) textO.y);
	}
}
