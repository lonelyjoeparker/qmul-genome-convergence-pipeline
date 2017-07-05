package uk.ac.qmul.sbcs.evolution.convergence.gui.views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.*;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AlignmentsModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.SummaryStatisticsTableModel;

import org.apache.commons.lang3.ArrayUtils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.Histogram;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;


public class AlignmentsView extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3925504665016275161L;
	private JPanel panel, subPanel, buttonPanel;
	private JFileChooser fc = new JFileChooser();	// file chooser for single files
	private JFileChooser dc = new JFileChooser();	// file chooser for directories
	private JButton addAlignmentsButton;		// add alignment
	private JButton removeAlignmentsButton;		// remove selected alignment
	private JButton textDumpButton;				// dump all data to text at 'alignment_descriptive_stats.tdf' in the selected dir
	private JButton tableDefinitionButton;		// show definitions of alignment stats
	private JButton showPlottingFrameButton;	// show the plotting frame
	private JTable alignmentsTable;
	private JScrollPane alignmentsScrollPane;
	private JScrollPane sequencePaneNT;
	private JScrollBar scrollBarNT;
	private JScrollPane sequencePaneAA;
	private JScrollBar scrollBarAA;
	public DefinitionsFrame definitionFrame;
	public PlottingFrame plottingFrame;
	
	public AlignmentsView() {
		// set up panels
		panel = new JPanel(new GridLayout(3,1));
		subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		// set up buttons
		addAlignmentsButton = new JButton("Add single alignment");
		addAlignmentsButton.setToolTipText("Adds a single alignment. Use 'File>Add Alignments..>Directory' to add whole directories in batch mode.");
		removeAlignmentsButton = new JButton("Remove single alignment");
		removeAlignmentsButton.setToolTipText("Removes the currently selected alignment.");
		textDumpButton = new JButton("Export statistics to text");
		textDumpButton.setToolTipText("Selects a directory, then creates a file 'alignment_descriptive_stats.tdf' and dumps alignment statistics into it.");
		tableDefinitionButton = new JButton("Show column definitions");
		tableDefinitionButton.setToolTipText("Display alignment statistics' definitions.");
		showPlottingFrameButton = new JButton("Show plots");
		showPlottingFrameButton.setToolTipText("Display alignment statistics' plots as scatter or histogram plots.");
		// add buttons to panels
		buttonPanel.add(addAlignmentsButton);
		buttonPanel.add(removeAlignmentsButton);
		buttonPanel.add(textDumpButton);
		buttonPanel.add(tableDefinitionButton);
		buttonPanel.add(showPlottingFrameButton);
		subPanel.add(buttonPanel);
		panel.add(subPanel);
		dc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// JFrame to display table's statistics' definition
		definitionFrame = new DefinitionsFrame();
		// JFrame to display plots
		plottingFrame = new PlottingFrame();
		plottingFrame.setVisible(true);
	}
	
	/**
	 * A JFrame holding plotting (scatter, histogram) info
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since 3 Jul 2017
	 * @version 0.1
	 */
	public class PlottingFrame extends JFrame{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4331637424478573163L;
		JPanel mainPanel, infoPanel, statsTablePanel, optionsPanel, chartPanel;
		JLabel label;
		JTable statsTable;
		JScrollPane statsTableScrollPane, wholeViewScrollPane;
		String internalText = "Some plotting data.";
	    String currentScatterSeriesName = null;
	    String currentHistogramSeriesName = null;
		XYChart scatterChart;
		CategoryChart histogramChart;
		XChartPanel scatterChartPanel, histogramChartPanel;
	    JCheckBox plotLogX, plotLogY, collectOverlay;
	    boolean doPlotLogX, doPlotLogY, doCollectOverlay;
	    
	    
		public PlottingFrame(){
			super("Data plotting");
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
			infoPanel = new JPanel(new FlowLayout());
			statsTablePanel = new JPanel();
			chartPanel = new JPanel(new GridLayout(2,1));
			label = new JLabel("<html><center>"+internalText+"</html>");
			optionsPanel = new JPanel(new FlowLayout());
			plotLogX = new JCheckBox("Log-plot X-axis");
			plotLogY = new JCheckBox("Log-plot Y-axis");
			collectOverlay = new JCheckBox("Overlay existing plots");
			optionsPanel.add(plotLogX);
			optionsPanel.add(plotLogY);
			//optionsPanel.add(collectOverlay);
			scatterChart = this.getScatterChart();
			histogramChart = this.getHistogramChart();
			scatterChartPanel = new XChartPanel<XYChart>(scatterChart);
			scatterChartPanel.setSize(650,250);
			scatterChartPanel.setPreferredSize(new Dimension(650, 250));
			histogramChartPanel = new XChartPanel<CategoryChart>(histogramChart);
			histogramChartPanel.setSize(650,250);
			histogramChartPanel.setPreferredSize(new Dimension(650, 250));
			chartPanel.add(histogramChartPanel);
			chartPanel.add(scatterChartPanel);
			chartPanel.setPreferredSize(new Dimension(650,500));
			//infoPanel.add(label);
			infoPanel.add(optionsPanel);
			mainPanel.add(statsTablePanel);
			mainPanel.add(infoPanel);
			mainPanel.add(chartPanel);
			wholeViewScrollPane = new JScrollPane(mainPanel);
			wholeViewScrollPane.setPreferredSize(new Dimension(650,800));
			add(wholeViewScrollPane);
			setSize(650,900);
			setLocationRelativeTo(null);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// quickly create/add action listeners for each checkbox
			class PlotLogXActionListener implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					doPlotLogX = plotLogX.isSelected();	
					redrawScatterChartWithExistingData(scatterChart);
				}}
			plotLogX.addActionListener(new PlotLogXActionListener());
			
			class PlotLogYActionListener implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					doPlotLogY = plotLogY.isSelected();					
					redrawScatterChartWithExistingData(scatterChart);
				}}
			plotLogY.addActionListener(new PlotLogYActionListener());
			
			class CollectOverlayActionListener implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					doCollectOverlay = collectOverlay.isSelected();					
				}}
			collectOverlay.addActionListener(new CollectOverlayActionListener());
		}
		
		public void addTable(SummaryStatisticsTableModel statisticsModel) {
	        // Init resultsTable
	        statsTable = new JTable(statisticsModel);
	        statsTable.setPreferredSize(new Dimension(650,200));
	        statsTable.setFillsViewportHeight(true);
	        statsTable.setRowSelectionAllowed(true);
	        statsTable.setColumnSelectionAllowed(true);
	        statsTable.setCellSelectionEnabled(true);
	        statsTable.setAutoCreateRowSorter(true);
	        statsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	        /* Create scrollpanes  and add them to the pane */
	        statsTableScrollPane = new JScrollPane(statsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	        statsTableScrollPane.setPreferredSize(new Dimension(650,300));
	        
	        statsTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	        statsTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        statsTablePanel.removeAll();
	        statsTablePanel.add(statsTableScrollPane);
		}

		/**
		 * Update the label in the plotting window (mainly debug function)
		 * @param newContent
		 */
		public void updateLabelContent(String newContent){
			internalText = newContent;
			label.setText("<html><center>"+internalText+"</html>");
		}

		/**
		 * Update a chart with univariate data as histogram
		 * @param name
		 * @param xData
		 */
		public void updateHistogramChart(String name, Double[] xData){
			histogramChart.removeSeries(currentHistogramSeriesName);
			currentHistogramSeriesName = name;
			int maxXdataOOM = (int)Math.log10(org.apache.commons.lang3.math.NumberUtils.max(ArrayUtils.toPrimitive(xData)));
			try {
				Histogram histogram = new Histogram(Arrays.asList(xData), 20);
				histogramChart.addSeries(name, histogram.getxAxisData(),histogram.getyAxisData());
				// try and get the x-axis labels' precision right...
				if(maxXdataOOM > 0){
					// bigger than 10^1 so safe to ignore d.p.
				    histogramChart.getStyler().setXAxisDecimalPattern("#");
				}else{
					// 10^0 or smaller, fiddle precision on x-axis labels
					switch(maxXdataOOM){
					case(0):{
						histogramChart.getStyler().setXAxisDecimalPattern("#.#");
					}
					case(-1):{
						histogramChart.getStyler().setXAxisDecimalPattern("#.#");
					}
					case(-2):{
						histogramChart.getStyler().setXAxisDecimalPattern("#.#");
					}
					case(-3):{
						histogramChart.getStyler().setXAxisDecimalPattern("#.##");
					}
					case(-4):{
						histogramChart.getStyler().setXAxisDecimalPattern("#.##");
					}
					default:{
						histogramChart.getStyler().setXAxisDecimalPattern("#.###");
					}
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			histogramChartPanel.repaint();
		}

		public void redrawScatterChartWithExistingData(XYChart existing){
			XYSeries some = existing.getSeriesMap().get(currentScatterSeriesName);
			// Use an iterator to find the min of X data
			Iterator<Double> xItr = (Iterator<Double>) some.getXData().iterator();
			Double xMin = xItr.next();
			while(xItr.hasNext()){
				xMin = Math.min(xMin, xItr.next());
			}
			// Use an iterator to find the min of Y data
			Iterator<Double> yItr = (Iterator<Double>) some.getYData().iterator();
			Double yMin = yItr.next();
			while(xItr.hasNext()){
				yMin = Math.min(yMin, yItr.next());
			}
			try {
				if(doPlotLogX && (xMin > 0)){
					scatterChart.getStyler().setXAxisLogarithmic(true);
				}else{
					scatterChart.getStyler().setXAxisLogarithmic(false);
				}
				if(doPlotLogY && (yMin > 0)){
					scatterChart.getStyler().setYAxisLogarithmic(true);
				}else{
					scatterChart.getStyler().setYAxisLogarithmic(false);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				scatterChartPanel.repaint();
			}catch(Exception ex){
				scatterChart.getStyler().setXAxisLogarithmic(false);
				scatterChart.getStyler().setYAxisLogarithmic(false);
				scatterChartPanel.repaint();
				ex.printStackTrace();
			}
			
		}
		
		/**
		 * Update a chart with bivariate data
		 * @param name
		 * @param xData
		 * @param yData
		 */
		public void updateScatterChart(String name, Double[] xData, Double[] yData){
			scatterChart.removeSeries(currentScatterSeriesName);
			currentScatterSeriesName = name;
			try {
				scatterChart.addSeries(name, Arrays.asList(xData), Arrays.asList(yData));
				if(doPlotLogX && (org.apache.commons.lang3.math.NumberUtils.min(ArrayUtils.toPrimitive(xData)) > 0)){
					scatterChart.getStyler().setXAxisLogarithmic(true);
				}else{
					scatterChart.getStyler().setXAxisLogarithmic(false);
				}
				if(doPlotLogY && (org.apache.commons.lang3.math.NumberUtils.min(ArrayUtils.toPrimitive(yData)) > 0)){
					scatterChart.getStyler().setYAxisLogarithmic(true);
				}else{
					scatterChart.getStyler().setYAxisLogarithmic(false);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			scatterChartPanel.repaint();
		}
		
		public CategoryChart getHistogramChart(){
			/**
			 * Internal class to generate data according to a normalish distribution
			 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
			 * @since 4 Jul 2017
			 * @version 0.1
			 */
			final class DataGenerator{
			  private List<Double> getGaussianData(int count) {

				    List<Double> data = new ArrayList<Double>(count);
				    Random r = new Random();
				    for (int i = 0; i < count; i++) {
				      data.add(r.nextGaussian() * 10);
				    }
				    return data;
				  }
			}
			
			// Create Chart
		    CategoryChart histogramPlaceHolder = new CategoryChartBuilder().title("Histogram").theme(ChartTheme.GGPlot2).height(300).width(650).build();
		    // Customize Chart
		    histogramPlaceHolder.getStyler().setLegendPosition(LegendPosition.InsideNW);
		    histogramPlaceHolder.getStyler().setAvailableSpaceFill(.96);
		    histogramPlaceHolder.getStyler().setOverlapped(true);
		    histogramPlaceHolder.getStyler().setXAxisDecimalPattern("#");
		    Histogram histogram = new Histogram(new DataGenerator().getGaussianData(1000), 20, -20, 20);
		    String dummyName = "Dummy data - select a single Alignments numeric column to plot.";
		    currentHistogramSeriesName = dummyName;
		    histogramPlaceHolder.addSeries(dummyName, histogram.getxAxisData(), histogram.getyAxisData());
		    return histogramPlaceHolder;			
		}
		
		/**
		 * Get an XChart scatter plot (XYChart)
		 * @return
		 */
		public XYChart getScatterChart(){

			// Create Chart
			currentScatterSeriesName = "Dummy data - select two Alignments numeric columns to plot.";
			scatterChart = new XYChartBuilder().title("Scatterplot").theme(ChartTheme.GGPlot2).height(300).width(650).build();

			// Customize Chart
			scatterChart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
			scatterChart.getStyler().setLegendPosition(LegendPosition.InsideN);

			// Series
			List<Double> xData = new ArrayList<Double>();
			List<Double> yData = new ArrayList<Double>();
			Random random = new Random();
			int size = 10;
			for (int i = 0; i < size; i++) {
				double nextRandom = random.nextDouble();
				xData.add(Math.pow(10, nextRandom * 10));
				yData.add(1000000000.0 + nextRandom);
			}
			scatterChart.addSeries(currentScatterSeriesName, xData, yData);

			return scatterChart;
		}

		/**
		 * Return the stats JTable
		 * @return
		 */
		public JTable getStatsTable() {
			return this.statsTable;
		}
	}

	/**
	 * Class extending a JFrame to hold the statistics definitions
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since Aug 20, 2015
	 * @version 0.1
	 */
	private class DefinitionsFrame extends JFrame{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2377591672408559870L;
		JLabel definition;
		private DefinitionsFrame(){
			super("Statistics' definitions");
			setSize(600, 650);
			setLocationRelativeTo(null);
			definition = new JLabel("");
			add(definition);
			setVisible(false);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		}
		
		private void setLabelText(String[] text){
			String label = "";
			for(String s:text){
				label+=s;
			}
			this.definition.setText(label);
		}
	}
	
	public void addTable(AlignmentsModel alignmentsModel) {
        // Init resultsTable
        alignmentsTable = new JTable(alignmentsModel);
        alignmentsTable.setPreferredScrollableViewportSize(new Dimension(700, 250));
        alignmentsTable.setFillsViewportHeight(true);
        alignmentsTable.setRowSelectionAllowed(true);
        alignmentsTable.setColumnSelectionAllowed(true);
        alignmentsTable.setCellSelectionEnabled(true);
        alignmentsTable.setAutoCreateRowSorter(true);
        alignmentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        /* Create scrollpanes  and add them to the pane */
        alignmentsScrollPane = new JScrollPane(alignmentsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sequencePaneNT = new JScrollPane();
		sequencePaneNT.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sequencePaneNT.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sequencePaneAA = new JScrollPane();
		sequencePaneAA.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sequencePaneAA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// Issue #54 - track the two horizontal scrolls
		scrollBarNT = sequencePaneNT.getHorizontalScrollBar();
		scrollBarAA = sequencePaneAA.getHorizontalScrollBar();
        subPanel.add(alignmentsScrollPane);
        panel.add(sequencePaneNT);
        panel.add(sequencePaneAA);
	}
	

	public JTable getTable(){
		return alignmentsTable;
	}
	
	public JPanel getPanel(){
		return panel;
	}

	public JScrollBar getScrollBarNT() {
		return scrollBarNT;
	}


	public JScrollBar getScrollBarAA() {
		return scrollBarAA;
	}


	public void addAddAlignmentsButtonListener(ActionListener al){
		addAlignmentsButton.addActionListener(al);
	}
	
	public void addRemoveAlignmentsButtonListener(ActionListener al){
		removeAlignmentsButton.addActionListener(al);
	}

	public void addTextDumpButtonListener(ActionListener al){
		textDumpButton.addActionListener(al);
	}

	public void addTableDefinitionButtonListener(ActionListener al){
		tableDefinitionButton.addActionListener(al);
	}
	
	public void addShowPlottingFrameButtonListener(ActionListener al){
		showPlottingFrameButton.addActionListener(al);
	}

	public void addListRowSelectionListener(AlignmentsRowListener arl){
        alignmentsTable.getSelectionModel().addListSelectionListener(arl);
	}

	public void addListColumnSelectionListener(AlignmentsColumnListener acl){
        alignmentsTable.getColumnModel().getSelectionModel().addListSelectionListener(acl);
	}

	public JFileChooser getFileChooser() {
		return fc;
	}

	public JFileChooser getDirectoryChooser() {
		return dc;
	}
	
	public void addScrollBarNTAdjustmentListener(AdjustmentListener al){
		scrollBarNT.addAdjustmentListener(al);
	}

	public void addScrollBarAAAdjustmentListener(AdjustmentListener al){
		scrollBarAA.addAdjustmentListener(al);
	}

	/**
	 * Updates the sequencePaneNT and sequencePaneAA with currently selected alignments
	 * @param da DisplayAlignment contraining the alignment data to display
	 */
	public void updateAlignmentScrollPanes(DisplayAlignment da) {
		// Get scroller JScrollPanes from DisplayAlignment
		//JScrollPane newPaneNT = da.getAlignmentScroller();	// do not call the old Stylesheet-based method
		// instead call getAlignmentCanvas() which uses java.awt.Graphics2D to render text.
		JScrollPane newPaneNT = da.getAlignmentCanvas(false);	//arg (useAminoAcidColours) is false e.g. nucleotide colouring
		// try and pass the scrollbar adjustment listener binding from one bar to the next
		JScrollBar newBarNT = newPaneNT.getHorizontalScrollBar();
		newBarNT.addAdjustmentListener(scrollBarNT.getAdjustmentListeners()[0]);
		scrollBarNT = newBarNT;
		//JScrollPane newPaneAA = da.getAlignmentScrollerAA();	// do not call the old Stylesheet-based method
		// instead call getAlignmentCanvas() which uses java.awt.Graphics2D to render text.
		JScrollPane newPaneAA = da.getAlignmentCanvas(true);	//arg (useAminoAcidColours) is true e.g. amino-acid colouring
		JScrollBar newBarAA = newPaneAA.getHorizontalScrollBar();
		newBarAA.addAdjustmentListener(scrollBarAA.getAdjustmentListeners()[0]);
		scrollBarAA = newBarAA;
		
		// Swap the sequencePaneNT
		panel.remove(sequencePaneNT);
		sequencePaneNT = newPaneNT;
//		sequencePaneNT.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		sequencePaneNT.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(sequencePaneNT);
		// Swap the sequencePaneAA		
		panel.remove(sequencePaneAA);
		sequencePaneAA = newPaneAA;
//		sequencePaneAA.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		sequencePaneAA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(sequencePaneAA);
		
		panel.revalidate();
		panel.repaint();

		// repaint / update components
/*
		paintComponents(getGraphics());
		panel.paintComponents(panel.getGraphics());
		sequencePaneNT.paintComponents(sequencePaneNT.getGraphics());
		repaint();
		subPanel.repaint();
		super.repaint();
*/
	}


	public void setStatisticDefinitions(String[] tableColumnDefinitions) {
		definitionFrame.setLabelText(tableColumnDefinitions);
		
	}


	public void setDefinitionFrameVisibility(boolean b) {
		definitionFrame.setVisible(b);
	}
}
