package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.text.StyleConstants;

/**
 * Class to extend JPanel to paint alignments directly as rendered java.awt.Graphics2D canvasses.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 * @see JPanel
 * @see uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe.ColouredCharPanelWireframe
 */
public class DisplayAlignmentPanel extends JPanel{
	private final String[] drawSequences;	// sequences of nucleotides or amino acids
	private final String[] drawTaxa;		// names of the taxa
	private final boolean isAminoAcids;		// TRUE if amino acids, else nucleotide or codon colours will be used.
	protected boolean allowRepaintFlag;		// A flag to only allow repainting via paintComponent() when desired.
	private int paintCallsAllowed = 0;
	private int paintCallsDenied = 0;
	private Color[][] sequenceCharColours = null;
	private long lastRepaintMillis = 0;
	private final int insetSequencesXpos = 200;	// inset the sequences this far into the window
	private final int x_cols_increment = 10;	// how much each char increments x-pos by
	private final int y_rows_increment = 15;	// hom much each row / taxon line increments y-pos by
	private int number_of_taxon_name_chars_before_sequences_start;
	private char[][] taxonNamesAsCharArray;	// Taxon names as a char[][] array, nTaxa * insetSequencesXpos
	private char[][] sequencesAsCharArray;	// Sequences as a char[][] array, nTaxa * longest seq
	private static final Font font = new Font("Monospaced", Font.PLAIN, 16);
	
	/**
	 * Default no-arg constructor, deprecated.
	 */
	@Deprecated
	public DisplayAlignmentPanel(){
		// no-arg constructor, deprecated
		drawSequences = null;
		drawTaxa = null;
		isAminoAcids = false;
	}
	
