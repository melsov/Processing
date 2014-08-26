import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.*;
import processing.serial.*;
import drawbotV3OffTrack.Asserter;
import drawbotV3OffTrack.BotController;
import drawbotV3OffTrack.DrawBot;
import drawbotV3OffTrack.KeyHandling;
import drawbotV3OffTrack.MotorInstructions;
import drawbotV3OffTrack.Settings;
	 
 // Graphing sketch
  
 // This program takes ASCII-encoded strings
 // from the serial port at 9600 baud and graphs them. It expects values in the
 // range 0 to 1023, followed by a newline, or newline and carriage return
 
 // Created 20 Apr 2005
 // Updated 18 Jan 2008
 // by Tom Igoe
 // This example code is in the public domain.
	 
public class SerialCallResponseASCIIV32 extends PApplet implements SerialPortEventListener, KeyHandling {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Serial myPort;                  // The serial port
	private DrawBot drawBot = new DrawBot();
	private BotController botController;
	
	private int artificialDelay = 0;
	private int DELAYTIMEFRAMES = 10;
	
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
		
//		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownRoutine()));
		
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
		myPort.clear();
	    myString = trim(myString);
	    print("Serial string: " + myString + " : ");
	    
	    if (!madeFirstContact) {
	    	if (myString.equals("HI")) {
	    		println("arduino said hi");
	    		madeFirstContact = true;
	    		sendNextInstructions(goAbsZeroInstructions);
	    		DELAYTIMEFRAMES = 2;
	    	} 
	    	println("not made first contact returning");
	    	return;
	    }
	    
	    if (myString.equals("MORE")) {
	    	String duinoInstructionSet = makeSerialInstructionBatch();
	    	println(duinoInstructionSet);
	    	myPort.write(duinoInstructionSet);
	    	return;
	    } else {
	    	Asserter.assertFalseAndDie("WHY did we not get asked for more??");
	    }
	    
//	    ArduinoResponse ar = ArduinoResponse.FromSerialStringQuickResponse(myString);
//	    if (ar == null && madeFirstContact) {
//	    	println("got a non-done string: " + myString);
//	    	return;
//	    } else if (myString.equals("HI")) {
//	    	if (!madeFirstContact) {
//	    		madeFirstContact = true;
//	    		println("got said hi (again????)");
//	    		sendNextInstructions(goAbsZeroInstructions);
//	    	}
//	    	println("Already got hi. just returning...");
//	    	return;
//	    } else {
//	    	if (ar == null) {
//	    		return;
//	    	}
////	    	println("VALID AR RESPONSE: L/R speed " + ar.lspeed + " : " + ar.rspeed);
////	    	if (abs(ar.lspeed) > 30 || abs(ar.rspeed) > 30) ar.debug(); 
//	    }
//	    
////	    myPort.clear(); 
//	    MotorInstructions mInstructions = null;
//
//	    if (!nudgeMode) {
//		    mInstructions = botController.nextMotorInstructions();
//	    } else {
//	    	mInstructions = nudgeInstructions; 
//	    }
//	    if (mInstructions != null) {
//	    	mInstructions.debug();
//	    	sendNextInstructions(mInstructions);
//	    }
	}
	private String makeSerialInstructionBatch() {
		String stepInstructions = "";
		for(int i = 0; i < Settings.ARDUINO_INSTRUCTION_BUFFER_SIZE; ++i) {
			MotorInstructions mi = botController.nextMotorInstructions();
			mi.assertLimits();
//			stepInstructions += "," + formatInt(mi.leftSteps()) + "," + formatInt(mi.rightSteps());
			stepInstructions += intToByteCrude(mi.leftSteps()) + "" + intToByteCrude(mi.rightSteps());
			stepInstructions += "," + formatFloatingValue(mi.leftSpeed()) + "," + formatFloatingValue(mi.rightSpeed()) + "*";
		}
		return stepInstructions;
	}
	private void sendNextInstructions(MotorInstructions motorIns) {
		motorIns.assertLimits();
		String stepInstructions = motorIns.sentinel + formatInt( motorIns.leftSteps()) + "," + formatInt( motorIns.rightSteps()) ; // 1+5+1+5
		stepInstructions = stepInstructions + "," + formatFloatingValue(motorIns.leftSpeed()) + "," + formatFloatingValue(motorIns.rightSpeed()); // 1+7+1+7
//		println(stepInstructions.length());
//		println(stepInstructions);
		
		myPort.write(stepInstructions);
//		myPort.write(1);
//		myPort.write(intToByteCrude( motorIns.leftSteps()) );
//	    myPort.write(1);
//		myPort.write(intToByteCrude(motorIns.rightSteps()) );
	}
	private byte intToByteCrude(int i) {
		return (byte) i;
	}
	private String formatInt(int i) {
//		return "" + i;
		return String.format("%07d", i);
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
		PApplet.main(new String[] { "--present", "SerialCallResponseASCIIV32" });
	}
	@Override
	public void serialEvent(SerialPortEvent spe) {
		println("got an event. This doesn't get called??");
		
	}

}

