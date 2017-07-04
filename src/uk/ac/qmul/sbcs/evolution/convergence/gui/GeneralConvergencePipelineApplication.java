package uk.ac.qmul.sbcs.evolution.convergence.gui;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.*;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.*;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.*;

/**
 * Launcher / entry-point and application for a genome convergence detection pipeline and graphical user interface (GUI)
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class GeneralConvergencePipelineApplication implements Runnable{

	// Global model-view-controller
	private GlobalModel globalModel;
	private GlobalApplicationView globalView;
	private GlobalController globalController;

	// Alignments M-V-C
	private AlignmentsModel alignmentsModel;
	private AlignmentsView alignmentsView;
	private AlignmentsController alignmentsController;

	// Phylogenies M-V-C
	private PhylogeniesModel phylogeniesModel;
	private PhylogeniesView phylogeniesView;
	private PhylogeniesController phylogeniesController;
	
	// Analyses (XMLs) M-V-C
	private AnalysesModel analysesModel;
	private AnalysesView analysesView;
	private AnalysesController analysesController;
	
	// Results M-V-C
	private ResultsTableModel resultsModel;
	private ResultsView resultsView;
	private ResultsController resultsController;

	// Menu bar
	private MenuBarFactory menuBar;
	private MenuBarController menuController;
	
	/**
	 * No-arg constructor.
	 */
	public GeneralConvergencePipelineApplication(){

		/* Instantiate the model-view-controllers */

		//Instantiate the specific model / view / controllers first, so that the controllers can be passed to globalcontroller.
		// First alignments MVC
		alignmentsModel = new AlignmentsModel();
		alignmentsView = new AlignmentsView();
		alignmentsController = new AlignmentsController(alignmentsModel, alignmentsView);

		// Phylogenies MVC
		phylogeniesModel = new PhylogeniesModel();
		phylogeniesView = new PhylogeniesView();
		phylogeniesController = new PhylogeniesController(phylogeniesModel, phylogeniesView);
		
		// Analyses (XMLs) M-V-C
		analysesModel = new AnalysesModel();
		analysesView = new AnalysesView();
		analysesController = new AnalysesController(analysesModel, analysesView);
		
		// Results MVC
		resultsModel = new ResultsTableModel();
		resultsView = new ResultsView();
		resultsController = new ResultsController(resultsModel, resultsView);
		
		// Instantiate the global view, model and controller
		globalModel = new GlobalModel();
		globalView = new GlobalApplicationView();
		globalController = new GlobalController(globalModel, globalView);
		
		// Instantiate and add the menu bar and controller
		menuBar = new MenuBarFactory();
		menuController = new MenuBarController(globalModel, menuBar);
		
		
		/* Add model-view-controllers to global controller */
		
		// Add the alignments model-view-controller to the global controller
		globalController.addAlignmentsController(alignmentsController);

		// Add the phylogenies model-view-controller to the global controller
		globalController.addPhylogeniesController(phylogeniesController);
		
		// Add the analyese model-view-controller to the global controller
		globalController.addAnalysesController(analysesController);
		
		// Add the results model-view-controller to the global controller
		globalController.addResultsController(resultsController);
		
		// Add the menu bar view and controller (it only accesses global model/vars) to the global controller
		globalController.addMenuBarController(menuController);
		
		
		/* Finally, register the global listeners. */
		// These are those listeners which span multiple models/views/controllers so depend on them all being instantiated first.
		globalController.addGlobalActionListeners();
	}
	
	/**
	 * Application entry point
	 * @param args
	 */
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new GeneralConvergencePipelineApplication());

	}

	@Override
	public void run() {
//		new GeneralConvergencePipelineApplication();
	}
}
