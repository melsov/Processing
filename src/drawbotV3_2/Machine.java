package drawbotV3_2;

import static java.lang.Math.*;
import java.awt.event.KeyEvent;

import drawbotV3_2.CoordVelocity;
import drawbotV3_2.Line;
import drawbotV3_2.Motor;
import drawbotV3_2.MotorInstructions;
import drawbotV3_2.Pointt;
import drawbotV3_2.Settings;


public class Machine 
{
	public float machineWidth = Settings.MACHINE_WIDTH;
	public float machineHeight = 0; 

	private Pointt startPoint = new Pointt(machineWidth/2.0, Settings.GONDOLA_HOME_POINT_HEIGHT_MM);
	private Motor leftMotor;
	private Motor rightMotor;
	
	public Pointt motorLocationL = new Pointt(0,0);
	public Pointt motorLocationR = new Pointt(Settings.MACHINE_WIDTH,0);
	
	public Pointt targetPoint = new Pointt();
	public Pointt departurePoint = new Pointt();
	private Pointt currentPoint = new Pointt();
	private Pointt prevPoint = new Pointt();
	
	public Pointt cheatPoint = new Pointt();
	public Pointt cheatPointForPrev = new Pointt();

	
	public Machine() {
		departurePoint = currentPoint = targetPoint = startPoint.copy();
		leftMotor = new Motor(distanceFromMotorToPoint(startPoint.copy(), false));
		leftMotor.rotationToStringWindDirection = -1;
		rightMotor = new Motor(distanceFromMotorToPoint(startPoint.copy(), true));
		rightMotor.rotationToStringWindDirection = 1;
		
	}
	public MotorInstructions moveToStartPoint() {
		CoordVelocity cv = new CoordVelocity(startPoint.copy(), new Pointt());
		return moveToPoint(cv);
	}
	public MotorInstructions moveToPoint(CoordVelocity covy){
		if (covy.goNowhere) {
			Asserter.assertFalseAndDie("nowhere veloc.");
			return MotorInstructions.GoNowhereInstructions();
		}
		Pointt p = covy.getCoord();
		B.bug("Machine's next point: " + p.toString());
		MotorInstructions nextMotorInstr = getNextMotorInstructions(covy);
		nextMotorInstr = updateMotors(nextMotorInstr, targetPoint, covy); 

		currentPoint = prevPoint = departurePoint = targetPoint;
		targetPoint = p;
		return nextMotorInstr;
	}

	public Pointt startPoint() { return startPoint.copy(); }

	public Pointt penLocation() {
		return penLoc(false);
	}

	public boolean readyForNextPoint() {
		return leftMotor.distanceToTarget() == 0 && rightMotor.distanceToTarget() == 0;
	}
	public boolean oneMotorArrivedFirst() {
		return (leftMotor.distanceToTarget() == 0) != (rightMotor.distanceToTarget() == 0);
	}
	public boolean sameDistanceToTarget() {
		return leftMotor.distanceToTarget() == rightMotor.distanceToTarget();
	}
	private MotorInstructions getNextMotorInstructions( CoordVelocity covy) {
		Pointt p = covy.getCoord();
		double rightSteps = getStepsForMotor(p, true);
		double leftSteps = getStepsForMotor(p, false);
		
		return new MotorInstructions((int)leftSteps,(int) rightSteps); //, (float) step_vel_l, (float) step_vel_r);
	}
	private double motorVelocityWith(Pointt _curPoint, Pointt vel, boolean wantRightMotor) {
		double curDist = distanceFromMotorToPoint(_curPoint, wantRightMotor);
		double distInOneTick = distanceFromMotorToPoint(_curPoint.plus(vel), wantRightMotor);
		return distInOneTick - curDist;
	}
	private double getStepsForMotor(Pointt p, boolean wantRightMotor) {
		Motor motor = getMotor(wantRightMotor);
		double difLength = chordDistanceFromCurrentPointToPoint(p, wantRightMotor);
		double steps = stepsForDistance(difLength, motor); // * motor.rotationToStringWindDirection;
		return steps;

//		motor.updateRotationTargetBySteps(steps);
	}
	private MotorInstructions updateMotors(MotorInstructions mIs, Pointt _curPoint, CoordVelocity covy) {
		leftMotor.updateRotationTargetBySteps(mIs.leftSteps());
		rightMotor.updateRotationTargetBySteps(mIs.rightSteps());
		
		double step_vel_r = Motor.STEPS_PER_REV * motorVelocityWith(_curPoint, covy.getVelocity(), true)/(PI * Settings.spoolDiameter);
		double step_vel_l = Motor.STEPS_PER_REV * motorVelocityWith(_curPoint, covy.getVelocity(), false)/(PI * Settings.spoolDiameter);
		step_vel_r *= getMotor(true).rotationToStringWindDirection;
		step_vel_l *= getMotor(false).rotationToStringWindDirection;
		
		MotorInstructions resMis = new MotorInstructions((int)leftMotor.distanceToTarget(),(int)rightMotor.distanceToTarget(),(float)step_vel_l,(float)step_vel_r);
		resMis.coVelDebug = covy;
		
		leftMotor.teleportToTargetSteps();
		rightMotor.teleportToTargetSteps();
		return resMis;
	}
	
