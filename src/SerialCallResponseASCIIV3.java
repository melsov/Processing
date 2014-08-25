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
import drawbotV3.ShortToBytes;
	 
 // Graphing sketch
  
 // This program takes ASCII-encoded strings
 // from the serial port at 9600 baud and graphs them. It expects values in the
 // range 0 to 1023, followed by a newline, or newline and carriage return
 
 // Created 20 Apr 2005
 // Updated 18 Jan 2008
 // by Tom Igoe
 // This example code is in the public domain.
	 
// **** V3 !!****
public class SerialCallResponseASCIIV3 extends PApplet implements SerialPortEventListener, KeyHandling {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Serial myPort;                  // The serial port
	private DrawBot drawBot = new DrawBot();
	private BotController botController;
	
	private int artificialDelay = 0;
	private int DELAYTIMEFRAMES = 20;
	
	private boolean nudgeMode = false;
	
	private MotorInstructions nudgeInstructions = new MotorInstructions();
	private MotorInstructions goAbsZeroInstructions = new MotorInstructions();
	
	private boolean madeFirstContact = false;

	private byte DATA_REQUEST_SENTINEL = '#';
	
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
	private void attemptFirstContact() {
		String myString = myPort.readStringUntil('\n');
		myPort.clear();
	    myString = trim(myString);
	    
    	if (myString.equals("HI")) {
    		println("arduino said hi");
    		madeFirstContact = true;
    		sendNextInstructions(goAbsZeroInstructions);
    		DELAYTIMEFRAMES = 0; // non-zero seems to 'de-glitch' serial communication: TODO: discern why this is
    	} 
    	println("it is " + madeFirstContact + " that we're in touch with the arduino");
    	return;
	}

	// serialEvent  method is run automatically by the Processing applet
	// whenever the buffer reaches the  byte value set in the bufferUntil() 
	// method in the setup():
	public void serialEvent(Serial myPort) throws Exception { 
		if (artificialDelay < DELAYTIMEFRAMES) return;
		else artificialDelay = 0;
		
	    if (!madeFirstContact) {
	    	attemptFirstContact();
	    	return;
	    }
	    
//	    String myString = myPort.readStringUntil('\n');

//	    int req = dataRequestAmount(myPort);
	    int request_amt = 5; // Math.min(req, 5); // 2;   //
//	    myPort.clear();
	    sendInstructions(request_amt);

	}
	private void sendInstructions(int howMany) {
		byte[] bs = new byte[howMany * 4];
		for (int i = 0; i < howMany; ++i) {
			byte[] bb = getBytes(botController.nextMotorInstructions());
//			sendNextInstructions(botController.nextMotorInstructions());
			for (int j = 0; j < bb.length ; j++) {
				bs[i * 4 + j] = bb[j];
			}
		}
		myPort.write(bs);
	}
	private int dataRequestAmount(Serial serial) {
		int res = 0;
		if (serial.available() > 1) {
//			if (serial.read() == DATA_REQUEST_SENTINEL) {
				res = serial.read();
//			}
		}
		return res;
	}
	private void sendNextInstructions(MotorInstructions motorIns) {
//		motorIns.assertLimits(); //TEST>>!!!
		byteMethod(motorIns);		
	}
	private void byteMethod(MotorInstructions motorIns)  {
//		writeBytes(motorIns.leftSteps());
//		writeBytes(motorIns.rightSteps());
//		writeBytes((int)motorIns.leftSpeed());
//		writeBytes((int)motorIns.rightSpeed());
		print("about to write");
		myPort.write(getBytes(motorIns));
//		myPort.write(intToByte(motorIns.leftSteps()));
//		print(0);
//		myPort.write(intToByte(motorIns.rightSteps()));
//		print(1);
//		myPort.write(intToByte((int)motorIns.leftSpeed()));
//		print(2);
//		myPort.write(intToByte((int)motorIns.rightSpeed()));
		println("wrote");
	}
	private byte[] getBytes(MotorInstructions motorIns) {
		byte[] res = new byte[4];
		int i = 0;
		res[i++] = (byte) motorIns.leftSteps();
		res[i++] = (byte) motorIns.rightSteps();
		res[i++] = (byte) motorIns.leftSpeed();
		res[i++] = (byte) motorIns.rightSpeed();
		return res;
	}
	private void writeBytes(int i) {
		byte[] bs = ShortToBytes.Convert(i);
		myPort.write(bs[0]);
		myPort.write(bs[1]);
	}

	private byte intToByte(int i) { return (byte) i; }

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
	  
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "SerialCallResponseASCII" });
	}
	@Override
	public void serialEvent(SerialPortEvent spe) {
		println("got an event. This doesn't get called??");
		
	}

}

