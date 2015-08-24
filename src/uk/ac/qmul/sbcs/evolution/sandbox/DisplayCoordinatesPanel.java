package uk.ac.qmul.sbcs.evolution.sandbox;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * A JPanel that displays a set of X1, Y1, X2, Y2 co-ordinates using Graphics2D.drawLine()
 * <br/>Intended as sandbox class to demonstrate JPanel extension including overridden paintComponent() method.
 * @see Graphics
 * @see Graphics2D
 * @see JPanel
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class DisplayCoordinatesPanel extends JPanel {
	ArrayList<Integer[]> lineCoordinates;	// Specifies the start (X1, Y1) and end (X2, Y2) points in cartesian space of a list of lines. 
	
	
	/**
	 * 
	 * @param coords - Specifies the start (X1, Y1) and end (X2, Y2) points in cartesian space of a list of lines. 
	 */
	public DisplayCoordinatesPanel(ArrayList<Integer[]> coords) {
		this.lineCoordinates = coords;
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for(Integer[] line:lineCoordinates){
			g2.drawLine(line[0], line[1], line[2], line[3]);
		}
	}
}
