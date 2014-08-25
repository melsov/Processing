import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.*;
import processing.serial.*;
import drawbotV3.Asserter;
import drawbotV3.BotController;
import drawbotV3.DrawBot;
import drawbotV3.KeyHandling;
import drawbotV3.MotorInstructions;
import drawbotV3.Settings;
	 
 // Graphing sketch
  
 // This program takes ASCII-encoded strings
 // from the serial port at 9600 baud and graphs them. It expects values in the
 // range 0 to 1023, followed by a newline, or newline and carriage return
 
 // Created 20 Apr 2005
 // Updated 18 Jan 2008
 // by Tom Igoe
 // This example code is in the public domain.
	 
public class SSerialCallResponseASCIIV3Experi extends PApplet implements SerialPortEventListener, KeyHandling {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Serial myPort;                  // The serial port
	private DrawBot drawBot = null; // new DrawBot();
	private BotController botController;
	
	private int artificialDelay = 0;
	private int DELAYTIMEFRAMES = 10;
	
	private boolean nudgeMode = false;
	
	private MotorInstructions nudgeInstructions = new MotorInstructions();
	private MotorInstructions goAbsZeroInstructions = new MotorInstructions();
	
	private boolean madeFirstContact = false;

	public void setup() {
		botController = null; // drawBot;
//		drawBot.startSimulation();
//		drawBot.serialForKeyPresses = this; //awkward!!
//		nudgeInstructions.setToNudgeSentinel();
//		goAbsZeroInstructions.setToGoAbsZeroSentinel();
		
//		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownRoutine()));
		
		size(640,480);
		println(Serial.list()); // List all the available serial ports
		myPort = new Serial(this, Serial.list()[4], 9600);
//		myPort.bufferUntil('\n'); // read bytes into a buffer until you get a linefeed (ASCII 10):
//		myPort.buffer(6);
	}

	public void draw() {
//		if (artificialDelay == DELAYTIMEFRAMES - 1) {
			myPort.write(4);
//		}
		if (myPort.available() > 9) {
			println("more than 9 bytes here");
			String ser = "";
			while(myPort.available() > 0) {
				ser += myPort.readChar();
			}
			println(ser);
		}
		artificialDelay++;
	}

	// serialEvent  method is run automatically by the Processing applet
	// whenever the buffer reaches the  byte value set in the bufferUntil() 
	// method in the setup():
	public void serialEvent(Serial myPort) throws Exception { 
		
	}
	private void sendNextInstructions(MotorInstructions motorIns) {
		motorIns.assertLimits();

		byteMethod(motorIns);
		
		//may want bak
//		String stepInstructions = motorIns.sentinel + formatInt( motorIns.leftSteps()) + "," + formatInt( motorIns.rightSteps()) ; // 1+5+1+5
//		stepInstructions = stepInstructions + "," + formatFloatingValue(motorIns.leftSpeed()) + "," + formatFloatingValue(motorIns.rightSpeed()); // 1+7+1+7
//		myPort.write(stepInstructions);
		
	}
	private void byteMethod(MotorInstructions motorIns)  {
		myPort.write(intToByteCrude(motorIns.leftSteps()));
		myPort.write(intToByteCrude(motorIns.rightSteps()));
		myPort.write(intToByteCrude((int)motorIns.leftSpeed()));
		myPort.write(intToByteCrude((int)motorIns.rightSpeed()));
	}
//	private String floatAsCharString(float f) {
//		Asserter.assertTrue(Math.abs(f + .5) < Settings.StepMaxValue, "float + .5 must be less than " + Settings.StepMaxValue);
//		byte b = (byte) (f + .5);
//		
//	}
	private byte intToByteCrude(int i) {
		return (byte) i;
	}
	private char intToChar(int i) {
		return (char) i;
	}
	private String formatInt(int i) {
//		return "" + i;
		return String.format("%05d", i);
	}
	private String formatFloatingValue(double d) {
		boolean negative = d < 0;
		int i = (int) d;
		String istr = String.format("%04d", i);
		istr = istr.substring(istr.length() - 4); //9999.99 is too high anyway. guarantee string length
		d = d - i;
		String dstr = String.format("%.2f", d);
		while (dstr.charAt(0) == '0' || dstr.charAt(0) == '-') 
			dstr = dstr.substring(1);
		
		String sign = "#"; 
		if (negative) {
			if (istr.charAt(0) != '-') sign = "-";
		}
		return sign + istr + dstr;
	}

	public void doKeyPressed(KeyEvent ke) {
		int k = ke.getKeyCode();
		if (k == KeyEvent.VK_N) {
			nudgeMode = !nudgeMode;
			System.out.println("**** Nudge Mode: " + nudgeMode + " ******");
			if (!nudgeMode) {
				nudgeInstructions = new MotorInstructions();
			}
		}
		if (nudgeMode) {
			nudgeInstructions.setToNudgeSentinel();
			myPort.clear(); // help with instructions?
			int nudgeSpeed = 5;
			if (k == KeyEvent.VK_W) {
				nudgeInstructions.addLeftSteps(nudgeSpeed);//  leftMotorSteps += nudgeSpeed;
			} else if (k == KeyEvent.VK_S) {
				nudgeInstructions.addLeftSteps(-nudgeSpeed); // leftMotorSteps += -nudgeSpeed;
			} 
			if (k == KeyEvent.VK_K) {
				nudgeInstructions.addRightSteps(nudgeSpeed);   //rightMotorSteps += nudgeSpeed;
			} else if (k == KeyEvent.VK_I) {
				nudgeInstructions.addRightSteps(-nudgeSpeed);  // .rightMotorSteps += -nudgeSpeed;
			}
			if (k == KeyEvent.VK_H) {
				nudgeInstructions = new MotorInstructions();
			}
			if (k == KeyEvent.VK_Z ) {
				nudgeInstructions.setToGoAbsZeroSentinel();
			}
			
		}

	}
	
	private class ShutdownRoutine implements Runnable
	{

		@Override
		public void run() {
			System.out.println("shutting down...sending zero instrucs");
			
//			String serString = null;
//			int tries=0;
//			while(serString == null) {
//				serString = myPort.readStringUntil('\n');
//				if (tries++ > 2000) {
//					System.out.println("didn't hear from the arduino... exiting");
//					break;
//				}
//			}
			System.out.println("SHUT DOWN ROUTINE TURNED OFF");
//			sendNextInstructions(goAbsZeroInstructions);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	private static void AssertAbsLessThanNibble(int n) {
		Asserter.assertTrue(Math.abs(n) < 128, "Not less than 128 : " + n);
	}
	  
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "SSerialCallResponseASCIIV3Experi" });
	}
	@Override
	public void serialEvent(SerialPortEvent spe) {
		println("got an event. This doesn't get called??");
		
	}

}

