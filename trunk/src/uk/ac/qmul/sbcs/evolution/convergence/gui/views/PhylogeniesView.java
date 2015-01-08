package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.gui.PhylogenyDisplayPanel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.PhylogeniesController.PhylogeniesRowListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;

public class PhylogeniesView extends JComponent {
	private JPanel mainPanel, selectionPanel, displayPanel;
	private JTextArea textTreeDisplay;
	private JTable phylogeniesTable;
	private JScrollPane phylogenyTableScrollPane;
	private JFileChooser chooser = new JFileChooser("Choose a phylogeny");
	private JPanel renderPhylogeny = new TreeGraphicsDisplay();
	private JScrollPane textTreeScrollPane = new JScrollPane();
	
	/**
	 * Default no-arg constructor
	 */
	public PhylogeniesView(){
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		selectionPanel = new JPanel();
		selectionPanel.add(new JLabel("Selection/table here"));
		displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel,BoxLayout.Y_AXIS));
		displayPanel.add(new JLabel("Phylogeny display here"));
		textTreeDisplay = new JTextArea("Trees as text strings");
		textTreeDisplay.setPreferredSize(new Dimension(320, 30));
		textTreeScrollPane.setViewportView(textTreeDisplay);
		displayPanel.add(renderPhylogeny);
		displayPanel.add(textTreeScrollPane);
		selectionPanel.setPreferredSize(new Dimension(320, 30));
		mainPanel.add(selectionPanel);
		mainPanel.add(displayPanel);
	}
	
	/**
	 * Update the display of the current tree (text/string display currently)
	 * @param updateTextTreeString
	 */
	public void setTextTreeDisplay(String updateTextTreeString){
		textTreeDisplay.setText(updateTextTreeString);
	}
	
	/**
	 * returns the JPanel containing the display phylogeny.
	 * @return
	 */
	public JPanel getRenderedPhylogeny(){
		return this.renderPhylogeny;
	}
	
	/**
	 * Get a JComponent to be added to the GlobalView (main JFrame)
	 * @return
	 */
	public JPanel getPanel(){
		return mainPanel;
	}
	
	/**
	 * Get a file chooser.
	 * @return
	 */
	public JFileChooser getFileChooser(){
		return chooser;
	}
	
	/**
	 * Sets the TableModel used to pick / update phylogenies list
	 * @param newTableModel a PhylogenyModel (superclass AbstractTableModel)
	 */
	public void addTable(PhylogeniesModel newTableModel){
		phylogeniesTable = new JTable(newTableModel);
		phylogenyTableScrollPane = new JScrollPane(phylogeniesTable);
		phylogenyTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		phylogenyTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		selectionPanel.add(phylogenyTableScrollPane);
	}
	
	public JTable getTable(){
		return phylogeniesTable;
	}
	
	public class TreeGraphicsDisplay extends JPanel{
		
		Graphics2D g2d;
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g2d = (Graphics2D)g;
			g2d.setBackground(Color.cyan);
			g2d.fillRect(0, 0, 100, 100);
			g2d.setColor(Color.RED);
			g2d.drawString("Tree canvas text string",50,50);
			g2d.setColor(Color.GREEN);
			g2d.setStroke(new BasicStroke(10));
			g2d.drawLine(25, 90, 125, 110);
		}

		public void drawCircle() {
			// TODO Auto-generated method stub
			g2d.setColor(Color.BLUE);
			g2d.fillOval(75, 75, 30, 30);
		}
	}

	public void updatePhylogenyDisplay(DisplayPhylogeny dp) {
		String textTreeString = dp.getTextTreeRepresentation();
		this.setTextTreeDisplay(textTreeString);
		JPanel pdp = dp.getDisplayedPhylogeny();
		try {
			displayPanel.remove(renderPhylogeny);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderPhylogeny = (JPanel)pdp;
		displayPanel.add(renderPhylogeny);
		displayPanel.revalidate();
		displayPanel.repaint();
	}

	public void addRowSelectionListener(PhylogeniesRowListener phylogeniesRowListener) {
		phylogeniesTable.getSelectionModel().addListSelectionListener(phylogeniesRowListener);		
	}
}
