package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AnalysesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.AnalysesView;

public class AnalysesController {

	AnalysesModel model;
	AnalysesView view;
	
	/**
	 * Default no-arg constructor - deprecated.
	 */
	@Deprecated
	public AnalysesController(){}
	
	/**
	 * Preferred constructor, adds the model and view.
	 * @param aModel
	 * @param aView
	 */
	public AnalysesController(AnalysesModel aModel, AnalysesView aView){
		model = aModel;
		view = aView;
	}
}
