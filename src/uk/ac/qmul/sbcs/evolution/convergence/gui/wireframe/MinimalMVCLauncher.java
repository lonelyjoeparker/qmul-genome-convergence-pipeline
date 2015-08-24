package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

/**
 * The Launcher just serves as the application entry point.
 * NB the controller is called last, as the view and model must be passed to it...
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class MinimalMVCLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinimalMVCView view = new MinimalMVCView();
		MinimalMVCModel model = new MinimalMVCModel();
		MinimalMVCController controller = new MinimalMVCController(view, model);
		view.setVisible(true);
	}

}
