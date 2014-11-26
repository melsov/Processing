import drawbotV3_2.*;
import drawbotV3_2.DrawPointProvider.NudgeMode;

import java.awt.event.KeyEvent;
import java.io.IOException;

import cz.adamh.utils.NativeUtils;
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
	 
/*
 * TODO: MACHINE IN NUDGE MODE AT START.
 * NUDGE MODE DOES NOT CHANGE THE LOCATION FROM THE SOFTWARES PERSPECTIVE. STAYS AT HOME POINT.
 * NUDGE (MAYBE) DISPLAYS AS AN ARROW FROM THE HOME POINT.
 * ALSO, PULL MODE: THE OLD STYLE WHERE WE SIMPLY SPIN ONE MOTOR OR THE OTHER OR BOTH.
 * DISPLAY ANNOUNCES THE MODE RATHER PROMINENTLY
 * TODO: PERFORM TESTS TO FIND CRASH POINTS: PLACES WHERE ACTIVITY STARTS. OUTPUT TO DIAGNOSE CRASHES. (FROM DUINO AND CLIENT)
 */
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
	private TestClient testClient;
	
	public void setup() {
		println("send int!!!");
		if (!DialogBoxer.QuestionWithTwoOptions("Do you want to really run the drawing machine or just show a simulation?",
				"Just Simulation", "Really Run") ) {
			TestClient tc = new TestClient();
			testClient = tc;
			tc.serialForKeyPresses = this;
//			this.frame.add(tc);
			drawBot = tc.drawBot;
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
	    int request_amt = 10;
	    sendInstructions(request_amt);
	    serialPort.clear();
	}
	
	private void getMessageFromSerial() {
		print(serialPort.readStringUntil('\n'));
	}

	int debug_count = 0;
	private void sendInstructions(int howMany) {
		byte[] bs = new byte[howMany * MotorInstructions.ByteArrayLength()];
		for (int i = 0; i < howMany; ++i) {
			MotorInstructions motorIns = botController.nextMotorInstructions();
			byte[] bb = motorIns.toByteArray();
			//if (debug_count++ % 10 == 0) debugSpeed(bb);
			for (int j = 0; j < bb.length ; j++) {
				bs[i * MotorInstructions.ByteArrayLength() + j] = bb[j];
			}
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
		byte[] bb = new byte[MotorInstructions.ByteArrayLength()];
		for (int j = 0; j < bb.length ; j++) { bb[ j] = 0; }
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

	//TODO: make pause mode release serial port, to be able to re-flash arduino...
	public void doKeyPressed(KeyEvent ke) {
		int k = ke.getKeyCode();
		if (k == KeyEvent.VK_N) {
			nudgeMode = !nudgeMode;
			System.out.println("**** Nudge Mode: " + nudgeMode + " ******");
			drawBot.setNudgeMode(nudgeMode ? NudgeMode.NOWHERE : NudgeMode.OFF);
			if (testClient != null) {
				testClient.autoDraw = !nudgeMode;
			}
		}
		if (nudgeMode) {
			nudgeInstructions.setToNudgeSentinel();
			if (serialPort != null) serialPort.clear(); // help with instructions?
			int nudgeSpeed = 5;
			if (k == KeyEvent.VK_UP) {
				drawBot.setNudgeMode(NudgeMode.UP);
			} else if (k == KeyEvent.VK_DOWN) {
				drawBot.setNudgeMode(NudgeMode.DOWN);
			} else if (k == KeyEvent.VK_RIGHT) {
				drawBot.setNudgeMode(NudgeMode.RIGHT);
			} else if (k == KeyEvent.VK_LEFT) {
				drawBot.setNudgeMode(NudgeMode.LEFT);
			} else if (k == KeyEvent.VK_E) {
				drawBot.setNudgeMode(NudgeMode.NOWHERE);
			}
			if (testClient != null) {
				testClient.doNextPoint();
			}
		}
	}
	
	private class ShutdownRoutine implements Runnable
	{

		@Override
		public void run() {
			System.out.println("shutting down...NOT sending zero instrucs");
			
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
			if (serialPort != null)
				serialPort.stop();
//			sendNextInstructions(goAbsZeroInstructions);
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		}
		
	}
	
	/*
	 * TODO: Load JNIs
	 * courtesy: http://stackoverflow.com/questions/2937406/how-to-bundle-a-native-library-and-a-jni-library-inside-a-jar
	 */

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

