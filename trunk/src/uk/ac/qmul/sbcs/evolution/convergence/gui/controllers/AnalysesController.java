package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;

import uk.ac.qmul.sbcs.evolution.convergence.gui.controllers.GlobalController.UpdateGlobalVariablesFromAnalysisXMLsListener;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AnalysesModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.AnalysesView;

public class AnalysesController {

	AnalysesModel model;
	AnalysesView view;
	AddAnalysesListener addAnalysesListener = new AddAnalysesListener();
	DeleteAnalysesListener deleteAnalysesListener = new DeleteAnalysesListener();
	
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
		view.addAddAnalysesListener(addAnalysesListener);
		view.addDeleteAnalysesListener(deleteAnalysesListener);
	}

	public AnalysesModel getModel() {
		return model;
	}

	public JComponent getView() {
		return view.getPanel();
	}
	
	/**
	 * An actionlistener to add analysis rows to the view/model
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class DeleteAnalysesListener implements ActionListener{
		
		/**
		 * TODO - delete rows from table
		 */
		@Override
		public void actionPerformed(ActionEvent ev){
			/* Delete the selected row from the table.. */
			int selectedTableRowForDeletion = view.getTable().getSelectedRow();
			System.out.println("Delete selected row "+ selectedTableRowForDeletion);
			switch(view.getTable().getSelectedRowCount()){
			case 0:{
				System.out.println("No selected analysis row to remove.");
				break;
			}
			case 1:{
				// if exactly one row is selected
				selectedTableRowForDeletion = view.getTable().getSelectedRow();
				System.out.println("remove selected alignment, row "+selectedTableRowForDeletion);
				model.removeRow(selectedTableRowForDeletion);
				view.getTable().repaint();
				break;
			}
			default:{
				System.out.println("More than one row selected ("+view.getTable().getSelectedRowCount()+"); select exactly one row to remove.");
				break;
			}
		}
		}
	}
	
	/**
	 * An actionlistener to add analysis rows to the view/model. An additional {@link UpdateGlobalVariablesFromAnalysisXMLsListener}
	 *  can be chained to this (to attempt to run their actions in sequence in response to an actionevent) with the 
	 *  registerSecondActionListener() method.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class AddAnalysesListener implements ActionListener{
		public UpdateGlobalVariablesFromAnalysisXMLsListener secondAction;
		
		@Override
		public void actionPerformed(ActionEvent ev){
			view.getFileChooser().showOpenDialog(view);
			File someAnalysisFile = view.getFileChooser().getSelectedFile();
			model.addAnalysisFile(someAnalysisFile);
			// view.repaint() should not be needed?
			secondAction.actionPerformed(ev);
		}
		
		/**
		 * Attempt to chain an additional {@link UpdateGlobalVariablesFromAnalysisXMLsListener} to this listener, 
		 * to run their actions in sequence in response to an actionevent.
		 * @param al - a UpdateGlobalVariablesFromAnalysisXMLsListener
		 */
		public void registerSecondActionListener(UpdateGlobalVariablesFromAnalysisXMLsListener al){
			secondAction = al;
		}
	}

}
