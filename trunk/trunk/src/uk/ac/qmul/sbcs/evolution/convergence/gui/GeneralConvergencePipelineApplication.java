package uk.ac.qmul.sbcs.evolution.convergence.gui;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.*;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.*;
import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.*;

public class GeneralConvergencePipelineApplication {

	private GlobalModel globalModel;
	private GlobalApplicationView globalView;
	private GlobalController globalController;
	private ResultsTableModel resultsModel;
	private ResultsView resultsView;
	private ResultsController resultsController;
	
	/**
	 * No-arg constructor.
	 */
	public GeneralConvergencePipelineApplication(){
		// Instantiate the specific model / view / controllers first, so that the controllers can be passed to globalcontroller
		resultsModel = new ResultsTableModel();
		resultsView = new ResultsView();
		resultsController = new ResultsController(resultsModel, resultsView);
		
		// Instantiate the global view, model and controller
		globalModel = new GlobalModel();
		globalView = new GlobalApplicationView();
		globalController = new GlobalController(globalModel, globalView);
		
		// Add the results model-view-controller to the global controller
		globalController.addResultsController(resultsController);
	}
	
	/**
	 * Application entry point
	 * @param args
	 */
	public static void main(String[] args) {
		new GeneralConvergencePipelineApplication();
	}
}
