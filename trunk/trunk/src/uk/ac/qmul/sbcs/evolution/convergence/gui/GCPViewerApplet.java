package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.applet.Applet;
import java.awt.HeadlessException;

public class GCPViewerApplet extends Applet {

	TableRenderDemoGCP gui;
	
	public GCPViewerApplet() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public void init(){
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        System.err.println("running");
        gui = new TableRenderDemoGCP();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.createAndShowGUI();
            }
        });
	}
	
	public void stop(){}
}
