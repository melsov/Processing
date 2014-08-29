package drawbotV3_2;

import java.util.ArrayList;

import drawbotV3_2.Pointt;
import drawbotV3_2.PointtSet;

//import drawbotV2.SVGToPointt.PointtSet;

public class PointtSetGroup 
{
	private ArrayList<PointtSet> pointSets = new ArrayList<PointtSet>();
	
	public void addPointSet(PointtSet pointSet) {
		pointSets.add(pointSet);
	}
	
	public void massagePointSetOrder() {
		if (pointSets.size() == 0) return;
		ArrayList<PointtSet> massaged = new ArrayList<PointtSet> ();
		PointtSet currentSet = removePointtSetClosestTo(Pointt.FloatMinPointSet());
		
		Pointt targetPoint;
		while(pointSets.size() > 0) {
			massaged.add(currentSet);
			targetPoint = currentSet.lastPoint();
			currentSet = removePointtSetClosestTo(targetPoint);
		}
		pointSets = massaged;
		
	}
	private PointtSet removePointtSetClosestTo(Pointt targetPoint) {
		if (pointSets.size() == 0) return null;
		
		int mindex = 0;
		Pointt curPoint = pointSets.get(mindex).getMinPoint(); 
		double minDistance = curPoint.minus(targetPoint).distanceSquared();
		
		for(int i=0; i < pointSets.size() ; ++i) {
			Pointt ps_min = pointSets.get(i).getMinPoint();
			double ps_dist = ps_min.minus(targetPoint).distanceSquared();
			if (ps_dist < minDistance) {
				minDistance = ps_dist;
				mindex = i;
			}
		}
		return pointSets.remove(mindex);
	}
	
	public PointtSet aggregatePointtSet() {
		PointtSet result = new PointtSet();
		
		for (PointtSet ps : pointSets) {
			for (Pointt p : ps.getPoints()) {
				result.addPointt(p);
			}
		}
		
		return result;
	}
	

}
