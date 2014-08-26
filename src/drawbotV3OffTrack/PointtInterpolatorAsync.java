package drawbotV3OffTrack;

import java.util.ArrayList;

import drawbotV3OffTrack.Asserter;
import drawbotV3OffTrack.B;
import drawbotV3OffTrack.CoordVelocity;
import drawbotV3OffTrack.Pointt;
import drawbotV3OffTrack.Settings;
import static java.lang.Math.*;

/*
 * Mostly a port of: https://code.google.com/p/gocupi/
 * 
 * TODO: make point interpolater threadable and give it a ring buffer
 * CONSIDER: little corner radii for right or more acute angles.
 */
public class PointtInterpolatorAsync 
{
	TrapezoidalInterpolator trapezoidalInterpolator = new TrapezoidalInterpolator();
	Pointt origin, destination, nextDestination;
	ArrayList<Pointt> points;
	private int pointsIndex = -1;
	private int outPointsIndex = 0;
	
	public PointtInterpolatorAsync(ArrayList<Pointt> _points) {
		points = _points;
		filterOutClosePoints(points);
	}
	
	public boolean hasNext() { return pointsIndex < points.size(); }

	public ArrayList<CoordVelocity> interpolatePoints() {
		return interpolatePoints(Integer.MAX_VALUE);
	}
	public ArrayList<CoordVelocity> interpolatePoints(int limit) {
		ArrayList<CoordVelocity> coVels = new ArrayList<CoordVelocity>();

//		int startingIndex = outPointsIndex;
		for (;pointsIndex < points.size() - 1;) {
			limit = addInterpolatedCoordVelocity(coVels, limit);
			if (limit == -1) {
				break;
			}
		}
		return coVels;
	}
	private int addInterpolatedCoordVelocity(ArrayList<CoordVelocity> coVels, int limit) {
		if (!trapezoidalInterpolator.hasNext()) {
			
			int pointsIndexOrig = pointsIndex + 1;
			int debug = 0;
			do {
				origin = points.get(pointsIndexOrig);
				pointsIndex++;
				destination = pointsIndex + 1 < points.size() ? points.get(pointsIndex + 1) : origin.plus(new Pointt(.1,.1));
				nextDestination = pointsIndex + 2 < points.size() ? points.get(pointsIndex + 2) : origin;
				if (debug++ > 0) {
					B.bugln("Point Interp: we skipped: over: "+ destination.toString());
				}
			} while(!trapezoidalInterpolator.setUp(origin, destination, nextDestination)) ;
		}
		
		int startingIndex = outPointsIndex;
		while(trapezoidalInterpolator.hasNext()) {
			coVels.add(trapezoidalInterpolator.nextPositionAndVelocity());
			outPointsIndex++;
			if (outPointsIndex > startingIndex + limit) {
				return -1;
			}
			if (!Settings.ShouldInterpolate) break;
		}
		return limit - (outPointsIndex - startingIndex);
	}
	
	private void filterOutClosePoints(ArrayList<Pointt> points) {
		Pointt origin, destination;
		for (int i = 0; i < points.size(); ++i) {
			origin = points.get(i);
			if (i + 1 >= points.size() ) break;
			destination = points.get(i + 1);
			while (destination.minus(origin).distance() < Settings.MinGondolaMoveDistance) {
				points.remove(i + 1);
				if (i + 1 >= points.size() ) break;
				destination = points.get(i + 1);
			}
		}
	}
	
	private void softenCorners(ArrayList<Pointt> points) {
		
	}
	
	public class TrapezoidalInterpolator
	{
		public Pointt origin = new Pointt();
		public Pointt destination = new Pointt();
		public Pointt nextDestination = new Pointt();
		
		public Pointt direction;
		public Pointt nextDirection;
		
		public double distance;
		
		public double entrySpeed;
		public double exitSpeed;
		public double cruiseSpeed;
		
		public double accelTime;
		public double decelTime;
		public double cruiseTime;
		
		public double accelDist;
		public double decelDist;
		public double cruiseDist;
		
		public double acceleration;
		public double time;
		public double slices;
		private int sliceIndex;
		
		public String toString() {
			String times = String.format("Trapezoid interpolator: accelTime: %f cruiseTime: %f decelTime %f ", accelTime, cruiseTime, decelTime);
			String dists = String.format("Distances: accel: %f cruise %f decel: %f ", accelDist, cruiseDist, decelDist);
			String more = String.format("Distance: %f EntrySpeed: %f exitSpeed: %f ", distance, entrySpeed, exitSpeed);
			return times + "\n" + dists + "\n" + more + "\n";
		}
		public void debug() {
			B.bug(this.toString());
		}
		
