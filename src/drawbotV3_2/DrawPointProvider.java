package drawbotV3_2;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import drawbotV3_2.Asserter;
import drawbotV3_2.B;
import drawbotV3_2.CoordVelocity;
import drawbotV3_2.DrawBot;
import drawbotV3_2.Pointt;
import drawbotV3_2.SVGToPointt;
import drawbotV3_2.Settings;


public class DrawPointProvider {
	private ArrayList<Pointt> points = new ArrayList<Pointt>();
	public final int DRAWPOINT_QUEUE_SIZE = 30000;
	private volatile LinkedBlockingQueue<CoordVelocity> cvs;
	private ArrayList<CoordVelocity> cvList = new ArrayList<CoordVelocity>();
	PointtInterpolatorAsync pointInterpolator;
	private int curIndex;
	private Pointt startPoint;
	private CoordVelocity currentCoordVelocity = new CoordVelocity();
	private SVGFileChooser svgFileChooser;
	private HashMap<String, String> defaultFiles;
	AsyncDrawPoints asyncDrawPoints;
	
	public enum NudgeMode {
		OFF (new Pointt(0,0)),
		NOWHERE (new Pointt(0,0)),
		UP (new Pointt(0, -1)), 
		DOWN (new Pointt(0, 1)),
		LEFT (new Pointt (-1, 0)),
		RIGHT (new Pointt(1, 0));
		
		public final Pointt nudge;
		public final float nudgeSpeed = .1f;
		NudgeMode(Pointt _nudge) {
			nudge = _nudge;
		}
		public CoordVelocity getCoordVelocity(Pointt pointt) {
			return new CoordVelocity( pointt.plus(nudge.multiply(nudgeSpeed)), new Pointt(nudgeSpeed, nudgeSpeed));
		}
	}
	NudgeMode nudgeMode = NudgeMode.OFF;
	
	public DrawPointProvider(Pointt _startPoint, SVGFileChooser _svgFileChooser) {
		svgFileChooser = _svgFileChooser;
		startPoint = _startPoint.copy();
	}
	
	private void setUpCVSAndPointsThread() {
		setupPoints();
		setupCVS();
		startDrawPointsThread();
	}
	
