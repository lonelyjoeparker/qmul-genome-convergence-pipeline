package uk.ac.qmul.sbcs.evolution.convergence.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;


public class CirclePanel extends JPanel {

	int x = 0;
	int y = 0;
	int diameter = 100;
	Color colour;
	
	public CirclePanel() {
		// TODO Auto-generated constructor stub
	}

	public CirclePanel(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public CirclePanel(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public CirclePanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public void paintComponent(Graphics g) {
		   super.paintComponent(g);
		   Graphics2D g2d = (Graphics2D)g;
		   // Assume x, y, and diameter are instance variables.
		   Ellipse2D.Double circle = new Ellipse2D.Double(x, y, diameter, diameter);
		   g2d.setColor(colour);
		   g2d.fill(circle);
		}
	
	public void setDiameter(int newScale){
		diameter = 100*newScale;
		this.repaint();
	}

	public void setColour(Color new_color) {
		// TODO Auto-generated method stub
		this.colour = new_color;
		this.repaint();
	}
}
