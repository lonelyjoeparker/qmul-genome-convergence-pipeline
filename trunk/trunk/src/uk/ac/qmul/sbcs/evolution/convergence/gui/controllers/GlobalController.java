package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.GlobalModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.GlobalApplicationView;

public class GlobalController {

	GlobalModel model;
	GlobalApplicationView view;
	ResultsController resultsController;
	/**
	 * No-arg constructor. Deprecated
	 */
	@Deprecated
	public GlobalController(){}
	
	/**
	 * Preferred constructor.
	 * @param globalModel
	 * @param globalView
	 */
	public GlobalController(GlobalModel globalModel, GlobalApplicationView globalView) {
		model = globalModel;
		view = globalView;
	}

	public void addResultsController(ResultsController addResultsController) {
		this.resultsController = addResultsController;
		view.addTab(this.resultsController.getView(), "Results tab");
	}
}
