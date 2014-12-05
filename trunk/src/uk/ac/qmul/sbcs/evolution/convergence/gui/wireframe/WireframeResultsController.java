package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.ResultsTableModel;

public class WireframeResultsController {

	ResultsTableModel model;
	WireframeResultsPanel view;

	public WireframeResultsController(ResultsTableModel aModel, WireframeResultsPanel aView){
		model = aModel;
		view = aView;
		view.addResultsButtonListener(new AddResultsButtonListener());
		view.addTable(model);
		WireframeMultiTableController.initColumnSizesAnyModel(model, view.getTable());
		view.addListRowSelectionListener(new RowListener());
		view.addListColumnSelectionListener(new ColumnListener());
	/*
		view.setVisible(true);
		view.setOpaque(true);
	 *	not needed as I think Visible / Opaque should propagate through all the components from main JFrame..
	 */
	}

	class AddResultsButtonListener implements ActionListener{
		String chooserText = "choose a results file..";
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = view.getFileChooser().showOpenDialog(view);
			File resultFile = view.getFileChooser().getSelectedFile();
			// do nothing for now
			model.addRow(resultFile);
			view.repaint();
		}
	}
	
	class ColumnListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			// text refers to a text area in the global view
			// not sure what to do about that, unless we pass two views to the controller,
			// the specific one and a the global one... hmm...  or globalViewActionsController...?
			// view.text.setText("COLUMN SELECTION EVENT. ");
			System.out.println("COLUMN SELECTION EVENT. ");
		}
	}

	class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			/*
			 * My code - try and get the row data...
			 * 
			 * NB that at the moment there is only one RowListener (and ColumnListnener) shared by all TableDataModels
			 * NB that at the moment TableDataModel.data is not visible hence access through getData() and setData() calls.
			 * This is possibly less efficient but safer than allowing any class to modify Data (given it is an Object[][] of a variety of subclasses)
			 */
			JTable resultsTable = view.getTable();
			int viewModelRow = resultsTable.getSelectedRow();
			Object[] a_row = model.getData()[viewModelRow];
			String val = a_row[4].toString();
			String entropy = a_row[8].toString();
			System.out.println("VIEW ROW ("+viewModelRow+") selected n sites nt: "+val+", entropy "+entropy);
			int tableModelRow = resultsTable.convertRowIndexToModel(viewModelRow);
			a_row = model.getData()[tableModelRow];
			val = a_row[4].toString();
			entropy = a_row[8].toString();
			System.out.println("MODEL ROW ("+tableModelRow+") selected n sites nt: "+val+", entropy "+entropy);

			/*
			 * Try the histogram
			 * NOTE not doig this crap now... lose it soon..
			 * 
			double[] compEntropies = new double[alignmentsModel.getData().length];
			double minVal = 0; //maximum to set hist limit
			double maxVal = 0; //maximum to set hist limit
			for(int i=0;i<compEntropies.length;i++){
				compEntropies[i] = ((Integer) alignmentsModel.getData()[i][4]).doubleValue();
				minVal = Math.min(minVal, compEntropies[i]);
				maxVal = Math.max(maxVal, compEntropies[i]);
			}
			text.setText("RENDERING ALIGNMENT...");
			text.setText("ROW SELECTION EVENT.");
			outputSelection();
			 */
		}
	}

	
	public JComponent getView() {
		return view.getPanel();
	}
}

