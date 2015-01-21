package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.AlignmentsController.EmptyAlignmentsListException;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.GlobalModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.GlobalApplicationView;

public class GlobalController {

	GlobalModel model;
	GlobalApplicationView view;
	AlignmentsController alignmentsController;
	PhylogeniesController phylogeniesController;
	AnalysesController analysesController;
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
		view.setBinariesLocation.addActionListener(new SetBinariesLocationListener());
		view.setWorkdirLocation.addActionListener(new SetWorkdirLocationListener());
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
	 * Add the Controller for the analyses panel / model.
	 * @param addPhylogeniesController
	 */
	public void addAnalysesController(AnalysesController addAnalysesController) {
		this.analysesController = addAnalysesController;
		view.addTab(this.analysesController.getView(), "Analyses");
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
			menuBarController.addShowParameterWindowListener(new ShowParametersWindowListener());
			alignmentsController.setGlobalController(this);
		}
		if((phylogeniesController != null)&&(menuBarController != null)){
			menuBarController.addAddPhylogeniesMenuListener(phylogeniesController.addPhylogenyListener);
		}
		if((analysesController != null)&&(menuBarController != null)){
			menuBarController.addAddAnalysesMenuListener(analysesController.addAnalysesListener);
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
		try {
			model.setTaxonNamesSet(this.alignmentsController.updateTaxonSet(model.getTaxonNamesSet()));
		} catch (EmptyAlignmentsListException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Update the view panels with actual values
		view.sidePanelDebugIndicator.setSelected(model.isDEBUG());
		view.sidePanelTaxonListText.setText(model.getTaxonNamesSetAsMultilineString());
	}
	
	public void updateTaskbar(String message, int percentComplete){
		view.taskLabel.setText(message);
		view.taskbar.setValue(percentComplete);
	}
	
	class ShowParametersWindowListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent arg0){
			if(view.parametersWindow.isVisible()){
				view.parametersWindow.setVisible(false);
				menuBarController.view.showParameters.setSelected(false);
			}else{
				//view.parametersWindow.setLocation(64, 128); // to re-set the location back to the start. we'll disable this so the parameters window reappears where it was last positioned by the user.
				view.parametersWindow.setVisible(true);
				menuBarController.view.showParameters.setSelected(true);
			}
		}
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
	
	/**
	 * Use a JFileChooser to select a directory for the required binaries.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class SetBinariesLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			int returnVal = view.setBinariesLocationChooser.showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File userBinariesLocation = view.setBinariesLocationChooser.getSelectedFile();
				if(userBinariesLocation.canRead()){
					view.setBinariesLocationLabel(userBinariesLocation.getAbsolutePath());
					model.setUserBinariesLocation(userBinariesLocation);
				}else{
					System.err.println("Unable to read "+userBinariesLocation.getAbsolutePath());
					view.setBinariesLocationLabel("WARNING! Unable to read "+userBinariesLocation.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Use a JFileChooser to select a directory for the working directory.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class SetWorkdirLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			int returnVal = view.setWorkdirLocationChooser.showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File userWorkdirLocation = view.setWorkdirLocationChooser.getSelectedFile();
				if(userWorkdirLocation.canRead()){
					view.setWorkdirLocationLabel(userWorkdirLocation.getAbsolutePath());
					model.setUserWorkdirLocation(userWorkdirLocation);
				}else{
					System.err.println("Unable to read "+userWorkdirLocation.getAbsolutePath());
					view.setWorkdirLocationLabel("WARNING! Unable to read "+userWorkdirLocation.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Validate the binaries directory: check that each of the
	 * required binaries can be found, executed, and gives 
	 * expected output.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class ValidateBinariesLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			/*
			 * Validate the binaries directory: check that each of the
			 * required binaries can be found, executed, and gives 
			 * expected output.
			 */
		}
	}

	/**
	 * Validate the working directory: check that files / 
	 * directories can be written and created.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	class ValidateWorkdirLocationListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			/*
			 * Validate the working directory: check that files / 
			 * directories can be written and created.
			 */
		}
	}
}
