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
	 
public class Graph extends PApplet implements SerialPortEventListener, KeyListener {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Serial myPort;        // The serial port
	int xPos = 1;         // horizontal position of the graph
	 
	 public void setup () {
		 // set the window size:
		 size(400, 300);        
		 
		 // List all the available serial ports
		 println(Serial.list());
		 // I know that the first port in the serial list on my mac
		 // is always my  Arduino, so I open Serial.list()[0].
		 // Open whatever port is the one you're using.
		 myPort = new Serial(this, Serial.list()[4], 9600);
		 // don't generate a serialEvent() unless you get a newline character:
		 myPort.bufferUntil('\n');
		 // set inital background:
		 background(0);
	 }
	 public void draw () {
	 // everything happens in the serialEvent()
	 }
	 
	 public void serialEvent (Serial myPort) {
		 // get the ASCII string:
		 String inString = myPort.readStringUntil('\n');
		 
		 if (inString != null) {
			 // trim off any whitespace:
			 inString = trim(inString);
			 // convert to an int and map to the screen height:
			 float inByte;
			 try {
				 inByte = Float.valueOf(inString);
			 } catch (java.lang.NumberFormatException nfe) {
				 inByte = 0;
			 }
			 //float inByte = Float.parseFloat(inString); // float(inString); 
			 inByte = map(inByte, 0, 1023, 0, height);
			 
			 // draw the line:
			 stroke(127,34,255);
			 line(xPos, height, xPos, height - inByte);
			 
			 // at the edge of the screen, go back to the beginning:
			 if (xPos >= width) {
				 xPos = 0;
				 background(0); 
			 } 
			 else {
				 // increment the horizontal position:
				 xPos++;
			 }
		 }
	 }
	 	 
	  
	public static void main(String args[]) 
	{
		PApplet.main(new String[] { "--present", "Graph" });
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