package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AnalysesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.AnalysesView;

public class AnalysesController {

	AnalysesModel model;
	AnalysesView view;
	AddAnalysesListener addAnalysesListener = new AddAnalysesListener();
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
		view.addTable(model);
	}

	public AnalysesModel getModel() {
		return model;
	}

	public JComponent getView() {
		return view.getPanel();
	}
	
	public class AddAnalysesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent ev){
			view.getFileChooser().showOpenDialog(view);
			File someAnalysisFile = view.getFileChooser().getSelectedFile();
			model.addAnalysisFile(someAnalysisFile);
			// view.repaint() should not be needed?
		}
	}

}
