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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import uk.ac.qmul.sbcs.evolution.convergence.PhylogenyConvergenceContext;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogenyPanel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.PhylogeniesController.PhylogeniesRowListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;

public class PhylogeniesView extends JComponent {
	private JPanel mainPanel, selectionPanel, displayPanel;
	private JTextArea textTreeDisplay;
	private JTable phylogeniesTable;
	private JScrollPane phylogenyTableScrollPane;
	private JFileChooser fileChooser = new JFileChooser("Choose a phylogeny");
	private JFileChooser directoryChooser = new JFileChooser("Choose a directory");
	private JScrollPane renderPhylogeny;
	private JScrollPane textTreeScrollPane = new JScrollPane();
	private JComboBox convergenceContextComboBox;
	
	/**
	 * Default no-arg constructor
	 */
	public PhylogeniesView(){
		
		/* The selection panel will hold the table, and text area 
		 * holding (selectable) current tree string - they share 
		 * a panel.
		 */
		selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel,BoxLayout.Y_AXIS));
		phylogeniesTable = new JTable();
		phylogenyTableScrollPane = new JScrollPane(phylogeniesTable);
		phylogenyTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		phylogenyTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textTreeDisplay = new JTextArea("\n\n(Tree/phylogeny as text: no trees loaded.)");
		textTreeScrollPane.setViewportView(textTreeDisplay);
		selectionPanel.add(phylogenyTableScrollPane);
		selectionPanel.add(textTreeScrollPane);
		//selectionPanel.add(new JLabel("Selection/table here"));
		
		// The phylogeny visualisation 
		displayPanel = new JPanel();
		displayPanel.setLayout(new BoxLayout(displayPanel,BoxLayout.Y_AXIS));
		//displayPanel.add(new JLabel("Phylogeny display here"));
		renderPhylogeny = new JScrollPane(new TreeGraphicsDefaultDisplay());
		displayPanel.add(renderPhylogeny);
		//selectionPanel.setPreferredSize(new Dimension(320, 30));

		// The main panel holds everything
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
		mainPanel.add(selectionPanel);
		mainPanel.add(displayPanel);
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	/**
	 * Update the display of the current tree (text/string display currently)
	 * @param updateTextTreeString
	 */
	public void setTextTreeDisplay(String updateTextTreeString){
		textTreeDisplay.setText(updateTextTreeString);
	}
	
	/**
	 * returns the JScrollPane containing the display phylogeny.
	 * @return
	 */
	public JScrollPane getRenderedPhylogeny(){
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
	 * Get a file chooser (files enabled).
	 * @return
	 */
	public JFileChooser getFileChooser(){
		return fileChooser;
	}
	
	/**
	 * Get a file chooser (directories enabled).
	 * @return
	 */
	public JFileChooser getDirectoryChooser(){
		return directoryChooser;
	}
	
	/**
	 * Sets the TableModel used to pick / update phylogenies list
	 * @param newTableModel a PhylogenyModel (superclass AbstractTableModel)
	 */
	public void addTable(PhylogeniesModel newTableModel){
		selectionPanel.remove(phylogenyTableScrollPane);
		phylogeniesTable = new JTable(newTableModel);
		phylogeniesTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
		phylogeniesTable.setFillsViewportHeight(true);
		phylogeniesTable.setRowSelectionAllowed(true);
		phylogeniesTable.setColumnSelectionAllowed(true);
		phylogeniesTable.setCellSelectionEnabled(true);
		phylogeniesTable.setAutoCreateRowSorter(true);
		phylogeniesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		/*
		 * table view fiddling ported from AlignmentsView.addTable() and not checked/implemented
		 * 
		 *
		 */
		phylogenyTableScrollPane = new JScrollPane(phylogeniesTable);
		phylogenyTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		phylogenyTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		selectionPanel.add(phylogenyTableScrollPane);
		
		/* attempt to set the phlogeny convergence context editor */
		TableColumn convergenceContextColumn = phylogeniesTable.getColumnModel().getColumn(phylogeniesTable.getColumnCount()-1);
		convergenceContextComboBox = new JComboBox();
		for(PhylogenyConvergenceContext contextEnum:PhylogenyConvergenceContext.values()){
			convergenceContextComboBox.addItem(contextEnum);
		}
		convergenceContextColumn.setCellEditor(new DefaultCellEditor(convergenceContextComboBox));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click to set convergence context");
        convergenceContextColumn.setCellRenderer(renderer);
        
        resetLayoutSpacings();
	}
	
	public void resetLayoutSpacings(){
		Dimension mainPanelDimension = mainPanel.getSize();
		int halfAvailableWidth = mainPanelDimension.width / 2;
		Dimension halfPanelDimension = new Dimension(halfAvailableWidth,mainPanelDimension.height);
		selectionPanel.setPreferredSize(halfPanelDimension);
		displayPanel.setPreferredSize(halfPanelDimension);
	}
	
	public JTable getTable(){
		return phylogeniesTable;
	}
	
	public class TreeGraphicsDefaultDisplay extends JPanel{
		
		Graphics2D g2d;
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g2d = (Graphics2D)g;
			g2d.drawString("\n\n(Tree/phylogeny display: no trees loaded.)",0,50);
			/* pratting about
			g2d.setBackground(Color.cyan);
			g2d.fillRect(0, 0, 100, 100);
			g2d.setColor(Color.RED);
			g2d.setColor(Color.GREEN);
			g2d.setStroke(new BasicStroke(10));
			g2d.drawLine(25, 90, 125, 110);
			*/
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
		try {
			displayPanel.remove(renderPhylogeny);
			renderPhylogeny = null;
			renderPhylogeny = dp.getDisplayedPhylogeny();
			displayPanel.add(renderPhylogeny);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		displayPanel.revalidate();
		displayPanel.repaint();
	}

	public void addRowSelectionListener(PhylogeniesRowListener phylogeniesRowListener) {
		phylogeniesTable.getSelectionModel().addListSelectionListener(phylogeniesRowListener);		
	}
}
