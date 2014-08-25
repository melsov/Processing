import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.*;
import processing.serial.*;
import drawbotV2.Asserter;
import drawbotV2.BotController;
import drawbotV2.DrawBot;
import drawbotV2.KeyHandling;
import drawbotV2.MotorInstructions;
	 
 // Graphing sketch
  
 // This program takes ASCII-encoded strings
 // from the serial port at 9600 baud and graphs them. It expects values in the
 // range 0 to 1023, followed by a newline, or newline and carriage return
 
 // Created 20 Apr 2005
 // Updated 18 Jan 2008
 // by Tom Igoe
 // This example code is in the public domain.
	 
public class SerialCallResponseASCII extends PApplet implements SerialPortEventListener, KeyHandling {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Serial myPort;                  // The serial port
	private DrawBot drawBot = new DrawBot();
	private BotController botController;
	
	private int artificialDelay = 0;
	private final static int DELAYTIMEFRAMES = 2;
	
	private boolean nudgeMode = false;
	
	private MotorInstructions nudgeInstructions = new MotorInstructions();
	private MotorInstructions goAbsZeroInstructions = new MotorInstructions();
	
	private boolean madeFirstContact = false;

	public void setup() {
		botController = drawBot;
		drawBot.startSimulation();
		drawBot.serialForKeyPresses = this; //awkward!!
		nudgeInstructions.setToNudgeSentinel();
		goAbsZeroInstructions.setToGoAbsZeroSentinel();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownRoutine()));
		
		size(640,480);
		println(Serial.list()); // List all the available serial ports
		myPort = new Serial(this, Serial.list()[4], 9600);
		myPort.bufferUntil('\n'); // read bytes into a buffer until you get a linefeed (ASCII 10):
	}

	public void draw() {
		artificialDelay++;
	}

	// serialEvent  method is run automatically by the Processing applet
	// whenever the buffer reaches the  byte value set in the bufferUntil() 
	// method in the setup():
	public void serialEvent(Serial myPort) throws Exception { 
		if (artificialDelay < DELAYTIMEFRAMES) return;
		else artificialDelay = 0;
		
		String myString = myPort.readStringUntil('\n');
	    myString = trim(myString);
	    
	    /*
	    ArduinoResponse ar = ArduinoResponse.FromSerialString(myString);
	    if (ar == null && madeFirstContact) {
	    	println("got a non-done string: " + myString);
	    	return;
	    } else if (myString.equals("HI")) {
	    	if (!madeFirstContact) {
	    		madeFirstContact = true;
	    	} else {
	    		return;
	    	}
	    } else {
	    	if (ar != null) ar.debug();
	    }
	    */ //DELETED TO REMOVE AN ERROR!!! (TODO: FIND ARDUINORESPONSE CLASS IF WE WANT TO RECOVER THIS VERSION OF SCRA.JAVA)

	    myPort.clear();
	    
	    MotorInstructions mInstructions = null;

	    if (!nudgeMode) {
		    mInstructions = botController.nextMotorInstructions();
	    } else {
	    	mInstructions = nudgeInstructions; //  botController.goHomeInstructions();
	    }
	    if (mInstructions != null) {
	    	mInstructions.debug();
	    	sendNextInstructions(mInstructions);
	    }
	}
	private void sendNextInstructions(MotorInstructions motorIns) {
		myPort.write(motorIns.sentinel);

		String stepInstructions = "#" +motorIns.leftSteps() + "," + motorIns.rightSteps() + "#";
		myPort.write(stepInstructions);
//		myPort.write(1);
//		myPort.write((int) motorIns.leftSteps());
//	    myPort.write(1);
//		myPort.write((int) motorIns.rightSteps());
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
				nudgeInstructions = goAbsZeroInstructions;
			}
			
		}

	}
	
	private class ShutdownRoutine implements Runnable
	{

		@Override
		public void run() {
			System.out.println("shutting down...sending zero instrucs");
			
			String serString = null;
			int tries=0;
			while(serString == null) {
				serString = myPort.readStringUntil('\n');
				if (tries++ > 2000) {
					System.out.println("didn't hear from the arduino... exiting");
					break;
				}
			}
			sendNextInstructions(goAbsZeroInstructions);
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
		PApplet.main(new String[] { "--present", "SerialCallResponseASCII" });
	}
	@Override
	public void serialEvent(SerialPortEvent spe) {
		println("got an event. This doesn't get called??");
		
	}

}

