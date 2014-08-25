package drawbotV3;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import drawbotV3.Asserter;
import drawbotV3.B;
import drawbotV3.CoordVelocity;
import drawbotV3.DrawBot;
import drawbotV3.Pointt;
import drawbotV3.PointtInterpolator;
import drawbotV3.SVGToPointt;
import drawbotV3.Settings;

public class DrawPointProvider {
	private ArrayList<Pointt> points = new ArrayList<Pointt>();
	private ArrayList<CoordVelocity> cvs = new ArrayList<CoordVelocity>();
	private int curIndex;
	private Pointt startPoint;
	
	public DrawPointProvider(Pointt _startPoint) {
		startPoint = _startPoint.copy();
		setupPoints();
		setupCVS();
	}
	private void setupCVS() {
		PointtInterpolator pointInterpolator = new PointtInterpolator();
		cvs = pointInterpolator.interpolatePoints(points);
	}
	private void setupPoints() {
//		circle();
//		spiralInnerToOuter();
		LoadSVGFile(null);
//		spiral();
//		squareSpiral();
//		upAndDown();
//		upAndDownTwoStepDown();
//		sideSide();
//		diag();
//		jags();
//		shiftPointSetBeyondStartY();
		points.add(0, startPoint.copy());
		points.add(0, startPoint.copy());
	}
	public void LoadSVGFile(String fileName) {
		URI svgURI = null;
//		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/test_shapes.svg");
//		File f = new File("/Users/didyouloseyourdog/Desktop/bitmaps/path_find_small.svg");
//		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/gray.svg");
		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/mr-snuffles.svg");
//		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/eye.svg");
//		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/rick_and_morty.svg");
//		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/ps_house_mass.svg");
//		File f = new File("/Users/didyouloseyourdog/all_codes/TEST_EC_SPACE/JavaChallenges/rockyPS.svg");
		if (f.exists())
			svgURI = f.toURI();
		else {
			B.bug("no svg file. death.");
			System.exit(1);
			return;
		}
		SVGToPointt svgToPoint = new SVGToPointt(svgURI);
		svgToPoint.loadFile();
		
		/* calculate drawing bounds */
		double halfGondolaDim_mm = 74;
		double halfMachineW = Settings.MachineWidth / 2.0; //  DrawBot.CanvasWidth / 2.0;
		double halfD = halfMachineW * .5;
		Pointt halfDim = new Pointt(halfD,halfD);
		Pointt newMin = startPoint.copy(); 
		int hedge_width = 50;
		newMin.x = (float) (halfGondolaDim_mm + hedge_width); 
		Pointt newMax = startPoint.copy().plus(halfDim);
		newMax.x = (float) (Settings.MachineWidth - halfGondolaDim_mm - hedge_width);

		points = svgToPoint.getPointSet().scaleToFitNewMinMax(newMin, newMax);
		
		if (points == null || points.size() == 0) {
			Asserter.assertTrue(false, "null points");
		}
		
		
//		points.add(0, newMax);
//		points.add(0, new Pointt(newMin.x, newMax.y));
//		points.add(0, newMin); //test purposes
//		points.add(0, new Pointt(newMax.x, newMin.y));
//		points.add(0, newMax);

//		B.bug("points size: " + points.size());
//		for(Pointt p : points) B.bugln(p.toString());

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
		double r = 18; // DrawBot.CanvasHeight/2.0;
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
	private void spiralInnerToOuter() {
		double r = 18; // DrawBot.CanvasHeight/2.0;
		double minr = r * .1;
		Pointt origin = startPoint.copy();
		origin.y += r;
		origin.x -= Settings.MachineWidth * .2;
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
		double amount = Settings.MachineWidth * .45;
		Pointt origin = startPoint.copy();
		for(int i=0; i < 20; ++i) {
			Pointt off = new Pointt((i%2==0)?-amount : amount, 0);
			points.add(origin.plus(off));
		}
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
		Pointt origin = new Pointt(Settings.MachineWidth/2.0 * 0.0  , DrawBot.CanvasHeight/2.0 );
		for(int i=0; i < 20; ++i) {
			Pointt off = new Pointt((i/20.0) * Settings.MachineWidth, (i%2==0)? -200 : 200);
			points.add(origin.plus(off));
		}
	}
//	public Pointt nextPoint() {
//		Pointt res = null;
//		if (curIndex < points.size()) {
//			res = points.get(curIndex);
//		}
//		curIndex++;
//		return res;
//	}
	public CoordVelocity nextPoint() {
		CoordVelocity res = null;
		if (curIndex < cvs.size()) {
			res = cvs.get(curIndex);
		}
		curIndex++;
		return res;
	}
	public int getCurrentIndex() {
		return curIndex;
	}
	public double percentDone() {
		return (curIndex/(double)cvs.size());
	}
	public ArrayList<CoordVelocity> getCoordVels() { return cvs; }
}
