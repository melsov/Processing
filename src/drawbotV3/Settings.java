package drawbotV3;

import drawbotV3.Motor;

/*
 * Settings for gocupi-style interpolation
 * TODO: add more machine settings
 */
public class Settings 
{
//	public static boolean ShouldInterpolate = true;

	public static double TimeSlice_US = 2048d* 1d;
	
	public static final double StepMaxValue = 126; 
	
	// * 16 for micro, *2 for interleave 
	public static final int STEPS_PER_REV = 200 * 16; 
	
	public static float AbsMaxMotorStepsPerSecond = 120.0f; //should be set in arduino code as well
	public static float AbsMaxMotorStepsAccelPerSecond = 100.0f; //should be set in arduino code as well
	
	// Gondola Speed = (StepsPerSec / SPREV) * PI * D
	public static double MaxGondolaSpeed_MM_S = ((AbsMaxMotorStepsPerSecond / Motor.STEPS_PER_REV) * Math.PI) * (Motor.spoolDiameter); 
	public static double ZeroToMaxSpeedTime_S =  AbsMaxMotorStepsPerSecond / AbsMaxMotorStepsAccelPerSecond;
	
	public static double Acceleration_MM_S2 = MaxGondolaSpeed_MM_S/ZeroToMaxSpeedTime_S;
	
	public static double MinGondolaMoveDistance = 1d;
	public static double GondolaCrawlSpeed_MM_S = Acceleration_MM_S2 * .1; // MaxGondolaSpeed_MM_S * .1;
	
	// the length between the two points at which each thread meets its motor spool.
	public static int MachineWidth = (int) 457.2; 
	
	public static final double ONE_MILLION = 1000000.0; //typing aid
	
	public static final int ARDUINO_INSTRUCTION_BUFFER_SIZE = 40; // must also change in arduino code
}
