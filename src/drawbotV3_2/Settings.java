package drawbotV3_2;

import drawbotV3_2.Motor;

/*
 * Settings for gocupi-style interpolation
 * TODO: add more machine settings
 */
public class Settings 
{
	public static boolean INTERPOLATE_MODE = false;
	public static boolean AssertLimits = true;

	/*
	 * Vertical height from the center of (either) motor to the top of the gondola (not the pen tip)
	 */
	public static final float MOTOR_AXIS_TO_GONDOLA_TOP_HEIGHT_MM = 337.0f;
	/*
	 * Distance from the top of the gondola to the pen tip
	 */
	public static final float GONDOLA_RADIUS_MM = 78F;
	public static final float GONDOLA_HOME_POINT_HEIGHT_MM = MOTOR_AXIS_TO_GONDOLA_TOP_HEIGHT_MM + GONDOLA_RADIUS_MM; 
	public static final Pointt PAPER_DIMENSIONS = new Pointt(610, 457); //24 by 18 inches in mm
	
	/*
	 * the length between the two points where each thread meets its respective motor spool.
	 * Or, in other words, the distance between the left side of the left motor spool and
	 * the right side of the right motor spool.
	 */
	public static int MACHINE_WIDTH = 940;
	public static final Pointt PAPER_UPPER_LEFT_CORNER = new Pointt((MACHINE_WIDTH - PAPER_DIMENSIONS.x) * .5f, GONDOLA_HOME_POINT_HEIGHT_MM);

	
	public static float AbsMaxMotorStepsPerSecond = 120.0f; //*! needs to be set in arduino code as well
	public static float AbsMaxMotorStepsAccelPerSecond = 100.0f; //*! needs be set in arduino code as well
	
	public static StepType STEP_TYPE = StepType.MICROSTEP;
	// number of micro seconds per time slice
	public static double TimeSlice_US = STEP_TYPE.getTimeSliceUS(); // 2048d * 8d; // *! also arduino code
	
	public static double StepMaxValue = 126; 
	
	// * 16 for micro, * 2 for interleave 
	public static int STEPS_PER_REV = STEP_TYPE.getStepsPerRev(); // 200 * 16;
	
	public static double spoolDiameter=19.2d;
	
	// Gondola Speed = (StepsPerSec / STEPS_PER_REV) * PI * D
	public static double MaxGondolaSpeed_MM_S; // = Math.PI * spoolDiameter * (AbsMaxMotorStepsPerSecond / STEPS_PER_REV); 
	public static double ZeroToMaxSpeedTime_S; // =  AbsMaxMotorStepsPerSecond / AbsMaxMotorStepsAccelPerSecond;
	
	public static double Acceleration_MM_S2; // = MaxGondolaSpeed_MM_S/ZeroToMaxSpeedTime_S;
	
	public static double MinGondolaMoveDistance = 1d;
	public static double GondolaCrawlSpeed_MM_S = Acceleration_MM_S2 * .1; 
	
	public static double ONE_MILLION = 1000000.0d; //typing aid
	
	public static final int ARDUINO_INSTRUCTION_BUFFER_SIZE = 40; //*! must also change in arduino code?
	
	static {
		SetInterpolateAndReset(INTERPOLATE_MODE);
	}
	
	public static void SetInterpolateAndReset(boolean interpolate_is_on) {
		INTERPOLATE_MODE = interpolate_is_on;
		if (INTERPOLATE_MODE) {
			SetForInterpolationMode();
		} else {
			SetForNonInterpolationMode();
		}
	}
	
	public enum StepType {
		SINGLE (1,2),
		DOUBLE (1,2),
		INTERLEAVE(2,2),
		MICROSTEP(16,1);
		private int stepsPerRevMultiplier;
		private double timeSliceMultiplier;
		StepType(int _multiplier, double _timeSliceMultiplier) {
			stepsPerRevMultiplier = _multiplier; timeSliceMultiplier = _timeSliceMultiplier;
		}
		public double getTimeSliceUS() { return BaseTimeSlice_US * timeSliceMultiplier; }
		public int getStepsPerRev() { return (int) (BaseStepsPerRev * stepsPerRevMultiplier); }
		private static double BaseTimeSlice_US = 2048d; 
		private static int BaseStepsPerRev = 200;
	}
	
	public static void SetForInterpolationMode() {
		AbsMaxMotorStepsPerSecond = 120.0f; //*! needs to be set in arduino code as well
		AbsMaxMotorStepsAccelPerSecond = 100.0f; //*! needs be set in arduino code as well
		STEP_TYPE = StepType.INTERLEAVE;
		StepMaxValue = 126; 
		SetDerivedValues();
	}
	public static void SetForNonInterpolationMode() {
		AbsMaxMotorStepsPerSecond = 120.0f; //*! needs to be set in arduino code as well
		AbsMaxMotorStepsAccelPerSecond = 100.0f; //*! needs be set in arduino code as well
		STEP_TYPE = StepType.INTERLEAVE;
		StepMaxValue = 126; 
		SetDerivedValues();
	}
	
	private static void SetDerivedValues() {
		TimeSlice_US = STEP_TYPE.getTimeSliceUS();  // 2048d * 8d; // *! also arduino code
		// * 16 for micro, * 2 for interleave 
		STEPS_PER_REV = STEP_TYPE.getStepsPerRev();
		// -- Gondola Speed = (StepsPerSec / STEPS_PER_REV) * PI * D --
		MaxGondolaSpeed_MM_S = Math.PI * spoolDiameter * (AbsMaxMotorStepsPerSecond / STEPS_PER_REV); 
		ZeroToMaxSpeedTime_S =  AbsMaxMotorStepsPerSecond / AbsMaxMotorStepsAccelPerSecond;
		Acceleration_MM_S2 = MaxGondolaSpeed_MM_S/ZeroToMaxSpeedTime_S;
	}
}
/*
 * SMALL TRAY SETTINGS
public class Settings 
{
	public static boolean ShouldInterpolate = true;
	public static boolean AssertLimits = true;

	public static final float MOTOR_AXIS_TO_GONDOLA_TOP_HEIGHT_MM = 190.0f;
	public static final float GONDOLA_RADIUS_MM = 78F;
	public static final float GONDOLA_HOME_POINT_HEIGHT_MM = MOTOR_AXIS_TO_GONDOLA_TOP_HEIGHT_MM + GONDOLA_RADIUS_MM; // 262.0F;
	
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
*/
