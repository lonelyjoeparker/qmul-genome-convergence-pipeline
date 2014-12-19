package uk.ac.qmul.sbcs.evolution.convergence.tests;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DisplayCoordinatesPanel extends JPanel {
	ArrayList<Integer[]> lineCoordinates;
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
