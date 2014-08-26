package drawbotV3OffTrack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import drawbotV3OffTrack.Motor;
import drawbotV3OffTrack.Pointt;

public class LineSprite {
	
	Motor motor;
	boolean isRightMotorSprite;
	public LineSprite(Motor _motor, boolean right)
	{
		motor = _motor;
		isRightMotorSprite =right;
	}
	
	public void drawLine(Graphics2D g, Pointt motorPoint, Pointt gondola)
	{
		double stripeLen = 200;
		Pointt dif = gondola.minus(motorPoint);
		
		dif = dif.unitPointt();

		int offset = (int)(motor.totalRotationRadians() * (motor.spoolDiameter)) % (int)(stripeLen);
		Pointt p = dif.multiply((double)offset);
		p = motorPoint.plus(p);

		int count = (int)(Math.abs(offset) / stripeLen) % 2 == 0 ? 1 : 0;

		Line2D.Double line = new Line2D.Double(motorPoint.point2D(), p.point2D());
		drawLine(g, line, count);
		g.draw(line);
		Pointt prev = gondola.copy();
		
		while(p.y < gondola.y && dif.y > 0.0 ) {
			count++;
			prev = p.copy();
			p = p.plus(dif.multiply(stripeLen) );
			p.y = Math.min(p.y, gondola.y);
			if (isRightMotorSprite)
				p.x = Math.max(p.x, gondola.x);
			else 
				p.x = Math.min(p.x, gondola.x);
			line = new Line2D.Double(prev.point2D(), p.point2D());
			
			drawLine(g, line, count);
		}
		
	}
	
	private void drawLine(Graphics2D g, Line2D.Double line, int count)
	{
		Color col;
		if (count % 2 == 0) {
			col = Color.CYAN;
		} else {
			col = Color.ORANGE;
		}
		g.setPaint(col);
		g.draw(line);
	}

}
