package drawbotV3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import drawbotV3.Settings;

public class Motor // implements ActionListener
{
	public static double spoolDiameter=19.2d;
	private double originalStringLength = 0;
	private double currentTotalSteps = 0;
	private double targetSteps;
	public static final int STEPS_PER_REV=Settings.STEPS_PER_REV;
//	public double stepsPerSecond = 10d;
//	public static int MAX_STEPS_PER_SECOND=500;
//	public static int MIN_STEPS_PER_SECOND=10;
	
	public static int MotorSecondinMillis = 20; // speed up time
	private double velocitySteps = 1d;
	
	public int rotationToStringWindDirection = 1;
	
	public Motor(double startStringLength) {
		originalStringLength = startStringLength;
	}
	/*
	 * Motor operation methods
	 */
	public void updateRotationTargetBySteps(double steps) {
		targetSteps = currentTotalSteps + steps * rotationToStringWindDirection;
	}
	public void setVelocitySteps(double vel) {
		velocitySteps = Math.abs(vel);
	}
	public double velocity() { return velocitySteps; }
//	public void startRotating(double stepsPerSecond) {
//		startRotating(stepsPerSecond, 10d);
//	}
//	public void startRotating(double stepsPerSecond, double stepAcceleration) {
//
//	}
	public double direction() {
		if (targetSteps == currentTotalSteps) return 0d;
		if (targetSteps > currentTotalSteps) return 1d;
		return -1d;
	}
	public void spinOneFrame() {
		moveMotor();
	}
	public void teleportToTargetSteps() {
		currentTotalSteps = targetSteps;
	}
	
	private void moveMotor() {
//		currentTotalSteps += Math.min((int) Math.round(velocitySteps), absDistanceToTarget()) * direction();
		currentTotalSteps += Math.min(velocitySteps, absDistanceToTarget()) * direction();
	}
	
	/*
	 * Motor info methods
	 */
	public double stringLength() {
		return originalStringLength + deltaStringLength();
	}
	public double stringLengthWithDeltaSteps(int deltaSteps) {
		return Math.abs(originalStringLength + stringLengthForSteps(deltaSteps + currentTotalSteps));
	}
	public double distanceToTarget() {
		return targetSteps - currentTotalSteps;
	}
	public double absDistanceToTarget() {
		return Math.abs(distanceToTarget());
	}
	public double totalRotationRadians() {
		return (currentTotalSteps /(double) STEPS_PER_REV)*Math.PI*2.0;
	}
	public double currentTotalSteps() { return currentTotalSteps; }
	
	private double deltaStringLength() {
		return stringLengthForSteps(currentTotalSteps);
	}
	public double stringLengthForSteps(double steps) {
		return spoolDiameter * Math.PI * (steps/(double)STEPS_PER_REV ) * rotationToStringWindDirection;
	}

//	@Override
//	public void actionPerformed(ActionEvent e) {
//		moveMotor();
//	}

	
}
