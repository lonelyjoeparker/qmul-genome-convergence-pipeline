package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A class demonstrating basic method to render alignments with block colours using java.awt.Graphics2D canvas instead of style sheets.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class ColouredCharPanelWireframe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("Sandbox to explore char painting using awt.Graphics2D");
		frame.add(new ColouredCharPanelWireframe().new ColouredCharPanel());
		frame.setSize(300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public class ColouredCharPanel extends JPanel{
		
		public ColouredCharPanel(){}
		
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D)g;
			// text to display
			String displayText = "This is text drawn by string";
			char[] displayChar = displayText.toCharArray();
			// draw as string
			g2.drawString(displayText, 10, 10);
			// simple draw as chars
			g2.drawChars(displayChar, 10, displayChar.length-10, 10, 40);
			// try a bg colour shift
			g2.setBackground(Color.BLUE);
			g2.drawString(displayText, 10, 50);
			// try to draw chars with a background
			drawCharsWithBackground(g2, displayChar, 10, 70);
		}
		
		public void drawCharsWithBackground(Graphics2D g2d, char[] content, int x, int y){
			int length = content.length;
			Color last = Color.CYAN;
			for(int i=0;i<length;i++){
				g2d.setColor(last);
				g2d.fillRect(x-1, y-12, 10, 15);
				g2d.setColor(Color.BLACK);
				g2d.drawChars(content, i, 1, x, y);
				if(last == Color.CYAN){
					last = Color.MAGENTA;
				}else{
					last = Color.CYAN;
				}
				x += 10;
			}
		}
	}
}
