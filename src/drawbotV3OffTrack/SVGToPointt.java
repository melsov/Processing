package drawbotV3OffTrack;


import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
 
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;

import drawbotV3OffTrack.Pointt;
import drawbotV3OffTrack.PointtSet;
import drawbotV3OffTrack.PointtSetGroup;

/*
 * Converts SVG data to a flat list of Pointts.
 * Pointt is a custom vector class that could
 * be substituted without too much work.
 * Uses the SVG Salamander library.
 * Point conversion technique from: http://blog.gemserk.com/2011/03/03/svg-path-traversal-in-java/
 */
public class SVGToPointt {
	
//	PointtSet pointSet = new PointtSet();
	PointtSetGroup pointSetGroup = new PointtSetGroup();
	private URI fileUri;

	public SVGToPointt(URI _fileUri) {
		fileUri = _fileUri;
	}
	
	private void LoadPointsFromSVG(URI _fileUri) {  
		if (_fileUri == null) return; // null;
	    SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(_fileUri);
	    GetAllPoints(diagram);
	}  
	
	private void GetAllPoints(SVGDiagram diagram) {
		ArrayList<SVGElement> elems = new ArrayList<SVGElement>();
	    childrenRecursive(diagram.getRoot(), elems);
	    for (SVGElement el : elems) {
	    	com.kitfox.svg.ShapeElement svgPath = CastToShapeElement(el);
	    	if (svgPath != null) {
	    		PointtSet _pointSet = new PointtSet();
	    		addPathPoints(svgPath, _pointSet);
	    		pointSetGroup.addPointSet(_pointSet);
	    	}
	    }	    
	}

	private void childrenRecursive( SVGElement node, ArrayList<SVGElement> result) {
		if (result == null) result = new ArrayList<SVGElement>();
		result.add(node);
		List<SVGElement> children = new ArrayList<SVGElement>();
	    node.getChildren(children);
	    for (int i=0; i < children.size(); ++i) {
			SVGElement childNode = (SVGElement) children.get(i);
			childrenRecursive(childNode, result);
		}
	}
	
	private static void addPathPoints(com.kitfox.svg.ShapeElement pathSVG, PointtSet _pointSet) {
		 // get the AWT Shape  
	    Shape shape = pathSVG.getShape();      
	    // iterate over the shape using a path iterator discretizing with distance 0.001 units     
	    PathIterator pathIterator = shape.getPathIterator(null, 0.001d);  
	    float[] coords = new float[2];  
	    while (!pathIterator.isDone()) {
		    pathIterator.currentSegment(coords);
		    Pointt p = new Pointt(coords[0], coords[1]);
		    _pointSet.addPointt(p);
		    pathIterator.next();
	    }
	}
	
	private com.kitfox.svg.ShapeElement CastToShapeElement(SVGElement node) {
		if (node instanceof com.kitfox.svg.Group) {
			return null; // don't want groups (members more useful?)
		}
		if (node instanceof com.kitfox.svg.Tspan) {
			return null;
		}
		com.kitfox.svg.ShapeElement result = null;
		try{
			result = (com.kitfox.svg.ShapeElement) node;
		} catch (ClassCastException cce) {}
		return result;
	}
	

	public void loadFile() {
		LoadPointsFromSVG(fileUri);
	}

	public PointtSet getPointSet() {
//		pointSetGroup.massagePointSetOrder();
		return pointSetGroup.aggregatePointtSet();
	}

}