	private LinkedBlockingQueue<CoordVelocity> getCVS() {
		if (cvs == null) {
			cvs = new LinkedBlockingQueue<CoordVelocity>(DRAWPOINT_QUEUE_SIZE);
			setUpCVSAndPointsThread();
		}
		return cvs;
	}
	private void setupCVS() {
		pointInterpolator = new PointtInterpolatorAsync(points);
		cvList = pointInterpolator.interpolatePoints(DRAWPOINT_QUEUE_SIZE / 6);
		cvs.addAll(cvList);
		
//		for(int i=0; i < 2; ++i)
//			addMorePoints(cvList);
	}
	private void startDrawPointsThread() {
		asyncDrawPoints = new AsyncDrawPoints();
		Thread t = new Thread(asyncDrawPoints);
		t.start();
	}
	private void addMorePoints(ArrayList<CoordVelocity> bigCVList) {

		ArrayList<CoordVelocity> cvlist = pointInterpolator.interpolatePoints(Math.min(cvs.remainingCapacity() - 3, 200));
		if (bigCVList != null) bigCVList.addAll(cvlist);
		for(CoordVelocity cv : cvlist) {
				try { 
//					cvs.put(cv);
					cvs.offer(cv, 50, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					B.bugln("add more points was interrupted");
//					Asserter.assertFalseAndDie("death");
					e.printStackTrace(); 
				}
		}
		try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	public class AsyncDrawPoints implements Runnable
	{
		public AtomicBoolean keepGoing = new AtomicBoolean(true);
		@Override
		public void run() {
			while(keepGoing.get()) {
				addMorePoints(null);
			}
		}
	}
	
	private void setupPoints() {
		boolean wantsShape = DialogBoxer.QuestionWithTwoOptions("Do you want to draw some kind of shape or use an SVG file?"
				, "SVG file", "some shape");
		if (!wantsShape) {
			LoadSVGFile(svgFileChooser.getSVGFileFromDialog());
		} else {
//			paperProportionalRectangleCW(Settings.PAPER_DIMENSIONS.multiply(.15));
//			paperProportionalRectangle(Settings.PAPER_DIMENSIONS.multiply(.15));
//			circle();
//			spiralInnerToOuter();
//			spiral();
			detailedCircle();
//			squareSpiral();
//			upAndDown();
//			upAndDownTwoStepDown();
//			sideSide();
//			diag();
//			jags();
//			shiftPointSetBeyondStartY();
		}
		points.add(0, startPoint.copy());
		points.add(0, startPoint.copy());
	}

	
	public void LoadSVGFile(String fileName) {
		URI svgURI = null;
		File f;
		if (fileName == null) {
			fileName = getDefaultFiles().get("rocky");
		}
		f = new File(fileName);
		if (f.exists())
			svgURI = f.toURI();
		else {
			B.bugln("file didn't work out: " + fileName);
			B.bug("no svg file. death.");
			System.exit(1);
			return;
		}
		SVGToPointt svgToPoint = new SVGToPointt(svgURI);
		svgToPoint.loadFile();
		
		/* calculate drawing bounds */

		int hedgeAmount = (int) (Settings.PAPER_DIMENSIONS.y * .35);
		Pointt hedge = new Pointt(hedgeAmount, hedgeAmount);
		Pointt newMin = Settings.PAPER_UPPER_LEFT_CORNER.copy().plus(hedge.multiply(.5f)); 
		Pointt newMax = newMin.plus(Settings.PAPER_DIMENSIONS).minus(hedge);

		points = svgToPoint.getPointSet().scaleToFitNewMinMax(newMin, newMax);
		
		if (points == null || points.size() == 0) {
			Asserter.assertTrue(false, "null points");
		}
	}

	private void circle() {
		double r = 73; // DrawBot.CanvasHeight/2.0;
		Pointt origin = startPoint.copy();
		origin.y += r;
		int slices = 64;
		for(int i=0; i < 500; ++i) {
			int incr = i % ((int) slices);
			double angle = ((incr)/(double) slices) * PI * .5; 
			Pointt off = new Pointt(r*cos(angle), r*sin(angle));
			points.add(origin.plus(off));
		}
	}
	private void spiral() {
		double r = 145; // DrawBot.CanvasHeight/2.0;
		Pointt origin = startPoint.copy();
		origin.y += r;
		int slices = 8;
		for(int i=0; i < 500; ++i) {
			double angle = (Math.pow(i, 1d)/(double) slices) * PI * 2;
			Pointt off = new Pointt(r*cos(angle), -r*sin(angle));
			points.add(origin.plus(off));
			r *= .99;
//			if (r < .0001) break;
		}
	}
	private void detailedCircle() {
		double r = 145; // DrawBot.CanvasHeight/2.0;
		Pointt origin = startPoint.copy();
		origin.x += r;
		int slices = 12;
		double startAngle = PI;
		for(int i=0; i < slices; ++i) {
			double angle = startAngle + (i/(double) slices) * PI * 2;
			Pointt off = new Pointt(r*cos(angle), -r*sin(angle));
			points.add(origin.plus(off));
		}
	}
	private void spiralInnerToOuter() {
		double r = 18; // DrawBot.CanvasHeight/2.0;
		double minr = r * .1;
		Pointt origin = startPoint.copy();
		origin.y += r;
		origin.x -= Settings.MACHINE_WIDTH * .2;
		int slices = 36;
		for(int i=0; i < 500; ++i) {
			double angle = (Math.pow(i, 1d)/(double) slices) * PI * 2;
			Pointt off = new Pointt(minr*cos(angle), -minr*sin(angle));
			points.add(origin.plus(off));
			minr = Math.min(r, minr * 1.01);
			if (!(minr < r) && Math.abs(off.y + 1) < .01) break;
		}
	}
	private void squareSpiral() {
		double r = 45; // DrawBot.CanvasHeight/2.0;
		double rmin = 35;
		Pointt origin = startPoint.copy();
		origin.y += r + 40 ;
		int slices = 4;
		points.add(startPoint.copy());
		for(int i=0; i < 50; ++i) {
			double angle = (i/(double) slices) * PI * 2;
			Pointt off = new Pointt(rmin*cos(angle), -rmin*sin(angle));
			points.add(origin.plus(off));
//			points.add(startPoint.copy());
			if (i % 4 == 0) {
				rmin += .5;
				off = new Pointt(rmin*cos(angle), -rmin*sin(angle));
				points.add(origin.plus(off));
			}
			if (!(rmin < r)) break;
		}
	}
	private void upAndDown() {
		Pointt origin = startPoint.copy(); // new Pointt(DrawBot.CanvasWidth/2.0, DrawBot.CanvasHeight/3.0);
		for(int i=0; i < 40; ++i) {
			Pointt off = new Pointt(0,(i%2==0)? 100 :  0);
			points.add(origin.plus(off));
		}
	}
	private void upAndDownTwoStepDown() {
		Pointt origin = startPoint.copy(); 
		for(int i=0; i < 40; ++i) {
			Pointt off = new Pointt(0,0);
			Pointt off1 = new Pointt(0,25);
			Pointt off2 = new Pointt(0, 50);
			Pointt off3 = new Pointt(0, 75);
			Pointt off4 = new Pointt(0, 100);
			points.add(origin.plus(off));
			points.add(origin.plus(off1));
			points.add(origin.plus(off2));
			points.add(origin.plus(off3));
			points.add(origin.plus(off4));
			points.add(origin.plus(off3));
			points.add(origin.plus(off2));
			points.add(origin.plus(off1));
		}
	}
	private void sideSide() {
		double amount = Settings.PAPER_DIMENSIONS.x * .4f;
		Pointt origin = startPoint.copy();
		for(int i=0; i < 3; ++i) {
			Pointt off = new Pointt((i%2==0)?-amount : amount, 0);
			points.add(origin.plus(off));
		}
		points.add(origin.copy());
	}
	private void diag() {
		double amount = 100;
		Pointt origin = startPoint.copy();
		for(int i=0; i < 40; ++i) {
			Pointt off = new Pointt((i%2==0)?-amount :  amount, (i%2==0)?-amount :  amount);
			points.add(origin.plus(off));
		}
	}
	private void jags() {
		Pointt origin = new Pointt(Settings.MACHINE_WIDTH/2.0 * 0.0  , DrawBot.CanvasHeight/2.0 );
		for(int i=0; i < 20; ++i) {
			Pointt off = new Pointt((i/20.0) * Settings.MACHINE_WIDTH, (i%2==0)? -200 : 200);
			points.add(origin.plus(off));
		}
	}
	private void paperProportionalRectangle(Pointt paperDimensions) {
		int width = (int) paperDimensions.x, height = (int) paperDimensions.y;
		Pointt origin = startPoint.copy();
		float half_width = width * .5f;
		Pointt upperLeft = new Pointt(origin.x - half_width, origin.y);
		points.add(upperLeft);
		Pointt lowerLeft = upperLeft.copy();
		lowerLeft.y += height;
		points.add(lowerLeft);
		Pointt lowerRight = lowerLeft.copy();
		lowerRight.x += width;
		points.add(lowerRight);
		Pointt upperRight = lowerRight.copy();
		upperRight.y = origin.y;
		points.add(upperRight);
		Pointt returnPoint = origin.copy();
		returnPoint.x += 30;
		points.add(returnPoint);
		points.add(origin.copy());
		points.add(origin.copy());
		
	}
	private void paperProportionalRectangleCW(Pointt paperDimensions) {
		int width = (int) paperDimensions.x, height = (int) paperDimensions.y;
		Pointt origin = startPoint.copy();
		float half_width = width * .5f;
		Pointt upperLeft = new Pointt(origin.x - half_width, origin.y);
		Pointt lowerLeft = upperLeft.copy();
		lowerLeft.y += height;
		Pointt lowerRight = lowerLeft.copy();
		lowerRight.x += width;
		Pointt upperRight = lowerRight.copy();
		upperRight.y = origin.y;
		Pointt returnPoint = origin.copy();
		returnPoint.x -= 30;
		points.add(upperRight);
		points.add(lowerRight);
		points.add(lowerLeft);
		points.add(upperLeft);
		points.add(returnPoint);
		points.add(origin.copy());
		points.add(origin.copy());
		
	}

	int gotNullCount = 0;
	
	public CoordVelocity nextPoint() {
		if (!nudgeMode.equals(NudgeMode.OFF)) {
			B.bug(currentCoordVelocity.getCoord());
			currentCoordVelocity = nudgeMode.getCoordVelocity(currentCoordVelocity.getCoord());
			return currentCoordVelocity;
		}
		
		if (gotNullCount > 20 && pointInterpolator != null && !pointInterpolator.hasNext()) {
			asyncDrawPoints.keepGoing.set(false);
		}
		
		curIndex++;
		CoordVelocity result = getCVS().poll();
		
		if (result == null) {
			currentCoordVelocity = new CoordVelocity(currentCoordVelocity.getCoord(), currentCoordVelocity.getVelocity().multiply(.01f));
			++gotNullCount;
			B.bugln("giving a dummy coord: null count: " + gotNullCount);
		} else {
			currentCoordVelocity = result;
		}

		return currentCoordVelocity;
	}
	public int getCurrentIndex() {
		return curIndex;
	}
	public double percentDone() {
		if (pointInterpolator == null) return 0d;
		return pointInterpolator.percentDone(); // (curIndex/(double)cvList.size());
	}
	public ArrayList<CoordVelocity> getUninterpolatedPointts() {
		return cvList;
//		ArrayList<CoordVelocity> result = new ArrayList<CoordVelocity>(points.size());
//		for (Pointt p : points) {
//			result.add(new CoordVelocity(p, new Pointt()) );
//		}
//
//		return result;
	}
	
	private HashMap<String, String> getDefaultFiles() {
		if (defaultFiles == null) {
			defaultFiles = new HashMap<String,String>();
			defaultFiles.put("rocky", "/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/rockyPS.svg");
			defaultFiles.put("test_shapes", "/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/test_shapes.svg");
			defaultFiles.put("snuffles", "/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/mr-snuffles.svg");
			defaultFiles.put("gray","/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/gray.svg");
			defaultFiles.put("eye","/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/eye.svg");
			defaultFiles.put("rick_and_morty","/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/rick_and_morty.svg");
			defaultFiles.put("house_mass","/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/ps_house_mass.svg");
		}
		return defaultFiles;
	}
}
