package drawbotV3;

//import drawbot.Motor.MotorRotator;

public class MotorOLD1 
{
	public double gearDiameter=20d;
	private double originalStringLength = 0;
	private int currentTotalSteps = 0;
	private int targetSteps;
	public static final int STEPS_PER_REV=200;
	public double stepsPerSecond = 10d;
	public static int MAX_STEPS_PER_SECOND=500;
	public static int MIN_STEPS_PER_SECOND=10;
	MotorRotator mr;
	
	
	
	public MotorOLD1(double startStringLength) {
		originalStringLength = startStringLength;
	}
	/*
	 * Motor operation methods
	 */
	public void updateRotationTargetBySteps(int steps) {
		targetSteps = currentTotalSteps + steps;
	}
	public void startRotating(double stepsPerSecond) {
		startRotating(stepsPerSecond, 10d);
	}
	public void startRotating(double stepsPerSecond, double stepAcceleration) {
		mr = new MotorRotator(stepsPerSecond, (long) stepAcceleration); 
		Thread t = new Thread(mr);
		t.start();
	}
	
	/*
	 * Motor info methods
	 */
	public double stringLength() {
		return Math.abs( originalStringLength + deltaStringLength());
	}
	public int distanceToTarget() {
		return targetSteps - currentTotalSteps;
	}
	public double totalRotationRadians() {
		return (currentTotalSteps /(double) STEPS_PER_REV)*Math.PI*2.0;
	}
	
	private double deltaStringLength() {
		return gearDiameter * Math.PI * (currentTotalSteps/(double)STEPS_PER_REV);
	}
	private class MotorRotator implements Runnable
	{
		public long stepTimeMillis = 5;
		public long acceleration = 30;
		
		public MotorRotator(double stepsPerSecond, long _acceleration) {
			stepTimeMillis = getStepTimeMillis(stepsPerSecond); //  Math.max( (long) (1000.000/stepsPerSecond), 1);
			acceleration = _acceleration;
		}
		
		@Override
		public void run() {
			long currentStepTimeMillis = getStepTimeMillis(MIN_STEPS_PER_SECOND);
			while(currentTotalSteps != targetSteps) {
				currentTotalSteps += currentTotalSteps < targetSteps ? 1 : -1;
				try {
					Thread.sleep(currentStepTimeMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentStepTimeMillis = currentStepTimeMillis - acceleration; 
				currentStepTimeMillis = Math.max(stepTimeMillis,currentStepTimeMillis);
			}
		}
		
		private long getStepTimeMillis(double stepsPerSec_) {
			return Math.max( (long) (1000.000/stepsPerSec_), 1);
		}
		
	}
	
}
