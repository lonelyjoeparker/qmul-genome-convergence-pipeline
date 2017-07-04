package uk.ac.qmul.sbcs.evolution.convergence.gui.controllers;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.gui.DisplayAlignment;
import uk.ac.qmul.sbcs.evolution.convergence.gui.models.AlignmentsModel;
import uk.ac.qmul.sbcs.evolution.convergence.gui.views.AlignmentsView;
import uk.ac.qmul.sbcs.evolution.convergence.util.BasicFileWriter;
import uk.ac.qmul.sbcs.evolution.convergence.util.TaxaLimitException;

public class AlignmentsController {

	final AlignmentsModel model;
	public final AlignmentsView view;
	AddSingleAlignmentsButtonListener 	addAlignmentsListenerSingle;
	AddBatchAlignmentsButtonListener 	addAlignmentsListenerBatch;
	RemoveSelectedAlignmentsButtonListener	removeSelectedAlignmentSingle;
	DumpTextFileButtonListener	dumpTextFileButtonListener;
	TableDefinitionButtonListener tableDefinitionButtonListener;
	ShowPlottingFrameButtonListener showPlottingFrameButtonListener;
	GlobalController globalController; 

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
	public AlignmentsController(AlignmentsModel alignmentsModel, AlignmentsView alignmentsView) {
		model = alignmentsModel;
		view = alignmentsView;
		addAlignmentsListenerSingle = new AddSingleAlignmentsButtonListener();
		addAlignmentsListenerBatch = new AddBatchAlignmentsButtonListener();
		removeSelectedAlignmentSingle = new RemoveSelectedAlignmentsButtonListener();
		dumpTextFileButtonListener = new DumpTextFileButtonListener();
		tableDefinitionButtonListener = new TableDefinitionButtonListener();
		showPlottingFrameButtonListener = new ShowPlottingFrameButtonListener();
		view.addAddAlignmentsButtonListener(addAlignmentsListenerSingle);
		view.addRemoveAlignmentsButtonListener(removeSelectedAlignmentSingle);
		view.addTextDumpButtonListener(dumpTextFileButtonListener);
		view.addTableDefinitionButtonListener(tableDefinitionButtonListener);
		view.addShowPlottingFrameButtonListener(showPlottingFrameButtonListener);
		view.addTable(model);
		initColumnSizes();
		view.addListRowSelectionListener(new AlignmentsRowListener());
		view.addListColumnSelectionListener(new AlignmentsColumnListener());
		view.addScrollBarNTAdjustmentListener(new ScrollBarNTAdjustmentListener());
		view.addScrollBarAAAdjustmentListener(new ScrollBarAAAdjustmentListener());
	}

	public void setGlobalController(GlobalController controller){
		globalController = controller;
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
			for(int col = 0; col < colCount; col++){
				column = table.getColumnModel().getColumn(col);

				comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
				headerWidth = comp.getPreferredSize().width;

				comp = table.getDefaultRenderer(model.getColumnClass(col)).getTableCellRendererComponent(table, model.getValueAt(0,col),false, false, 0, col);
				cellWidth = comp.getPreferredSize().width;
				column.setPreferredWidth(Math.max(headerWidth, cellWidth));
			}
		}else{
			// try and set column widths based on the table column names (surely that's the obvious way to do it anyway? - Ed)
			for(int col = 0; col < colCount; col++){
				column = table.getColumnModel().getColumn(col);

				comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
				headerWidth = comp.getPreferredSize().width;
				cellWidth = comp.getPreferredSize().width;
				column.setPreferredWidth(headerWidth);
			}
		}
	}

	/**
	 * Show the plotting frame
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since 4 Jul 2017
	 * @version 0.1
	 */
	public class ShowPlottingFrameButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			view.plottingFrame.setVisible(true);
		}
	}
	
	/**
	 * Open a file chooser, pick a directory and write 'alignment_descriptive_stats.tdf' there; a dump of all the Alignment objects' stats.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / RBG Kew</a>
	 *
	 */
	public class DumpTextFileButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			// Get string representing table data
			String output = model.toString();
			// System I/O - get a directory to write to.
			view.getDirectoryChooser().showSaveDialog(null);
			//view.getDirectoryChooser().showOpenDialog(view);
			//File outputDirectory = view.getDirectoryChooser().getSelectedFile();
			//File outputTextFile = new File(outputDirectory,"alignment_descriptive_stats.tdf");
			new BasicFileWriter(view.getDirectoryChooser().getSelectedFile(),output);
			System.out.println("Writing output to "+view.getDirectoryChooser().getSelectedFile().getPath()+"\n"+model.toString());
		}
	}

	/**
	 * Add the table definitions to the definitionFrame, and display it
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since Aug 20, 2015
	 * @version 0.1
	 */
	public class TableDefinitionButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			view.setDefinitionFrameVisibility(true);
			view.setStatisticDefinitions(model.getTableColumnDefinitionsHTML());
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
			model.addRow(da, asr);
			view.updateAlignmentScrollPanes(da);
