package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		drawCharsWithBackground(g2, 0, 10);
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
		
		//g2d.setFont(new Font("Courier", Font.BOLD, 12)); //set the font	
		Color textColour = Color.BLACK;
		if(isAminoAcids){
			textColour = Color.WHITE;
		}
		
		int x = starting_X_pos;
		int y = starting_Y_pos;
		
		int numSequences = drawSequences.length;
		
		for(int taxon=0; taxon<numSequences; taxon++){
			// reset the x-pos to left hand side of the panel
			x = starting_X_pos;	
			
			// get this taxon and sequence as char arrays
			char[] nameChars = drawTaxa[taxon].toCharArray();
			char[] sequenceChars = drawSequences[taxon].toCharArray();

			// first draw the name chars. 
			g2d.setColor(Color.BLACK);
			g2d.drawChars(nameChars, 0, nameChars.length, x, y);

			// inset the sequences themselves a decent distance from the taxon names (so set x-pos).
			x = 200; 
			
			// now draw the sequence chars. background filled rectangles are drawn first then chars placed
			for(int c=0;c<sequenceChars.length;c++){
				Color chosenColour = chooseColours(sequenceChars[c], this.isAminoAcids);
				g2d.setColor(chosenColour);
				g2d.fillRect(x-1, y-12, 10, 15);
				g2d.setColor(textColour);
				g2d.drawChars(sequenceChars, c, 1, x, y);
				// increment the x-pos
				x+=10;
			}
			
			// increment the y-pos to move down the screen ready for the next line
			y+=15;					
		}
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