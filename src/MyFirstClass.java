import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
 
public class MyFirstClass extends PApplet 
{
	private PImage img;
 
	public void setup () {
		size (396, 396, PGraphicsOpenGL.OPENGL);
		img = loadImage ("demonLink.png");
	}
 
	public void draw () {
		image (img, 0, 0, width, height);
		ellipse (mouseX, mouseY, 20, 20);
	}
	  
	public static void main(String args[]) 
	{
		PApplet.main(new String[] { "--present", "MyProcessingSketch" });
	}
	public void keyPressed() {
		System.out.println("**** GOT KEY ***** in Processing ******");
		if (key == 'h') {
			System.out.println("h");
//			goHomeAndQuit = true;
		}
	}
}