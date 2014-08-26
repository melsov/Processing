package drawbotV3OffTrack;

import drawbotV3OffTrack.Motor;

/*
 * Settings for gocupi-style interpolation
 * TODO: add more machine settings
 */
public class Settings 
{
	public static boolean ShouldInterpolate = true;
	public static boolean AssertLimits = true;

	public static final float MOTOR_AXIS_TO_GONDOLA_TOP_HEIGHT_MM = 190.0f;
	public static final float GONDOLA_RADIUS_MM = 78F;
	public static final float GONDOLA_TOP_AT_175_MM_MARK_HEIGHT = MOTOR_AXIS_TO_GONDOLA_TOP_HEIGHT_MM + GONDOLA_RADIUS_MM; // 262.0F;
	
	public static double TimeSlice_US = 2048d;
	
	public static double StepMaxValue = 126; 
	
	// * 16 for micro, *2 for interleave 
	public static int STEPS_PER_REV = 200 * 2; 
	
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
	
	public static double ONE_MILLION = 1000000.0d; //typing aid
//	public static double REDUCE_STEP_FACTOR = 1.0d;
	
	public static final int ARDUINO_INSTRUCTION_BUFFER_SIZE = 40; // must also change in arduino code
}