	public Line departureToTargetLine() {
		return new Line(departurePoint, targetPoint);
	}
	public Line prevToCurrentLine() {
		return new Line(prevPoint, currentPoint);
	}
	private double stepsForDistance(double dist, Motor motor) {
		return (Motor.STEPS_PER_REV * dist/(Math.PI * Settings.spoolDiameter));
	}
	public double testStringDistanceFromCurrentPointToPoint(Pointt p, boolean wantRightMotor) {
		return chordDistanceFromCurrentPointToPoint(p, wantRightMotor); // for testing
	}
	private double chordDistanceFromCurrentPointToPoint(Pointt p, boolean wantRightMotor) {
		return distanceFromMotorToPoint(p, wantRightMotor) - distanceFromMotorToPoint(penLoc(false), wantRightMotor); 
	}
	public double testDistanceFromMotorToPoint(Pointt p, boolean wantRightMotor) {
		return distanceFromMotorToPoint(p, wantRightMotor); // for testing
	}
	private double distanceFromMotorToPoint(Pointt p, boolean wantRightMotor) {
		return p.distanceFrom(motorLocation(wantRightMotor));
	}
	private Pointt motorLocation(boolean wantRight) {
		if (wantRight) return motorLocationR;
		return motorLocationL;
	}
	private Pointt penLoc(boolean baseOffRightMotorLength) {
		double lAng = angleForSide(baseOffRightMotorLength);
		double len = baseOffRightMotorLength ? rightMotor.stringLength() : leftMotor.stringLength();
		double yy = len * Math.sin(lAng);
		double xx = len * Math.cos(lAng);
		if (baseOffRightMotorLength) xx = motorLocationR.x - xx;
		return new Pointt(xx,yy);
	}
	public double testAngleForSide(boolean wantRightSide) { //public for testing
		return angleForSide(wantRightSide);
	}
	/*
	 * apply the law of cosines...
	 */
	private double angleForSide(boolean wantRightSide) { 
		double opSide =  rightMotor.stringLength();
		double nearSide = leftMotor.stringLength();
		if (wantRightSide) {
			double temp = opSide;
			opSide = nearSide;
			nearSide = temp; 
		}
		return Math.acos((machineWidth*machineWidth + nearSide*nearSide - opSide*opSide)/(2*machineWidth*nearSide));
	}
	
	public Motor getMotor(boolean right) //for testing
	{
		if (right) return rightMotor;
		return leftMotor;
	}
	
	public void doKeyPressed(KeyEvent e) {
//		int code = e.getKeyCode();
//		if (code == KeyEvent.VK_P) {
//			pause=true;
//		} else if (code == KeyEvent.VK_N){
//			advanceOne=true;
//			pause=false;
//		} else if (code == KeyEvent.VK_R){
//			advanceOne=false;
//			pause=false;
//		}
	}

}
