package uk.ac.qmul.sbcs.evolution.convergence.gui;


import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


public class ChartTestbed {
	private XYSeriesCollection dataset;
	private XYPlot plot;

	public static void main(String[] args){
		new ChartTestbed().go();
	}

	public void go(){
		float[] dat_x = new float[100];
		float[] dat_y = new float[100];
		dataset = new XYSeriesCollection();
		XYSeries data = new XYSeries("some joe data");

		for(int i=0;i<dat_x.length;i++){
			dat_x[i] = (float)i;
			dat_y[i] = (float)i + (float)(Math.random()*10);
			data.add(dat_x[i],dat_y[i]);
		}

		dataset.addSeries(data);
		showGraph();
	}

	private void showGraph() {
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	//	final ApplicationFrame frame = new ApplicationFrame("Joe Chart Title");
	/*
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	 */
	}

	public JFreeChart createChart() {
		final JFreeChart chart = ChartFactory.createScatterPlot(
				"Entropies",                  // chart title
				"Position",                      // x axis label
				"Entropy",                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL,
				false,                     // include legend
				true,                     // tooltips
				false                     // urls
		);
		plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		plot.setRenderer(renderer);
		return chart;
	}
	
	public JFreeChart createHistogram(){
		double[] hdata = new double[100];
		for(int i=0;i<100;i++){
			hdata[i] = Math.random();
		}
		HistogramDataset histDataset = new HistogramDataset();
		histDataset.addSeries("entropies comparison",hdata,30,0,1);
		final JFreeChart hist = ChartFactory.createHistogram("Hist", null, null, histDataset, PlotOrientation.VERTICAL, false, false, false);
		plot = (XYPlot) hist.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		plot.setRenderer(renderer);
		return hist;
	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createScatterPlot(
				"Title",                  // chart title
				"X",                      // x axis label
				"Y",                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL,
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
		);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		plot.setRenderer(renderer);
		return chart;
	}
	
	public void setNewDataset(XYSeriesCollection newDataset){
		this.dataset = newDataset;
		plot.setDataset(dataset);
	}
	
	public void addXYseries(XYSeries newXYseries){
		dataset.addSeries(newXYseries);
	}
	
	public void replaceHistSeries(double[] hdata, double minVal, double maxVal){
		HistogramDataset histDataset = new HistogramDataset();
		histDataset.addSeries("entropies-comparison"+Math.random(),hdata,30,minVal,maxVal);
		plot.setDataset(histDataset);
		
	}
	
	public void addSeries(int scale){
		float[] dat_x = new float[100];
		float[] dat_y = new float[100];
		XYSeries data = new XYSeries("data"+Math.random());

		for(int i=0;i<dat_x.length;i++){
			dat_x[i] = (float)i;
			dat_y[i] = (float) ((((float)i)*(Math.random()*scale)) + (float)(Math.random()*scale));
			data.add(dat_x[i],dat_y[i]);
		}

		dataset.addSeries(data);
		
	}

	public void addYseries(float[] dummyEntropies) {
		float[] dat_x = new float[100];
		float[] dat_y = dummyEntropies;
		XYSeries data = new XYSeries("data"+Math.random());

		for(int i=0;i<dat_x.length;i++){
			dat_x[i] = (float)i;
			data.add(dat_x[i],dat_y[i]);
		}

		dataset.addSeries(data);
	}
}
