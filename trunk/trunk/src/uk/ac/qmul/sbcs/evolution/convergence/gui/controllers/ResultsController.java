package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import uk.ac.qmul.sbcs.evolution.convergence.gui.models.ResultsTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.ResultsView;

public class ResultsController {

	ResultsTableModel model;
	ResultsView view;

	public ResultsController(ResultsTableModel aModel, ResultsView aView){
		model = aModel;
		view = aView;
		view.addResultsButtonListener(new AddResultsButtonListener());
		view.addTable(model);
		initColumnSizes();
		view.addListRowSelectionListener(new ResultsRowListener());
		view.addListColumnSelectionListener(new ResultsColumnListener());
	}

	class AddResultsButtonListener implements ActionListener{
		String chooserText = "choose a results file..";
		@Override
		public void actionPerformed(ActionEvent ev) {
			view.getFileChooser().showOpenDialog(view);
			File resultFile = view.getFileChooser().getSelectedFile();
			// do nothing for now
			model.addRow(resultFile);
			view.repaint();
		}
	}
	
	public class ResultsColumnListener implements ListSelectionListener {
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

	public class ResultsRowListener implements ListSelectionListener {
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
		}
	}

	
	public JComponent getView() {
		return view.getPanel();
	}


	/*
	 * Attempt to write a generic init method for column sizes
	 */
	public void initColumnSizes() {
		JTable table = view.getTable();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		int rowCount = model.getRowCount();
		int colCount = model.getColumnCount();
		//Object[] longValues = model.longValues; // longValues seems to be just the null vals for each column. we can get this from scratch...
		TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
	
		// try and establish the preferred widths. the first row of each dataModel contains null vals anyway (we know this cos it's in their constructors, we put it there).
		if(rowCount>0){
			for (int col = 0; col < colCount; col++) {
				column = table.getColumnModel().getColumn(col);
	
				comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
				headerWidth = comp.getPreferredSize().width;
	
				comp = table.getDefaultRenderer(model.getColumnClass(col)).getTableCellRendererComponent(table, model.getValueAt(0,col),false, false, 0, col);
				cellWidth = comp.getPreferredSize().width;
				column.setPreferredWidth(Math.max(headerWidth, cellWidth));
			}
		}
	}
}

