package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AlignmentsTableModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.AlignmentsView;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

public class AlignmentsController {

	final AlignmentsTableModel model;
	final AlignmentsView view;
	AddSingleAlignmentsButtonListener 	addAlignmentsListenerSingle;
	AddBatchAlignmentsButtonListener 	addAlignmentsListenerBatch;

	/**
	 * No-arg constructor - deprecated. 
	 */
	@Deprecated
	public AlignmentsController(){
		// No-arg constructor, deprecated
		model = null;
		view = null;
	}

	/**
	 * Constructor for this class, do not use the (deprecated) no-arg constructor.
	 * @param alignmentsModel
	 * @param alignmentsView
	 */
	public AlignmentsController(AlignmentsTableModel alignmentsModel, AlignmentsView alignmentsView) {
		model = alignmentsModel;
		view = alignmentsView;
		addAlignmentsListenerSingle = new AddSingleAlignmentsButtonListener();
		addAlignmentsListenerBatch = new AddBatchAlignmentsButtonListener();
		view.addAlignmentsButtonListener(addAlignmentsListenerSingle);
		view.addTable(model);
		initColumnSizes();
		view.addListRowSelectionListener(new AlignmentsRowListener());
		view.addListColumnSelectionListener(new AlignmentsColumnListener());
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

	/**
	 * Open a file chooser, attempt to select an alignment file, read it in as a DisplayAlignment, and try to add it to the model/table
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class AddSingleAlignmentsButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			view.getFileChooser().showOpenDialog(view);
			File alignmentFile = view.getFileChooser().getSelectedFile();
			AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
			try {
				asr.loadSequences(alignmentFile, false);
				asr.calculateAlignmentStats(false);
			} catch (TaxaLimitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
			model.addRow(da);
			view.repaint();
		}
	}

	/**
	 * Attempt to add a directory of files in batch mode. 
	 * <br/>Currently *very* inefficient as multiple calls to addRow()
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class AddBatchAlignmentsButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = view.getDirectoryChooser().showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File alignmentDirectory = view.getDirectoryChooser().getSelectedFile();
				if(alignmentDirectory.isDirectory()){
					File[] files  = alignmentDirectory.listFiles();
					for(File alignmentFile:files){
						try {
							AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
							asr.loadSequences(alignmentFile, false);
							asr.calculateAlignmentStats(false);
							DisplayAlignment da = new DisplayAlignment(alignmentFile.getName(),asr);
							model.addRow(da);
							view.getTable().repaint();
						} catch (TaxaLimitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NullPointerException ex) {
							ex.printStackTrace();
						}
					}
				}			
			}
		}
	}

	
	public class AlignmentsColumnListener implements ListSelectionListener {
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

	public class AlignmentsRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			/* Get the table and print out some debug info... */
			JTable alignmentsTable = view.getTable();
			/* Get the selected table row, via view */
			int viewModelRow = alignmentsTable.getSelectedRow();
			Object[] a_row = model.getData()[viewModelRow];
			String val = a_row[4].toString();
			String entropy = a_row[8].toString();
			System.out.println("VIEW ROW ("+viewModelRow+") selected n sites nt: "+val+", entropy "+entropy);
			/* Get the selected row, via model */
			int tableModelRow = alignmentsTable.convertRowIndexToModel(viewModelRow);
			a_row = model.getData()[tableModelRow];
			val = a_row[4].toString();
			entropy = a_row[8].toString();
			System.out.println("MODEL ROW ("+tableModelRow+") selected n sites nt: "+val+", entropy "+entropy);

			/* Attempt to update the view alignment representation JScrollPanes */
			DisplayAlignment da = (DisplayAlignment) a_row[0];
			view.updateAlignmentScrollPanes(da);
			view.repaint();
		}
	}

	public HashSet<String> updateTaxonSet(HashSet<String> taxonNamesSet) {
		Object[][] data = model.getData();
		for(Object[] alignment:data){
			DisplayAlignment a = (DisplayAlignment)alignment[0];
			try {
				taxonNamesSet = a.expandTaxonNameSet(taxonNamesSet);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return taxonNamesSet;
	}

}

