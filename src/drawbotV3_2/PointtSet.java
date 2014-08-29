package drawbotV3_2;

import java.util.ArrayList;
import java.util.List;

import drawbotV3_2.Pointt;

public class PointtSet
{
	private List<Pointt> points = new ArrayList<Pointt> ();
	private Pointt minPoint = null;
	private Pointt maxPoint = null;
	
	public List<Pointt> getPoints() {
		return points;
	}
	public Pointt firstPoint() {
		if (points.size() == 0) return null;
		return points.get(0);
	}
	public Pointt lastPoint() {
		if (points.size() == 0) return null;
		return points.get(points.size() -1);
	}		
	public Pointt getMinPoint() {
		return minPoint;
	}
	
	public Pointt getMaxPoint() {
		return maxPoint;
	}
	public void reversePoints() {
		List<Pointt> reversed = new ArrayList<Pointt> ();
		for (int i = points.size() - 1; i >= 0; --i) { reversed.add(points.get(i)); }
		points = reversed;
	}
	public void addPointt(Pointt p) {
		updateMin(p);
		updateMax(p);
		points.add(p);
	}
	
	private void updateMin(Pointt p) {
		minPoint = Pointt.Min(p, minPoint);
	}
	private void updateMax(Pointt p) {
		maxPoint = Pointt.Max(p, maxPoint);
	}
	
	public ArrayList<Pointt> scaleToFitNewMinMax(Pointt newMin, Pointt newMax) {
		Pointt newDim = newMax.minus(newMin);
		Pointt oldDim = maxPoint.minus(minPoint);
		
		double proportion = newDim.y/oldDim.y;
		double proportionx = newDim.x /oldDim.x;
		proportion = (proportion > proportionx ) ? proportionx : proportion;
		
		for (int i=0; i < points.size(); ++i) {
			Pointt p = points.get(i);
			p = p.minus(minPoint);
			p = p.multiply(proportion);
			p = p.plus(newMin);
			points.set(i, p);
		}
		minPoint = newMin;
		maxPoint = newMax;
		
		return (ArrayList<Pointt>) points;
		
	}
}