	/**
	 * Creates a DisplayAlignmentPanel (subclass of JPanel) usinf Graphics2D canvas in overridden paintComponent() method
	 * @param sequences - nucleotide, codon or AA sequences
	 * @param taxa - taxon names
	 * @param isTranslated - TRUE if amino acid colours are to be used, else DNA (nucleotide / codon) colours are assumed.
	 */
	public DisplayAlignmentPanel(String[] sequences, String[] taxa, boolean isTranslated) {
		drawSequences = sequences;
		drawTaxa = taxa;
		isAminoAcids = isTranslated;
		number_of_taxon_name_chars_before_sequences_start = (insetSequencesXpos / x_cols_increment);
		// Initialize the Color[][] matrix for the nucleotide or AA colouration
		sequenceCharColours = new Color[drawSequences.length][drawSequences[0].toCharArray().length]; // assume that all sequences same length
		// Initialize the char matrix for names
		taxonNamesAsCharArray = new char[drawTaxa.length][number_of_taxon_name_chars_before_sequences_start];	// fix the max sequence length to (sequence chars inset start / char increment). NB this is in CHARS not PIXELS so not a good measure but will do for now
		// Initialise the char matrix for seqs
		sequencesAsCharArray = new char[drawSequences.length][drawSequences[0].toCharArray().length]; // assume that all sequences same length 
		// Populate the instance char and Color matrices
		for(int taxon=0;taxon<drawSequences.length;taxon++){
			// first get the arrays for seq and name (1-time only)
			char[] someName = this.repaintOperationNameCharArray(taxon);
			char[] someSequence = this.repaintOperationSeqCharArray(taxon);
			// iterate down sequence adding to char[][] and Color[][] matrices
			for(int c=0;c<someSequence.length;c++){
				sequenceCharColours[taxon][c] = chooseColours(someSequence[c],isAminoAcids);
				sequencesAsCharArray[taxon][c] = someSequence[c];
			}
			// add as many of the name chars (<number_of_taxon_name_chars_before_sequences_start) as we can
			if(someName.length < number_of_taxon_name_chars_before_sequences_start){
				// we'll have to pad
				for(int c=0;c<someName.length;c++){
					taxonNamesAsCharArray[taxon][c] = someName[c];
				}
			}else{
				// someName.length is >= number_of_taxon_name_chars_before_sequences_start so use number_of_taxon_name_chars_before_sequences_start and truncate
				for(int c=0;c<number_of_taxon_name_chars_before_sequences_start;c++){
					taxonNamesAsCharArray[taxon][c] = someName[c];
				}
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		//allowRepaintFlag = true;
		// set monospace font
		g.setFont(font);
		
//		if(allowRepaintFlag){
		long currentTimeMillis = System.currentTimeMillis();
		if((currentTimeMillis - this.lastRepaintMillis) > 0){
			paintCallsAllowed++;
			lastRepaintMillis = currentTimeMillis;
	//		System.out.println("\tpaintComponent() call allowed #"+paintCallsAllowed+", context "+g.toString());	//disabling these print statements in case they're slowin things down
			Graphics2D g2 = (Graphics2D) g;
			drawCharsWithBackground(g2, 0, 10);
			allowRepaintFlag = false;
		}else{
			paintCallsDenied++;
	//		System.out.println("paintComponent() call ignored #"+paintCallsDenied+", context "+g.toString());		//disabling these print statements in case they're slowin things down
		}
		
	}
	
	/**
	 * The main drawing method for alignments.
	 * @param g2d - the active Graphics2D. Good old g2d pointer, my friend.... ;)
	 * @param starting_X_pos - the starting Xpos
	 * @param starting_Y_pos - the starting Ypos
	 */
	public void drawCharsWithBackground(Graphics2D g2d, int starting_X_pos, int starting_Y_pos){
		/*
		 * The main drawing method for alignments:
		 *  set font face
		 * 	for each line (i in num. taxa):
		 * 		draw taxon[i] in chars on white, incrementing x
		 * 		draw sequence[i] in background blocks, then chars, continuing to increment x
		 * 		increment y
		 */

		// Get viewable area
		Rectangle viewable = this.getVisibleRect();
		int viewable_x_0 = viewable.x;
		int viewable_x_1 = viewable.x + viewable.width;
		int viewable_y_0 = viewable.y;
		int viewable_y_1 = viewable.y + viewable.height;
		// debug only
		//System.out.println("Viewable:\t("+viewable_x_0+","+viewable_x_1+") ("+viewable_y_0+","+viewable_y_1+")");
		
		/*
		 *  Compute the number of sequences and chars that this area will display 
		 */
		// number of lines / sequences
		int viewable_seqs_min = viewable.y / y_rows_increment;
		int viewable_seqs_max = Math.min((((viewable_y_0 + viewable.height)/y_rows_increment)+5),drawTaxa.length);	// take a min so we don't run off the end of the array, but add a bit extra just to cover fast scrolling
		// number of chars from the taxon name - counts from right as can't scroll past start at left of screen(!)
		int viewable_name_chars;		
		// number of chars from the sequences themselves
		int viewable_sequence_chars_start;
		int viewable_sequence_chars_end;
		if(viewable_x_0 >= insetSequencesXpos){
			/* 
			 * the viewport has already scrolled past the end of the taxon names/start of the taxon sequences:
			 * none of the taxon names will be visible
			 * sequences start will be (x_0 - offset)/increment
			 * sequences end will be (x_1/increment)-offset
			 */			
			viewable_name_chars = 0;
			viewable_sequence_chars_start = (viewable_x_0 - insetSequencesXpos) / x_cols_increment;
		}else{
			/* 
			 * the viewport contains some of the taxon names and the start of the taxon sequences:
			 * none of the taxon names will be visible
			 * sequences start will be 0
			 * sequences end will be (x_1/increment)-offset
			 */			
			viewable_name_chars = (insetSequencesXpos - viewable_x_0) / x_cols_increment;
			viewable_sequence_chars_start = 0;
		}
		viewable_name_chars = Math.min(viewable_name_chars+1, 20);
		viewable_sequence_chars_end = (viewable_x_1 / x_cols_increment) - number_of_taxon_name_chars_before_sequences_start;
		viewable_sequence_chars_end = Math.min(viewable_sequence_chars_end+20, sequencesAsCharArray[0].length);	// take a min so we don't run off the end of the array, but add a bit extra to cover fast scrolling.
		// what can we see? (DEBUG only)
		//System.out.println("Draw limits:\t("+viewable_seqs_min+","+viewable_seqs_max+") seqs, ("+viewable_sequence_chars_start+","+viewable_sequence_chars_end+") sequence chars, ("+viewable_name_chars+") name chars");
		
		// Clear the canvas before repainting
		g2d.setColor(Color.WHITE);
		//g2d.fillRect(0, 0, this.WIDTH-1, this.HEIGHT-1);	// repaints the whole canvas for now
		g2d.fillRect(viewable.x,viewable.y,viewable.width,viewable.height);	// repaints the VIEWABLE canvas 
		
		//g2d.setFont(new Font("Courier", Font.BOLD, 12)); //set the font	
		g2d.setFont(font);
		Color textColour = Color.BLACK;
		if(isAminoAcids){
			textColour = Color.WHITE;
		}

		/*
		 * Old drawing logic, which drew everything
		 */
//		// Clear the canvas before repainting
//		g2d.setColor(Color.WHITE);
//		g2d.fillRect(0, 0, this.WIDTH-1, this.HEIGHT-1);	// repaints the whole canvas for now
//		int x = starting_X_pos;
//		int y = starting_Y_pos;
//		
//		int numSequences = drawSequences.length;
//		
//		for(int taxon=0; taxon<numSequences; taxon++){
//			// reset the x-pos to left hand side of the panel
//			x = starting_X_pos;	
//			
//			// get this taxon and sequence as char arrays
//			/*
//			char[] nameChars = drawTaxa[taxon].toCharArray();
//			char[] sequenceChars = drawSequences[taxon].toCharArray();
//			 */
//
//			// first draw the name chars. 
//
//			g2d.setColor(Color.BLACK);								// draw in main call			
//			g2d.drawChars(taxonNamesAsCharArray[taxon], 0, taxonNamesAsCharArray[taxon].length, x, y);	// draw in main call
//			//this.repaintOperationDrawTaxonNames(g2d, taxonNamesAsCharArray[taxon], x, y);	// draw in encapsulated private method call (seems worse)
//			
//			// inset the sequences themselves a decent distance from the taxon names (so set x-pos).
//			x = insetSequencesXpos; 
//			
//			// now draw the sequence chars. background filled rectangles are drawn first then chars placed
//			for(int c=0;c<sequencesAsCharArray[taxon].length;c++){
//				// Pick the colour for the amino acid or nucleotide background
//				//Color chosenColour = chooseColours(sequenceChars[c], this.isAminoAcids); // now using Color[][] array instantiated earlier
//				Color chosenColour = this.sequenceCharColours[taxon][c];
//				g2d.setColor(chosenColour);
//				g2d.fillRect(x-1, y-12, 10, 15);
//				g2d.setColor(textColour);
//				/*
//				 * experiment with moving the char drawing itself to one go
//				g2d.drawChars(sequencesAsCharArray[taxon], c, 1, x, y);
//				 * 
//				 */
//				//this.repaintOperationDrawSequenceChar(g2d, textColour, sequenceChars, taxon, c, taxon, y);
//				// increment the x-pos
//				x+=x_cols_increment;
//			}
//			// experiment with moving the char drawing itself to one go
//			g2d.drawChars(sequencesAsCharArray[taxon], 0, sequencesAsCharArray[taxon].length, insetSequencesXpos, y);
//			
//			// increment the y-pos to move down the screen ready for the next line
//			y+=y_rows_increment;					
//		}

		/*
		 * New drawing logic, only draws (just) bigger than the viewport
		 */
		// Clear the canvas before repainting
		g2d.setColor(Color.WHITE);
		g2d.fillRect(viewable.x,viewable.y,viewable.width,viewable.height);	// repaints the VIEWABLE canvas 

		int x = starting_X_pos;
		int y = starting_Y_pos;
		
		for(int taxon=viewable_seqs_min; taxon<viewable_seqs_max; taxon++){
			// reset the x-pos to left hand side of the panel
			x = starting_X_pos;
			// advance the y-pos 
			y = starting_Y_pos + (taxon * y_rows_increment);
			
			// first draw the name chars. 
			g2d.setColor(Color.BLACK);			
			// draw the whole name for now
			g2d.drawChars(taxonNamesAsCharArray[taxon], (0), (taxonNamesAsCharArray[taxon].length), x, y);
			
			// inset the sequences themselves a decent distance from the taxon names (so set x-pos).
			x = insetSequencesXpos + (viewable_sequence_chars_start * x_cols_increment); 
			
			// now draw the sequence chars. background filled rectangles are drawn first then chars placed
			for(int c=viewable_sequence_chars_start;c<viewable_sequence_chars_end;c++){
				// Pick the colour for the amino acid or nucleotide background
				Color chosenColour = this.sequenceCharColours[taxon][c];
				g2d.setColor(chosenColour);
				g2d.fillRect(x-1, y-12, 10, 15);
				g2d.setColor(textColour);
				// increment the x-pos
				x+=x_cols_increment;
			}
			// experiment with moving the char drawing itself to one go
			g2d.drawChars(sequencesAsCharArray[taxon], 0, sequencesAsCharArray[taxon].length, insetSequencesXpos, y);
			//a bit more strict with the sequence char drawing:
			//g2d.drawChars(sequencesAsCharArray[taxon], viewable_sequence_chars_start, viewable_sequence_chars_end-viewable_sequence_chars_start, (insetSequencesXpos+(viewable_sequence_chars_start*x_cols_increment)), y);
			
			// increment the y-pos to move down the screen ready for the next line
			y+=y_rows_increment;					
		}

	}
	
	/**
	 * Privately encapsulated part of the paintComponent (drawCharsWithBackground()) logic
	 * @param taxonIndex
	 * @return
	 */
	private char[] repaintOperationNameCharArray(int taxonIndex){
		return drawTaxa[taxonIndex].toCharArray();
	}
	
	/**
	 * Privately encapsulated part of the paintComponent (drawCharsWithBackground()) logic
	 * @param taxonIndex
	 * @return
	 */
	private char[] repaintOperationSeqCharArray(int taxonIndex){
		return drawSequences[taxonIndex].toCharArray();
	}

	/**
	 * Privately encapsulated part of the paintComponent (drawCharsWithBackground()) logic
	 * @param g2d
	 * @param nameChars
	 * @param x
	 * @param y
	 */
	private void repaintOperationDrawTaxonNames(Graphics2D g2d, char[] nameChars, int x, int y){
		// first draw the name chars. 
		g2d.setColor(Color.BLACK);
		g2d.drawChars(nameChars, 0, nameChars.length, x, y);
	}
	
	/**
	 * Privately encapsulated part of the paintComponent (drawCharsWithBackground()) logic
	 * @param g2d - the Graphics2D context
	 * @param textColour - the colour of the text
	 * @param sequenceChars - the nucleotide or AA sequence to draw
	 * @param taxon - the taxon counter
	 * @param c - the char counter
	 * @param x - x-pos
	 * @param y - y-pos
	 */
	private void repaintOperationDrawSequenceChar(Graphics2D g2d, Color textColour, char[] sequenceChars, int taxon, int c, int x, int y){
		Color chosenColour = this.sequenceCharColours[taxon][c];
		g2d.setColor(chosenColour);
		g2d.fillRect(x-1, y-12, 10, 15);
		g2d.setColor(textColour);
		g2d.drawChars(sequenceChars, c, 1, x, y);
	}
	
	/**
	 * Picks the background colours for a character.
	 * @param c - sequence character (AA or nucleotide)
	 * @param useAAcolours - should aminio acid colourings be used?
	 * @return - a Color corresponding to the nucleotide or AA argument
	 */
	private static Color chooseColours(char c, boolean useAAcolours){
		Color color;
		if(useAAcolours){
			// use amino-acid colours
    		switch(c){
	    		case 'a':
	    		case 'A':
	    		case 'g':
	    		case 'G':
	    		case 'p':
	    		case 'P':
	    		case 's':
	    		case 'S':
	    		case 't':
	    		case 'T':{   
	    			color = Color.MAGENTA;
	    			break;
	    			}
	    		case 'c':
	    		case 'C':{   
	    			color = Color.RED;
	    			break;
	    			}
	    		case 'f':
	    		case 'F':
	    		case 'w':
	    		case 'W':
	    		case 'y':
	    		case 'Y':{   
	    			color = Color.BLUE;
	    			break;
	    			}
	    		case 'h':
	    		case 'H':
	    		case 'k':
	    		case 'K':
	    		case 'r':
	    		case 'R':{   
	    			color = new Color(0,183,247);
	    			break;
	    			}
	    		case 'i':
	    		case 'I':
	    		case 'l':
	    		case 'L':
	    		case 'm':
	    		case 'M':
	    		case 'v':
	    		case 'V':{   
	    			color = new Color(0,132,4);
	    			break;
	    			}
	    		case 'b':
	    		case 'B':
	    		case 'd':
	    		case 'D':
	    		case 'e':
	    		case 'E':
	    		case 'n':
	    		case 'N':
	    		case 'q':
	    		case 'Q':
	    		case 'z':
	    		case 'Z':{   
	    			color = Color.BLACK;
	    			break;
	    			}
	    		case 'x':
	    		case 'X':{   
	    			color = Color.DARK_GRAY;
	    			break;
	    			}
				default:{
					color = Color.LIGHT_GRAY;
					break;
				}
    		}
		}else{
			// use nucleotide colours
			switch(c){
				case 'a':
				case 'A':{   
					color = new Color(255, 100, 100);
					break;
				}
				case 'c':
				case 'C':{   
					color = new Color(100, 255, 100);
					break;
				}
				case 'g':
				case 'G':{   
					color = new Color(100, 100, 255);
					break;
				}
				case 't':
				case 'T':
				case 'u':
				case 'U':{   
					color = new Color(255, 100, 255);
					break;
				}
				case '-':{   
					color = Color.LIGHT_GRAY;
					break;
				}
				default:{
					color = Color.WHITE;
					break;
				}
			}
		}
		return color;
	}
}