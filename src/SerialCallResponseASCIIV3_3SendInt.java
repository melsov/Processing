import drawbotV3_2.*;

import java.awt.event.KeyEvent;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import processing.core.*;
import processing.serial.*;

	 
 // Graphing sketch
  
 // This program takes ASCII-encoded strings
 // from the serial port at 9600 baud and graphs them. It expects values in the
 // range 0 to 1023, followed by a newline, or newline and carriage return
 
 // Created 20 Apr 2005
 // Updated 18 Jan 2008
 // by Tom Igoe
 // This example code is in the public domain.
	 
// **** V3 !!****
public class SerialCallResponseASCIIV3_3SendInt extends PApplet implements SerialPortEventListener, KeyHandling {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Serial serialPort;                  // The serial port
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

		if (!DialogBoxer.QuestionWithTwoOptions("Do you want to really run the drawing machine or just show a simulation?",
				"Just Simulation", "Really Run") ) {
			TestClient tc = new TestClient();
			this.frame.add(tc);
			noLoop();
			return;
		}
		
		botController = drawBot;
		drawBot.startSimulation();
		drawBot.serialForKeyPresses = this; //awkward!!
		nudgeInstructions.setToNudgeSentinel();
		goAbsZeroInstructions.setToGoAbsZeroSentinel();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownRoutine()));
		
		size(640,480);
		int serialPick = 4;
		serialPort = new Serial(this, Serial.list()[serialPick], 9600);
		if (serialPort != null) serialPort.bufferUntil('\n'); // read bytes into a buffer until you get a linefeed (ASCII 10):
		
		int tries = 0;
		while( serialPort == null ) {
			try {
				println("trying port: " + serialPick);
				serialPort = new Serial(this, Serial.list()[serialPick], 9600);
				serialPort.bufferUntil('\n'); // read bytes into a buffer until you get a linefeed (ASCII 10):
				int contactTries = 0;
				while (!madeFirstContact) {
					println("attempting contact");
					if (serialPort.available() > 0) {
						attemptFirstContact();
					}
					try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
					if (contactTries++ > 9) {
						serialPort = null;
						throw new Exception("Oh dear. this serial port: " + Serial.list()[serialPick] + " isn't responding");
					}
				}
			} catch (Exception e) {
				println("exception for : " + serialPick);
				serialPick = ++serialPick % Serial.list().length;
			}
			if (tries++ == Serial.list().length) {
				DialogBoxer.Message("Couldn't get in touch with the Arduino.\n" +
						" Can you check the connection (maybe re-insert USB \n" +
						" cable, etc.), then hit the button below? (Failing this, make sure the \n" +
						" arduino is running the drawbot program, maybe?) ", "OK. I did that. Let's try again");
				tries = 0;
			}
		}
		println("Using the serial port: " + Serial.list()[serialPick] + " with index: " + serialPick + " from the following list: ");
		println(Serial.list()); // List all the available serial ports
		println("If connection fails, maybe try plugging into another USB slot on the computer");
	}

	public void draw() {
		artificialDelay++;
	}
	private void attemptFirstContact() {
		String myString = serialPort.readStringUntil('\n');
		serialPort.clear();
	    myString = trim(myString);
	    
    	if (myString.equals("HI")) {
    		println("arduino said hi");
    	
    		sendNowhereInstructions();
    		madeFirstContact = true;
    		DELAYTIMEFRAMES = 0; // non-zero seems to 'de-glitch' serial communication: TODO: discern why this is
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	} 
    	println("it is " + madeFirstContact + " that we're in touch with the arduino");
    	return;
	}

	/*
	 * serialEvent  method is run automatically by the Processing applet
	 * whenever the buffer reaches the  byte value set in the bufferUntil() 
	 * method in the setup():
	 */
	public void serialEvent(Serial myPort) throws Exception { 
		if (artificialDelay < DELAYTIMEFRAMES) return;
		else artificialDelay = 0;
		
	    if (!madeFirstContact) {
	    	attemptFirstContact();
	    	return;
	    }
	    
	    getMessageFromSerial();
	    int request_amt = 5;
	    sendInstructions(request_amt);
	    serialPort.clear();
//	    Thread.sleep(80);
	}
	
	private void getMessageFromSerial() {
		String msg = serialPort.readStringUntil('\n');
		println(trim(msg));
	}

	int debug_count = 0;
	private void sendInstructions(int howMany) {
		byte[] bs = new byte[howMany * MotorInstructions.ByteArrayLength()];
		for (int i = 0; i < howMany; ++i) {
			MotorInstructions motorIns = botController.nextMotorInstructions();
			byte[] bb = motorIns.toByteArray();
			if (debug_count++ % 10 == 0) debugSpeed(bb);
			for (int j = 0; j < bb.length ; j++) {
				bs[i * MotorInstructions.ByteArrayLength() + j] = bb[j];
//				print(bb[j]);
//				print('%');
			}
//			println();
		}
		serialPort.write(bs);
	}
	private void debugSpeed(byte[] bb) {
		byte[] bbl = new byte[] {bb[2], bb[3]};
		byte[] bbr = new byte[] {bb[4], bb[5]};
		int lspeed = ConvertToBytes.IntFromSignedBytePair(bbl);
		int rspeed = ConvertToBytes.IntFromSignedBytePair(bbr);
		println("left speed: "+ lspeed);
		println("right speed "+ rspeed);
		println(ConvertToBytes.intAsBinaryString(lspeed));
		println(ConvertToBytes.intAsBinaryString(rspeed));
	}
	private void sendInstructionsOLDGOLD(int howMany) {
		byte[] bs = new byte[howMany * 4];
		for (int i = 0; i < howMany; ++i) {
			byte[] bb = getBytes(botController.nextMotorInstructions());
			for (int j = 0; j < bb.length ; j++) {
				bs[i * 4 + j] = bb[j];
			}
		}
		serialPort.write(bs);
	}
	private void sendNowhereInstructions() {
//		byte[] bs = new byte[1 * MotorInstructions.ByteArrayLength()];
//		for (int i = 0; i < 1; ++i) {
		byte[] bb = new byte[MotorInstructions.ByteArrayLength()]; //  getBytes(botController.nextMotorInstructions());
		for (int j = 0; j < bb.length ; j++) {
			bb[ j] = 0; // bb[j];
		}
//		}
		serialPort.write(bb);
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
			serialPort.clear(); // help with instructions?
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
//			System.out.println("SHUT DOWN ROUTINE TURNED OFF");
			serialPort.stop();
//			sendNextInstructions(goAbsZeroInstructions);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	  
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "SerialCallResponseASCIIV3_3SendInt" });
	}
	@Override
	public void serialEvent(SerialPortEvent spe) {
		println("got an event. This doesn't get called??");
		
	}
	
	private void sendInstructionsNOT(int howMany) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < howMany; ++i) {
			sb.append(botController.nextMotorInstructions().toInstructionString());
		}
		serialPort.write(sb.toString());
	}
	

}

