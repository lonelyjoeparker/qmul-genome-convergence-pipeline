package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayPhylogeny;
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
		view.addRowSelectionListener(new PhylogeniesRowListener());
		
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
			Object[][] modelData = model.getData();
			view.updatePhylogenyDisplay((DisplayPhylogeny) modelData[modelData.length-1][0]);
//			view.repaint();
		}
	}
	
	/**
	 * Listener for phylogenies table row selections
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class PhylogeniesRowListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			/* Get the table and print out some debug info... */
			JTable phylogenyTable = view.getTable();
			/* Get the selected table row, via view */
			int viewModelRow = phylogenyTable.getSelectedRow();
			Object[] a_row = model.getData()[viewModelRow];
			String nTaxa = a_row[1].toString();
			String treeString = a_row[2].toString();
			System.out.println("VIEW ROW ("+viewModelRow+") selected n taxa : "+nTaxa+", tree "+treeString);
			/* Get the selected row, via model */
			int tableModelRow = phylogenyTable.convertRowIndexToModel(viewModelRow);
			a_row = model.getData()[tableModelRow];
			nTaxa = a_row[1].toString();
			treeString = a_row[2].toString();
			System.out.println("MODEL ROW ("+tableModelRow+") selected n taxa: "+nTaxa+", tree "+treeString);

			/* Attempt to update the view representation */
			DisplayPhylogeny dp = (DisplayPhylogeny) a_row[0];
			view.updatePhylogenyDisplay(dp);
//			view.repaint();
		}
		
	}
}
