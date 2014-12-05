package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

public class WireframeMultiTableControllerLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {public void run() {new WireframeMultiTableController().createAndShowGUI();}});
	}

}
