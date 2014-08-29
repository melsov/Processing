package drawbotV3_2;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import drawbotV3_2.Machine;
import static java.lang.Math.*;


public class MachineTest {

	Machine machine = new Machine();
	@Before
	public void setUp() throws Exception {
	}
	
//	@Test
//	public void testGoNowhere() {
//		Pointt targetPoint = machine.startPoint();
//		MotorInstructions mi = machine.moveToPoint(targetPoint);
//		double tolerance = 0.0;
////		assertEquals(0, mi.leftMotorSteps, tolerance);
////		assertEquals(0, mi.rightMotorSteps, tolerance);
//	}
	
//	@Test
//	public void testPosition() {
//		Pointt targetPoint = machine.startPoint(); 
//		targetPoint.y += 50;
//		machine.moveToPoint(targetPoint);
//		double tolerance = 3.5; //tolerance needs to be very high! TODO: figure out why.
//		assertEquals(machine.penLocation().x, targetPoint.x, tolerance);
//		assertEquals(machine.penLocation().y, targetPoint.y, tolerance);
//	}
//	
//	@Test
//	public void testPosition3060() {
//		double leftLen = 100;
//		Pointt targetPoint = new Pointt(leftLen * cos(PI/3.0), leftLen*sin(PI/3.0));
//		machine.moveToPoint(targetPoint);
//		B.bug(machine.penLocation());
//		B.bug(targetPoint);
//		assertEquals(machine.penLocation().x, targetPoint.x, 0.5);
//	}
//	
//	@Test
//	public void motorDistanceTestRight() {
//		double leftLen = Settings.MachineWidth/2.0;
//		Pointt targetPoint = new Pointt(leftLen * cos(PI/3.0), leftLen * sin(PI/3.0));
//		double machinesAnswer = machine.testDistanceFromMotorToPoint(targetPoint, true);
//		assertEquals(leftLen*SqrtOfThree(), machinesAnswer, 0.1);
//	}
//	
//	@Test
//	public void motorDistanceTestLeft() {
//		Pointt targetPoint = new Pointt(100 * cos(PI/6.0), 100 * sin(PI/6.0));
//		double machinesAnswer = machine.testDistanceFromMotorToPoint(targetPoint, false);
//		assertEquals(100,  machinesAnswer, 0.1);
//	}
//	
//	
//	@Test
//	public void testAngleCalc() {
//		double leftLen = (sqrt(6) - sqrt(2)) * machine.machineWidth /4.0;
//		double leftAng = DegreesToRad(75);
//		double expectedAng = DegreesToRad(15);
//		Pointt targetPoint = new Pointt(leftLen * cos(leftAng), leftLen * sin(leftAng));
//		machine.moveToPoint(targetPoint);
//		double a = machine.testAngleForSide(true);
//		assertEquals(expectedAng, a, 0.05);
//	}
//	
//	public static double RadToDegrees(double rads) { return rads * (180.0/PI); }
//	public static double DegreesToRad(double degs) { return degs * (PI/180.0); }
//	
//	public static double SqrtOfThree() { return sqrt(3); }


}
