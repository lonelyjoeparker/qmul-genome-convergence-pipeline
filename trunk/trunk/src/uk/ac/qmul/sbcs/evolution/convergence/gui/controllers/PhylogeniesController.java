package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.PhylogeniesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.PhylogeniesView;

public class PhylogeniesController {
	PhylogeniesModel model;
	PhylogeniesView view;
	AddPhylogeniesListener addPhylogenyListener = new AddPhylogeniesListener();
	
	/**
	 * No-arg constructor is deprecated
	 */
	@Deprecated
	public PhylogeniesController(){}
	
	public PhylogeniesController(PhylogeniesModel initModel, PhylogeniesView initView){
		model = initModel;
		view = initView;
		view.addTable(model);
		
	}

	public PhylogeniesModel getModel() {
		return model;
	}

	public JComponent getView() {
		return view.getPanel();
	}
	
	public class AddPhylogeniesListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			view.getFileChooser().showOpenDialog(view);
			File phylogenyFile = view.getFileChooser().getSelectedFile();
			// do nothing for now
			model.addPhylogenyRowAsStringTree(phylogenyFile);
			view.repaint();
		}
	}
	
	
}
