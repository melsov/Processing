package drawbotV3OffTrack;

import drawbotV3OffTrack.Asserter;
import drawbotV3OffTrack.B;
import drawbotV3OffTrack.CoordVelocity;
import drawbotV3OffTrack.Settings;

public class MotorInstructions {
	private static char DRAW_SENTINEL = '$'; //36
	private static char NUDGE_SENTINEL = '*'; //42
	private static char GO_ABS_ZERO_SENTINEL = '@'; //42
	
	public char sentinel = DRAW_SENTINEL;
	private int leftMotorSteps;
	private int rightMotorSteps;
//	public byte leftMotorStepsBigBits;
//	public byte rightMotorStepsBigBits;
//	public byte leftMotorStepsSmallBits;
//	public byte rightMotorStepsSmallBits;
	public float leftSpeed;
	public float rightSpeed;
	public float leftAcceleration;
	public float rightAcceleration;
	
	public CoordVelocity coVelDebug = new CoordVelocity();
	
	public MotorInstructions(int leftSteps, int rightSteps) {
		this(leftSteps, rightSteps, DRAW_SENTINEL);
	}
	public MotorInstructions(int leftSteps, int rightSteps, char _sentinel) {
		this(leftSteps, rightSteps, 0, 0, _sentinel);
	}
	public MotorInstructions(int leftSteps, int rightSteps,  float l_speed, float r_speed) {
		this(leftSteps, rightSteps, l_speed, r_speed, DRAW_SENTINEL);
	}
	public MotorInstructions(int leftSteps, int rightSteps,  float l_speed, float r_speed,char _sentinel) {

		sentinel = _sentinel;
		resetLeftHalf(leftSteps);
		resetRightHalf(rightSteps);
		leftMotorSteps = leftSteps;
		rightMotorSteps = rightSteps;
		leftSpeed = l_speed;
		rightSpeed = r_speed;
	}
	public static MotorInstructions GoNowhereInstructions() {
		float small = .001f;
		return new MotorInstructions(0,0, small, small);
	}
	public MotorInstructions(double leftSteps, double rightSteps) {
		this((int) leftSteps, (int) rightSteps);
	}
	private static byte extractBigHalf(int i) {
		return (byte) (i >> 7);
	}
	private static byte extractSmallHalf(int i) {
		i = (i & 127);
		return (byte) i;
	}
	private void resetLeftHalf(int leftSteps) {
//		AssertAbsLessThanShortMax(leftSteps);
//		leftMotorStepsSmallBits = extractSmallHalf(leftSteps);
//		leftMotorStepsBigBits = extractBigHalf(leftSteps);
	}
	private void resetRightHalf(int rightSteps) {
//		AssertAbsLessThanShortMax(rightSteps);
//		rightMotorStepsSmallBits = extractSmallHalf(rightSteps);
//		rightMotorStepsBigBits = extractBigHalf(rightSteps);
	}
	private static int RecombineBigSmall(byte big, byte small) {
		int res = big;
		res = res << 7;
		res = res | small;
		return res;
//		return (big << 8) | small;
	}
	public MotorInstructions() {
		this(0,0);
	}
	public void setToDrawSentinel() {
		sentinel=DRAW_SENTINEL;
	}
	public void setToNudgeSentinel() {
		sentinel=NUDGE_SENTINEL;
	}
	public void setToGoAbsZeroSentinel() {
		sentinel=GO_ABS_ZERO_SENTINEL;
	}
	private static void AssertAbsLessThanShortMax(int i) {
		Asserter.assertTrue(Math.abs(i) < Short.MAX_VALUE,"this steps is larger than short max (abs value)" + i);
	}
	public int leftSteps() {
		return leftMotorSteps;
//		return RecombineBigSmall(leftMotorStepsBigBits,leftMotorStepsSmallBits);
	}
	public int rightSteps() {
		return rightMotorSteps;
//		return RecombineBigSmall(rightMotorStepsBigBits,rightMotorStepsSmallBits);
	}
	public void setLeftSteps(int ls) {
//		resetLeftHalf(ls);
		leftMotorSteps = ls;
	}
	public void addLeftSteps(int als) {
//		leftMotorSteps += als;
		setLeftSteps(leftSteps() + als);
	}
	public void setRightSteps(int rs) {
//		resetRightHalf(rs);
		rightMotorSteps = rs;
	}
	public void addRightSteps(int ars) {
		setRightSteps(rightSteps() + ars);
	}
	public float leftSpeed() { return leftSpeed; }
	public float rightSpeed() {return rightSpeed; }
	
	@Override
	public String toString() {
		return "MOTOR INSTRUCTIONS: left steps: " + leftSteps()
		+ " right steps: " + rightSteps() +
		" left speed: " + leftSpeed + " right speed: " + rightSpeed + " left accel: " + leftAcceleration +
		" right accel: " + rightAcceleration;
	}
	public void debug() {
		B.bugln(this.toString());
	}
	public boolean nonZero() {
		return this.leftSteps() != 0 || this.rightSteps() != 0;
	}
	
	public void assertLimits() {
		if (!Settings.AssertLimits) return;
		Asserter.assertTrue(Math.abs(leftSpeed) < Settings.AbsMaxMotorStepsPerSecond, "left speed exceeded " + Settings.AbsMaxMotorStepsPerSecond + " Steps Per S: " +toString());
		Asserter.assertTrue(Math.abs(rightSpeed) < Settings.AbsMaxMotorStepsPerSecond, "right speed exceeded " + Settings.AbsMaxMotorStepsPerSecond + " Steps Per S: " + toString());
		Asserter.assertTrue(Math.abs(leftSpeed) < Settings.StepMaxValue, "left steps exceeded byte max : " +toString());
		Asserter.assertTrue(Math.abs(rightSpeed) < Settings.StepMaxValue, "right steps exceeded byte max : " + toString());
		Asserter.assertTrue(Math.abs(leftSteps()) < Settings.StepMaxValue, " left steps over max " + toString());
		Asserter.assertTrue(Math.abs(rightSteps()) < Settings.StepMaxValue, " right steps over max " + toString());
	}
	
	public void debugBinary() {
////		String lss = Integer.toBinaryString(leftMotorStepsSmallBits);
////		String lbs = Integer.toBinaryString(leftMotorStepsBigBits);
////		String rss = Integer.toBinaryString(rightMotorStepsSmallBits);
////		String rbs = Integer.toBinaryString(rightMotorStepsBigBits);
//		B.bugln(lss);
//		B.bugln(lbs);
//		B.bugln(rss);
//		B.bugln(rbs);
	
	}

}
