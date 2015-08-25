package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A JPanel that displays a cladogram-type representation of a phylogeny.
 * <p>The phylogeny is represented with two arguments, a list of X1, Y1, X2, Y2 co-ordinates (Integer) used to draw
 * the nodes/branches with Graphics2D.drawLine(); and a list of taxon names (String) to label the tips
 * @see Graphics
 * @see Graphics2D
 * @see JPanel
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class DisplayPhylogenyPanel extends JPanel {
	private int[][] lineCoordinates;		// Specifies the start (X1, Y1) and end (X2, Y2) points in cartesian space of a list of lines. 
	private ArrayList<String> taxonNames;	// Specifies the taxon names in order.
	private int maxBranchesXpos = 0;		// Maximum x-pos (width) in any of the branches, used to position the tip labels safely
	private int names_x_max, names_y_max;	// Maximum x-pos and y-pos of names (tip labels), used to set the Dimension preferredSize
	private Dimension preferredSize;		// Preferred size of this panel
	
	
	/**
	 * No-arg constructor (deprecated - do not use)
	 */
	@Deprecated
	public DisplayPhylogenyPanel(){}
	
	/**
	 * @param coords - Specifies the start (X1, Y1) and end (X2, Y2) points in cartesian space of a list of lines. 
	 */
	public DisplayPhylogenyPanel(ArrayList<Integer[]> coords, ArrayList<String> names) {
		this.lineCoordinates = new int[coords.size()][4];
		for(int i=0;i<lineCoordinates.length;i++){
			Integer[] line = coords.get(i);
			int[] lineArr = {line[0],line[1],line[2],line[3]};
			lineCoordinates[i] = lineArr;
			/* see if we need to update the max X position. 
			 * all branches assumed to be either vertical or 
			 * horixontal, ot at least specifed (top-left, 
			 * bottom-right), so we'll only test X2 not X1.
			 */
			maxBranchesXpos = Math.max(maxBranchesXpos, line[2]);
		}
		taxonNames = names;
		/* Iterate through the names to get max name chars */
		int maxNameChars = 0;
		for(String name:taxonNames){
			maxNameChars = Math.max(maxNameChars, name.length());
		}
		names_x_max = maxBranchesXpos + 10;
		names_y_max = 10 + (taxonNames.size() * 20);
		preferredSize = new Dimension((names_x_max+(maxNameChars*10)),names_y_max+10);	
		this.setPreferredSize(preferredSize);
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		drawNames(g2);
		drawLines(g2, lineCoordinates);
	}
	
	private void drawNames(Graphics2D g2){
		// draw the taxon names and dash-bars
		int names_x = maxBranchesXpos + 10;
		int names_y = 10;
		for(String taxon: taxonNames){
			g2.drawString(taxon, names_x, names_y);
			g2.setColor(Color.LIGHT_GRAY);
			g2.drawLine(0, names_y-5, names_x-5, names_y-5);
			g2.setColor(Color.BLACK);
			names_y += 20;
		}
	}
	
	private void drawLines(Graphics2D g2, int[][] lineCoordinates){
		// draw the tree itself
		for(int[] line:lineCoordinates){
			g2.drawLine(line[0], line[1]+5, line[2], line[3]+5);
		}
	}
}