//			view.repaint();
		}
	}
	
	/**
	 * If a single alignment is selected in the AlignmentsTableModel, 
	 * remove it and update the table.
	 * 
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 *	TODO Implement this. 
	 *	TODO Implement this behaviour for multiple, non-consecutive table rows.
	 */
	public class RemoveSelectedAlignmentsButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev){
			switch(view.getTable().getSelectedRowCount()){
				case 0:{
					System.out.println("No selected alignment row to remove.");
					break;
				}
				case 1:{
					// if exactly one row is selected
					int selectedRow = view.getTable().getSelectedRow();
					System.out.println("remove selected alignment, row "+selectedRow);
					model.removeRow(selectedRow);
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
	 * Attempt to add a directory of files in batch mode. 
	 * <br/>Currently *very* inefficient as multiple calls to addRow()
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class AddBatchAlignmentsButtonListener implements ActionListener, PropertyChangeListener{
		AlignmentsImportTask task;
		JLabel taskLabel;
		JProgressBar taskBar;
		public final String completeText = "Done ";
		public final int completeInt = 100;
		
		/**
		 * Set an optional tasklabel/bar
		 * @param label
		 * @param bar
		 */
		public void setTaskBarComponents(JLabel label, JProgressBar bar){
			taskLabel = label;
			taskBar = bar;
		}
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			int returnVal = view.getDirectoryChooser().showOpenDialog(view);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File alignmentDirectory = view.getDirectoryChooser().getSelectedFile();
				if(alignmentDirectory.isDirectory()){
			        if(globalController != null){
				        task = new AlignmentsImportTask(globalController.view.taskLabel, globalController.view.taskbar);
			        }else if((taskLabel != null)&&(taskBar != null)){
				        task = new AlignmentsImportTask(taskLabel, taskBar);
			        }else{
			        	task = new AlignmentsImportTask();
			        }
			        task.addPropertyChangeListener((PropertyChangeListener) this);
			        task.execute();
			        if(globalController != null){
			        	globalController.updateTaskbar(completeText, completeInt);
			        }else if((taskLabel != null)&&(taskBar != null)){
			        	taskLabel.setText(completeText);
			        	taskBar.setValue(completeInt);
			        }
				}			
			}
		}
		
	    /**
	     * Invoked when task's progress property changes.
	     */
	    public void propertyChange(PropertyChangeEvent evt) {
	        if ("progress" == evt.getPropertyName()) {
	        	int progress = task.getProgress();
	            String message = "Adding alignments ("+progress+";%)...";
		        if(globalController != null){
		        	globalController.updateTaskbar(message, progress);
		        }else if((taskLabel != null)&&(taskBar != null)){
		        	taskLabel.setText(message);
		        	taskBar.setValue(progress);
		        }
	        } 
	    }
	}

    class AlignmentsImportTask extends SwingWorker<Void, Void> {
		JLabel taskLabel;
		JProgressBar taskBar;
		public final String completeText = "Done ";
		public final int completeInt = 100;
		
		/**
		 * Set an optional tasklabel/bar
		 * @param label
		 * @param bar
		 */
		public AlignmentsImportTask(JLabel label, JProgressBar bar){
			taskLabel = label;
			taskBar = bar;
		}

		/**
		 * No-arg constructor - risky since taskLabel and taskBar will not be instantiated.
		 */
		public AlignmentsImportTask() {
			// TODO Auto-generated constructor stub
		}

		/*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
			File[] files  = view.getDirectoryChooser().getSelectedFile().listFiles();
			int totalFiles = files.length;
			int filesTried = 0;
			DisplayAlignment da = null;
			for(File alignmentFile:files){
				try {
					AlignedSequenceRepresentation asr = new AlignedSequenceRepresentation();
					asr.loadSequences(alignmentFile, false);
					asr.calculateAlignmentStats(false);
					da = new DisplayAlignment(alignmentFile.getName(),asr);
					model.addRow(da, asr);
				} catch (TaxaLimitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}
				filesTried++;
				progress = Math.round(((float)filesTried / (float)totalFiles)*100f);
				String message = "Adding alignments ("+filesTried+"; "+progress+"%)...";
		        if(globalController != null){
		        	globalController.updateTaskbar(message, progress);
		        }else if((taskLabel != null)&&(taskBar != null)){
		        	taskLabel.setText(message);
		        	taskBar.setValue(progress);
		        }
                setProgress(Math.min(progress, 100));
				System.out.println("Adding alignments ("+filesTried+"; "+progress+"%)...");
			}
			if(da != null){
				view.updateAlignmentScrollPanes(da);
			}
          return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
	        if(globalController != null){
	        	globalController.updateTaskbar(completeText, completeInt);
	        }else if((taskLabel != null)&&(taskBar != null)){
	        	taskLabel.setText(completeText);
	        	taskBar.setValue(completeInt);
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
			/* debug only, print selected row info
			System.out.println("COLUMN SELECTION EVENT. ");
			 */
			/* Get the table and print out some debug info... */
			JTable alignmentsTable = view.getTable();
			/* 
			 * Get the selected table column, via view. 
			 * Remember that getSelectedColumn returns -1 if no column selected 
			 * (delete events) / first column selected (multiple column selections)..
			 */
			String printMsg = null;
			int viewModelCol = alignmentsTable.getSelectedColumn();
			if(viewModelCol == -1){
				// No column selected, probably a delete event so re-select first column
				viewModelCol = 0;
			}else{
				if(alignmentsTable.getSelectedColumnCount() > 1){
					// multiple cols selected.
					int [] whichCols = alignmentsTable.getSelectedColumns();
					if((alignmentsTable.getSelectedColumnCount() == 2)&&(whichCols[0] > 2 && whichCols[0] < 16)&&(whichCols[1] > 2 && whichCols[1] < 16)){
						// exactly 2 selected, we can do a bivariate plot
						String dataXname = model.getColumnName(whichCols[0]);
						String dataYname = model.getColumnName(whichCols[1]);
						Double[] dataX = model.getColumnDataAsDouble(whichCols[0]);
						Double[] dataY = model.getColumnDataAsDouble(whichCols[1]);
						view.plottingFrame.updateScatterChart(dataXname+" vs "+dataYname, dataX, dataY);
					}else{
						// more then 2, throw a fit
						printMsg = "More than two (2) columns selected, or one or both contain non-numeric data. Select exactly 1 or 2 numeric data columns.";
					}
				}else{
					// probably a single column. which one?
					int whichCol = alignmentsTable.getSelectedColumn();
					// see if it is numerical data
					if(whichCol > 2 && whichCol < 16){
						// assign string name
						String dataName = model.getColumnName(whichCol);
						// get values
						Double[] values = model.getColumnDataAsDouble(whichCol);
						/*
						String stringValues = "";
						for(int i=0;i<values.length;i++){
							stringValues += "\n<br>"+values[i];
						}
						printMsg = dataName + stringValues;
						*/
						System.out.println("firing update histogram");
						view.plottingFrame.updateHistogramChart(dataName, values);
						
					}else{
						printMsg = "You must select a numeric data column.";
					}
				}
			}
			// print out what we've learnt is selected
			System.out.println(printMsg);
			// attempt to do a simple update on the plot
			view.plottingFrame.updateLabelContent(printMsg);
	}
	}

	/**
	 * Listener class for table row selection events.
	 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
	 *
	 */
	public class AlignmentsRowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			/* Get the table and print out some debug info... */
			JTable alignmentsTable = view.getTable();
			/* 
			 * Get the selected table row, via view. 
			 * Remember that getSelectedRow returns -1 if no row selected 
			 * (delete events) / first row selected (multiple row selections)..
			 */
			int viewModelRow = alignmentsTable.getSelectedRow();
			if(viewModelRow == -1){
				// No row selected, probably a delete event so re-select first row
				viewModelRow = 0;
			}else{
				if(alignmentsTable.getSelectedRowCount() > 1){
					// multiple rows selected.
				}
			}
			Object[] a_row = model.getData()[viewModelRow];
			String val = a_row[4].toString();
			String entropy = a_row[8].toString();
			/* debug only, print selected row info
			 * System.out.println("VIEW ROW ("+viewModelRow+") selected n sites nt: "+val+", entropy "+entropy);
			 */
			/* Get the selected row, via model */
			int tableModelRow = alignmentsTable.convertRowIndexToModel(viewModelRow);
			a_row = model.getData()[tableModelRow];
			val = a_row[4].toString();
			entropy = a_row[8].toString();
			/* debug only, print selected row info
			 * System.out.println("MODEL ROW ("+tableModelRow+") selected n sites nt: "+val+", entropy "+entropy);
			 */
			/* Attempt to update the view alignment representation JScrollPanes */
			DisplayAlignment da = (DisplayAlignment) a_row[0];
			view.updateAlignmentScrollPanes(da);
