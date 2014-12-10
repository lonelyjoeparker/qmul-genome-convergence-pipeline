package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.GlobalModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.GlobalApplicationView;

public class GlobalController {

	GlobalModel model;
	GlobalApplicationView view;
	AlignmentsController alignmentsController;
	PhylogeniesController phylogeniesController;
	ResultsController resultsController;
	MenuBarController menuBarController;
	
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
		view.sidePanelUpdateGlobalSettingsButton.addActionListener(new UpdateGlobalVariablesListener());
		view.sidePanelSaveGlobalSettingsButton.addActionListener(new SaveGlobalVariablesListener());
	}

	/**
	 * Add the Controller for the alignments panel / model.
	 * @param alignmentsController
	 */
	public void addAlignmentsController(AlignmentsController addAlignmentsController) {
		this.alignmentsController = addAlignmentsController;
		view.addTab(this.alignmentsController.getView(), "Alignments");
	}

	/**
	 * Add the Controller for the phylogenies panel / model.
	 * @param addPhylogeniesController
	 */
	public void addPhylogeniesController(PhylogeniesController addPhylogeniesController) {
		this.phylogeniesController = addPhylogeniesController;
		view.addTab(this.phylogeniesController.getView(), "Phylogenies");
	}

	/**
	 * Add the Controller for the results panel / model.
	 * @param addResultsController
	 */
	public void addResultsController(ResultsController addResultsController) {
		this.resultsController = addResultsController;
		view.addTab(this.resultsController.getView(), "Results");
	}

	/**
	 * Add the Controller for the menu bar.
	 * @param menuController
	 */
	public void addMenuBarController(MenuBarController menuController) {
		this.menuBarController = menuController;
		view.setANewJMenuBar(menuBarController.getTheMenuView());
	}
	
	/**
	 * Add those listeners which are global e.g. span multiple models/views/controllers
	 */
	public void addGlobalActionListeners(){
		if((alignmentsController != null)&&(menuBarController != null)){
			menuBarController.addAddAlignmentsMenuListenerSingle(alignmentsController.addAlignmentsListenerSingle);
			menuBarController.addAddAlignmentsMenuListenerBatch(alignmentsController.addAlignmentsListenerBatch);
		}
		if((phylogeniesController != null)&&(menuBarController != null)){
			menuBarController.addAddPhylogeniesMenuListener(phylogeniesController.addPhylogenyListener);
		}
		if((resultsController != null)&&(menuBarController != null)){
			menuBarController.addAddResultsMenuListener(resultsController.addResultsListener);
		}
	}

	// Set the global model (analysis parameters) using the global view values
	public void saveGlobalVariableModelFromView(){
		// first the debug flag
		boolean setDebug = view.sidePanelDebugIndicator.isSelected();
		System.out.println("Set global debug to: "+setDebug);
		model.setDEBUG(setDebug);
		// not the TaxonList as we have no reliable way to parse the JTextArea at present.		
	}

	public void updateGlobalVariableView(){
		// See if we can update the taxonList
		model.setTaxonNamesSet(this.alignmentsController.updateTaxonSet(model.getTaxonNamesSet()));
		// Update the view panels with actual values
		view.sidePanelDebugIndicator.setSelected(model.isDEBUG());
		view.sidePanelTaxonListText.setText(model.getTaxonNamesSetAsMultilineString());
	}
	
	
	class SaveGlobalVariablesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveGlobalVariableModelFromView();
		}
	}

	class UpdateGlobalVariablesListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			updateGlobalVariableView();			
		}
	}
}