		public boolean setUp(Pointt _origin, Pointt _destination, Pointt _nextDestination) 
		{
			boolean succeeded = true;
			entrySpeed = exitSpeed; // from last time
			
			destination = _destination;
			origin = _origin;
			nextDestination = _nextDestination;
			
//			destination = _destination.multiply(Settings.REDUCE_STEP_FACTOR);
//			origin = _origin.multiply(Settings.REDUCE_STEP_FACTOR);
//			nextDestination = _nextDestination.multiply(Settings.REDUCE_STEP_FACTOR);
			
			direction = destination.minus(origin);
			distance = direction.distance();
			direction = direction.unitPointt();
			
			// special case of not going anywhere
			if (distance < Settings.MinGondolaMoveDistance ) {
//			if (origin.equals(destination, 0f) ) { // || distance < Settings.MinGondolaMoveDistance ) {
				
				B.bugln("****what we got here?");
				double crawlSpeed = Settings.GondolaCrawlSpeed_MM_S;
				Pointt dir = direction;
				decelTime = Settings.TimeSlice_US;
				accelTime = 0;
				
				if (crawlSpeed > entrySpeed) {
					decelTime = 0;
					accelTime = Settings.TimeSlice_US;
				}
				
				if (origin.equals(destination, 0f)) {
					crawlSpeed = 0.0d;
					dir = new Pointt(0,1);
					decelTime = accelTime = 0;
				}
				
				origin = _origin;
				destination = _destination;
				direction = dir; // new Pointt(0,1); 
//				distance = 0;
				exitSpeed = crawlSpeed; // entrySpeed;
				cruiseSpeed = crawlSpeed; // entrySpeed;
				accelDist = 0;
//				accelTime = 0;
				cruiseDist = 0;
				cruiseTime = 0;
				decelDist = 0;
//				decelTime = 0;
				acceleration = Settings.Acceleration_MM_S2;
				time = 0;
				slices = 1;
				succeeded = false;
				return succeeded;
			}
			nextDirection = nextDestination.minus(destination); //  destination.minus(nextDestination);
			
			if (nextDirection.distance() == 0) { // or pen up != pen up (TODO)
				nextDirection = direction.multiply(-1f); // pretend its a U-turn to make the gondola stop
			} else {
				nextDirection = nextDirection.unitPointt();
			}
			double cosAngle = direction.dot(nextDirection);
			cosAngle = pow(cosAngle, 5); // juice the cosAngle b/c gocupi does it? (helps non-straight lines?)
			exitSpeed = Settings.MaxGondolaSpeed_MM_S * max(cosAngle, 0);
			cruiseSpeed = Settings.MaxGondolaSpeed_MM_S;
			acceleration = Settings.Acceleration_MM_S2;
			
			accelTime = (cruiseSpeed - entrySpeed) / acceleration;
//			accelTime = max(accelTime, 0d);
//			Asserter.assertTrue(accelTime >= 0, "Negative accel time??? " + accelTime + this.toString());
			
			accelDist = 0.5*Settings.Acceleration_MM_S2*accelTime*accelTime + entrySpeed*accelTime; // ((entrySpeed + cruiseSpeed )/2.0) * accelTime;
//			accelDist = max(accelDist, 0d);
//			Asserter.assertTrue(accelDist >= 0, "neg accel dist?: " + accelDist + this.toString());
			
			decelTime = (cruiseSpeed - exitSpeed) / acceleration;
//			Asserter.assertTrue(decelTime >= 0, "Negative decel time??? " + decelTime + this.toString());
//			decelTime = max(decelTime, 0.0);
			
			decelDist = 0.5*-Settings.Acceleration_MM_S2*decelTime*decelTime + cruiseSpeed*decelTime; //((cruiseSpeed + exitSpeed)/2.0) * decelTime;
//			Asserter.assertTrue(decelDist >= 0, "neg decel dist?: " + decelDist + this.toString());
			
			cruiseDist = distance - (accelDist + decelDist);
			cruiseTime = cruiseDist / cruiseSpeed;
			
			// Speed adjusting code entirely copy-pasted from gocupi's interpolator.go'
			// we don't have enough room to reach max velocity, have to calculate what max speed we can reach
			if (distance < accelDist + decelDist) {
				
				// equation derived by http://www.numberempire.com/equationsolver.php from equations:
				// distanceAccel = 0.5 * accel * timeAccel^2 + entrySpeed * timeAccel
				// distanceDecel = 0.5 * -accel * timeDecel^2 + maxSpeed * timeDecel
				// totalDistance = distanceAccel + distanceDecel
				// maxSpeed = entrySpeed + accel * timeAccel
				// maxSpeed = exitSpeed + accel * timeDecel
				decelTime = (Math.sqrt(2) * sqrt(exitSpeed*exitSpeed+entrySpeed*entrySpeed+2*Settings.Acceleration_MM_S2*distance) - 2*exitSpeed) / (2 * Settings.Acceleration_MM_S2);
				cruiseTime = 0;
				cruiseSpeed = exitSpeed + Settings.Acceleration_MM_S2*decelTime;
				accelTime = (cruiseSpeed - entrySpeed) / Settings.Acceleration_MM_S2;
				
				// don't have enough room to accelerate to exitSpeed over the given distance, have to change exit speed
				if (decelTime < 0 || accelTime < 0 )
				{
					succeeded = false;
					B.bugln(String.format("DecelTime %f or accelTime %f < 0", decelTime, accelTime));
					if ( exitSpeed > entrySpeed ) // need to accelerate to max exit speed possible
					{ 
						decelDist = 0;
						decelTime = 0;
						cruiseDist = 0;
						cruiseTime = 0;

						// determine time it will take to travel distance at the given acceleration
						accelTime = (sqrt(entrySpeed*entrySpeed+2*Settings.Acceleration_MM_S2*distance) - entrySpeed) / Settings.Acceleration_MM_S2;
						exitSpeed = entrySpeed + Settings.Acceleration_MM_S2*accelTime;
						cruiseSpeed = exitSpeed;
						accelDist = distance;
					} else { // need to decelerate to exit speed, by changing acceleration
						//b.Bug("Warning, unable to decelerate to target exit speed using acceleration, try adding -slowfactor=2");
						accelDist = 0;
						accelTime = 0;
						cruiseDist = 0;
						cruiseTime = 0;

						// determine time it will take to reach exit speed over the given distance
						decelTime = 2.0 * distance / (exitSpeed + entrySpeed);
						acceleration = (entrySpeed - exitSpeed) / decelTime;
						cruiseSpeed = entrySpeed;
						decelDist = distance;
					}
				} else {
					
					accelDist = 0.5*Settings.Acceleration_MM_S2*accelTime*accelTime + entrySpeed*accelTime;
					cruiseDist = 0;
					decelDist = 0.5*-Settings.Acceleration_MM_S2*decelTime*decelTime + cruiseSpeed*decelTime;
				}
			}
			
			time = accelTime + cruiseTime + decelTime;
//			Asserter.assertTrue(time > (Settings.TimeSlice_US / Settings.ONE_MILLION) * 2, "yikes less than slice time??");
			slices = time / (Settings.TimeSlice_US / Settings.ONE_MILLION);
			sliceIndex = 0;
			return succeeded;
		}
		
