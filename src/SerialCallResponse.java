import java.awt.event.KeyListener;

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
	 
public class SerialCallResponse extends PApplet implements SerialPortEventListener, KeyListener {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int bgcolor;			     // Background color
	int fgcolor = 255;			     // Fill color
	Serial myPort;                       // The serial port
	int[] serialInArray = new int[3];    // Where we'll put what we receive
	int serialCount = 0;                 // A count of how many bytes we receive
	int xpos, ypos;		             // Starting position of the ball
	boolean firstContact = false;        // Whether we've heard from the microcontroller
	
	int testSentCount = 0;

	public void setup() {
	  size(256, 256);  // Stage size
	  noStroke();      // No border on the next thing drawn

	  // Set the starting position of the ball (middle of the stage)
	  xpos = width/2;
	  ypos = height/2;

	  // Print a list of the serial ports, for debugging purposes:
	  println(Serial.list());

	  // I know that the first port in the serial list on my mac
	  // is always my  FTDI adaptor, so I open Serial.list()[0].
	  // On Windows machines, this generally opens COM1.
	  // Open whatever port is the one you're using.
	  String portName = Serial.list()[4];
	  myPort = new Serial(this, portName, 9600);
	}

	public void draw() {
	  background(bgcolor);
	  fill(fgcolor);
	  // Draw the shape
	  ellipse(xpos, ypos, 20, 20);
	}

	public void serialEvent(Serial myPort) {
	  // read a byte from the serial port:
	  int inByte = myPort.read();
	  // if this is the first byte received, and it's an A,
	  // clear the serial buffer and note that you've
	  // had first contact from the microcontroller. 
	  // Otherwise, add the incoming byte to the array:
	  if (firstContact == false) {
	    if (inByte == 'A') { 
	      myPort.clear();          // clear the serial port buffer
	      firstContact = true;     // you've had first contact from the microcontroller
	      myPort.write('A');       // ask for more
	    } 
	  } 
	  else {
	    // Add the latest byte from the serial port to array:
	    serialInArray[serialCount] = inByte;
	    serialCount++;

	    // If we have 3 bytes:
	    if (serialCount > 2 ) {
	      xpos = serialInArray[0];
	      ypos = serialInArray[1];
	      fgcolor = serialInArray[2];

	      // print the values (for debugging purposes only):
	      println(xpos + "\t" + ypos + "\t" + fgcolor);


	      char outByte = (char) (testSentCount++ % 40);
	      // Send a capital A to request new sensor readings:
	      myPort.write(outByte);
	      // Reset serialCount:
	      serialCount = 0;
	    }
	  }
	}
	  
	public static void main(String args[]) 
	{
		PApplet.main(new String[] { "--present", "SerialCallResponse" });
	}
	@Override
	public void serialEvent(SerialPortEvent spe) {
		println("got an event");
		
	}
}

/*
 * 
 * Arduino code:
*/
/*
Serial Event example

When new serial data arrives, this sketch adds it to a String.
When a newline is received, the loop prints the string and 
clears it.

A good test for this is to try it with a GPS receiver 
that sends out NMEA 0183 sentences. 

Created 9 May 2011
by Tom Igoe

This example code is in the public domain.

http://www.arduino.cc/en/Tutorial/SerialEvent

*/
/*
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

void setup() {
// initialize serial:
Serial.begin(115200);
// reserve 200 bytes for the inputString:
inputString.reserve(200);
}

void loop() {

// print the string when a newline arrives:
if (stringComplete) {
//  Serial.println("string is");
  Serial.println(inputString); 
  // clear the string:
  inputString = "";
  stringComplete = false;
}
delay(50);  
}

/*
SerialEvent occurs whenever a new data comes in the
hardware serial RX.  This routine is run between each
time loop() runs, so using delay inside loop can delay
response.  Multiple bytes of data may be available.

void serialEvent() {
while (Serial.available()) {
  // get the new byte:
  char inChar = (char)Serial.read(); 
  // add it to the inputString:
  inputString += inChar;
  // if the incoming character is a newline, set a flag
  // so the main loop can do something about it:
  if (inChar == '\n') {
    stringComplete = true;
  } 
  
}

}
*/