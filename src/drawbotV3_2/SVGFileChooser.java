package drawbotV3_2;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SVGFileChooser {

	Component parent;
	public SVGFileChooser(Component _parent) {
		parent = _parent;
	}
	
	public String getSVGFileFromDialog() {
		String result = null;
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("SVG files", "svg");
	    
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       try {
			System.out.println("You chose to open this file: " +
			        chooser.getSelectedFile().getCanonicalPath()); } catch (IOException e) { e.printStackTrace();}
	       try {
			result = chooser.getSelectedFile().getCanonicalPath(); } catch (IOException e) { e.printStackTrace();}
	    }

		return result;
	}
}