//			view.repaint();
		}
	}
	
	/**
	 * Custom adjustment linking the horizontal scrollbars in AA and NT panes
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since 4 Jul 2017
	 * @version 0.1
	 */
	public class ScrollBarNTAdjustmentListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent evt) {
			Adjustable source = evt.getAdjustable();
			if(evt.getValueIsAdjusting()){
				return;
			}
			int orient = source.getOrientation();
			if (orient == Adjustable.HORIZONTAL) {
				int valueNT = view.getScrollBarNT().getValue();
				// try to push the AA bar value to 1/3 the distance of the NT one
				view.getScrollBarAA().setValue((int)(valueNT/3.0));
			}
		}
	}

	/**
	 * Custom adjustment linking the horizontal scrollbars in AA and NT panes
	 * @author <a href="http://github.com/lonelyjoeparker">@lonelyjoeparker</a>
	 * @since 4 Jul 2017
	 * @version 0.1
	 */
	public class ScrollBarAAAdjustmentListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent evt) {
			Adjustable source = evt.getAdjustable();
			if(evt.getValueIsAdjusting()){
				return;
			}
			int orient = source.getOrientation();
			if (orient == Adjustable.HORIZONTAL) {
				int valueAA = view.getScrollBarAA().getValue();
				// try to push the NT bar value to 3 times the distance of the AA one
				view.getScrollBarNT().setValue(valueAA*3);
			}
		}
	}

	public HashSet<String> updateTaxonSet(HashSet<String> taxonNamesSet) throws EmptyAlignmentsListException {
		Object[][] data = model.getData();
		if(data != null){
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
		}else{
			throw new EmptyAlignmentsListException("Data array hashcode: "+data.hashCode());
		}
	}

	public class EmptyAlignmentsListException extends NullPointerException{
		// no-arg constructor
		public EmptyAlignmentsListException(){
			System.err.println("There is no active and valid alignment data.");
		}

		// string-arg constructor
		public EmptyAlignmentsListException(String message){
			System.err.println("There is no active and valid alignment data ("+message+").");
		}
	}
	
	public AddSingleAlignmentsButtonListener getAddSingleAlignmentsButtonListener(){
		return this.addAlignmentsListenerSingle;
	}

	public AddBatchAlignmentsButtonListener getAddBatchAlignmentsButtonListener(){
		return this.addAlignmentsListenerBatch;
	}
}