		public boolean hasNext() {
			return sliceIndex < (int) slices;
		}
		public CoordVelocity nextPositionAndVelocity() {
			return makePositionAndVelocityAtSlice(sliceIndex++);
		}

		private CoordVelocity makePositionAndVelocityAtSlice(double slice) {
			double sl_time = time * (slice/slices);
			double linear_dist;
			double linear_vel;
			if (sl_time < accelTime) {
				linear_dist = 0.5*Settings.Acceleration_MM_S2*sl_time*sl_time + entrySpeed*sl_time;
				linear_vel = Settings.Acceleration_MM_S2 * sl_time + entrySpeed;
			} else if (sl_time < accelTime + cruiseTime) {
				sl_time -= accelTime;
				linear_dist = accelDist + sl_time * cruiseSpeed;
				linear_vel = cruiseSpeed;
			} else { //decelerating 
				sl_time -= accelTime + cruiseTime;
				linear_dist = accelDist + cruiseDist + 0.5*-Settings.Acceleration_MM_S2*sl_time*sl_time + cruiseSpeed*sl_time;
				linear_vel = cruiseSpeed - Settings.Acceleration_MM_S2 * sl_time;
			}
			//end test
			Pointt coord = origin.plus(direction.multiply(linear_dist));
			Pointt vel = direction.multiply(linear_vel);
			return new CoordVelocity(coord, vel);
		}
	}
}
