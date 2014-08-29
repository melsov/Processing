package drawbotV3_2;

import static org.junit.Assert.*;

import org.junit.Test;

import drawbotV3_2.B;
import drawbotV3_2.MotorInstructions;


public class MotorInstructionsTest {

	MotorInstructions mi = new MotorInstructions();
	
	@Test
	public void testMath() {
		
		
		String hex;
		int i = -32766; //  Integer.parseInt("-100000000", 2);
		B.bug("I: ");B.bug(i);
		B.bugln(Integer.toBinaryString(i));
		
		byte k = (byte) (i >> 8);
		B.bug("K: "); B.bug(k);
		B.bugln(Integer.toBinaryString(k));
//		i = i >> 1;
		byte j = (byte) (i & 127);
		B.bug("J: "); B.bug(j);
		hex = Integer.toBinaryString(j);
		B.bugln(hex);
		 
		int g = k;
		g = g << 8;
		g = g | j;
		hex = Integer.toBinaryString(g);
		B.bugln(hex);
		B.bug(g);
		
		
		i = (int) (Short.MIN_VALUE * .25);
		
		for (; i < Short.MAX_VALUE * .25; i += 1) {
			int ti =  i; //-32766;
			mi = new MotorInstructions(ti, 455);
			float tolerance = 0;
			int ls = mi.leftSteps();
			if (ls != ti) {
				B.bugln("not eq!!");
				B.bug("ti:");B.bug(ti);
				B.bug("mi:");B.bug(ls);
				B.bug("ti:");B.bugln(Integer.toBinaryString(ti));
				B.bug("mi:");B.bugln(Integer.toBinaryString(ls));
				B.bug("instr debug binary: \n");
				mi.debugBinary();
			}
			assertEquals(ls, ti, tolerance);
		}
	
	}
}
