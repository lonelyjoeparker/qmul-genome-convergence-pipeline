package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

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
	int[][] lineCoordinates;	// Specifies the start (X1, Y1) and end (X2, Y2) points in cartesian space of a list of lines. 
	ArrayList<String> taxonNames;			// Specifies the taxon names in order.
	

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
		}
		this.taxonNames = names;
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		// draw the taxon names and dash-bars
		int guessSafeXposLimit = Math.round(((float)taxonNames.size())*0.75f); // the theoretical Xpos limit is #taxa * increment (20 here), but this is rarely reached except with *very* ladderlike trees, so we'll use 3/4 of this distance, giving a much nicer, more compact, tree.
		int names_x = guessSafeXposLimit*20;
		int names_y = 10;
		for(String taxon: taxonNames){
			g2.drawString(taxon, names_x, names_y);
			g2.setColor(Color.LIGHT_GRAY);
			g2.drawLine(0, names_y-5, names_x-5, names_y-5);
			g2.setColor(Color.BLACK);
			names_y += 20;
		}
		
		// draw the tree itself
		for(int[] line:lineCoordinates){
			g2.drawLine(line[0], line[1]+5, line[2], line[3]+5);
		}
		Dimension size = new Dimension(names_x+200,names_y+50);
		this.setPreferredSize(size);
	}
	
}
