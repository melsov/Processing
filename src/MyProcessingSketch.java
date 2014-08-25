import drawbot.DrawBot;
import processing.core.*;


public class MyProcessingSketch extends PApplet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void setup() 
	{
		size(200,200);
		background(122);
	}
	
	public void draw() 
	{
		stroke(255);
		if (mousePressed) {
			line(mouseX,mouseY,pmouseX,pmouseY);
		}
	}
	  
	public static void main(String args[]) 
	{
		PApplet.main(new String[] { "--present", "MyProcessingSketch" });
	}

}
