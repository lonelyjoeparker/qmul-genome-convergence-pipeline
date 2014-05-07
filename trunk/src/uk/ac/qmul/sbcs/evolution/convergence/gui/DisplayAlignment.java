package uk.ac.qmul.sbcs.evolution.convergence.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import uk.ac.qmul.sbcs.evolution.convergence.AlignedSequenceRepresentation;
import uk.ac.qmul.sbcs.evolution.convergence.SequenceTypeNotSupportedException;
import uk.ac.qmul.sbcs.evolution.convergence.runners.BasicAlignmentStats;

public class DisplayAlignment{
	private String name;
	private float[] entropies;
	private float[] entropiesAA;
	XYSeriesCollection entropyNTData;
	XYSeriesCollection entropyAAData;
	private String[] taxa;
	private String[] sequences;
	private String[] sequencesAA;
	private int numberOfTaxa = 0;
	private int numberOfSitesNT = 0;
	private int numberOfInvariantSitesNT = 0;
	private int numberOfSitesAA = 0;
	private int numberOfInvariantSitesAA = 0;
	private float meanSitewiseEntropyNT = 0;
	private float meanSitewiseEntropyAA = 0;
	private float meanTaxonwiseLongestUngappedSequenceNT = 0;
	private float meanTaxonwiseLongestUngappedSequenceAA = 0;
	private float longestNonZeroEntropyRunNT = 0;
	private float whichNonZeroEntropyRunNT = 0;
	private float longestNonZeroEntropyRunAA = 0;
	private float whichNonZeroEntropyRunAA = 0;
	
	public DisplayAlignment(String newName, AlignedSequenceRepresentation asr){
		this.name = newName;
		this.taxa = asr.getTaxaList().toArray(new String[asr.getNumberOfTaxa()]);
		this.entropies = asr.getSitewiseEntropies(true);
		XYSeries series = new XYSeries(Math.random()+"_NTentopies_"+name);
		for(int i=0;i<entropies.length;i++){
			series.add(i,entropies[i]);
		}
		this.entropyNTData = new XYSeriesCollection();
		this.entropyNTData.addSeries(series);
		this.sequences = new String[asr.getNumberOfTaxa()];
		for(int i=0;i<sequences.length;i++){
			sequences[i] = new String(asr.getSequenceChars(i));
		}
		this.numberOfTaxa = asr.getNumberOfTaxa();
		this.numberOfSitesNT = asr.getNumberOfSites();
		this.numberOfInvariantSitesNT = asr.getNumberOfInvariantSites();
		this.meanSitewiseEntropyNT = asr.getMeanSitewiseEntropy();
		this.meanTaxonwiseLongestUngappedSequenceNT = asr.getMeanTaxonwiseLongestUngappedSequence();
		float [] entropyStats = BasicAlignmentStats.parseEntropiesToFindLongestNonzeroRun(this.entropies);
		this.longestNonZeroEntropyRunNT = entropyStats[0];
		this.whichNonZeroEntropyRunNT = entropyStats[1];
		
		if(!asr.isAA()){
			/*
			 * Attempt to translate the alignment and put AA data
			 */
			try {
				asr.translate(true);
				this.numberOfSitesAA = asr.getNumberOfSites();
				this.numberOfInvariantSitesAA = asr.getNumberOfInvariantSites();
				this.meanSitewiseEntropyAA = asr.getMeanSitewiseEntropy();
				this.meanTaxonwiseLongestUngappedSequenceAA = asr.getMeanTaxonwiseLongestUngappedSequence();
				this.sequencesAA = new String[asr.getNumberOfTaxa()];
				for(int i=0;i<sequencesAA.length;i++){
					sequencesAA[i] = new String(asr.getSequenceChars(i));
				}
				this.entropiesAA = asr.getSitewiseEntropies(true);
				XYSeries seriesAA = new XYSeries(Math.random()+"_AAentropies_"+name);
				for(int i=0;i<entropiesAA.length;i++){
					seriesAA.add((i*3),entropiesAA[i]);
				}
				this.entropyAAData = new XYSeriesCollection();
				this.entropyAAData.addSeries(seriesAA);
			} catch (SequenceTypeNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("parsed "+this.name);
	}
	
	public DisplayAlignment(String newName){
		this.name = newName;
		this.entropies = new float[100];
		float[] x_data = new float[100];
		for(int i=0;i<100;i++){
			entropies[i] = 0;
			x_data[i] = i;
		}
		int startPos = new Random().nextInt(90);
		for(int i=startPos;i<(startPos+10);i++){
			entropies[i] = (float) ((Math.random()*10)+10);
		}
		XYSeries series = new XYSeries(Math.random()+"_name");
		for(int i=0;i<100;i++){
			series.add(x_data[i],entropies[i]);
		}
		this.entropyNTData = new XYSeriesCollection();
		this.entropyNTData.addSeries(series);
		this.sequences = new String[5];
		sequences[0] = "CCACAGGGAAGCACCTGGTGGACTTG-CCGGCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCGCAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGC";
		sequences[1] = "CCACA-----GCACCTGGTGGACCTG-CCGGCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGC";
		sequences[2] = "CCACAGGGAAGCACCTGGTGGACTTGGCCGGCCCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCGCAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGC";
		sequences[3] = "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC";
		sequences[4] = "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC";
		this.numberOfTaxa = new Random().nextInt();
		this.numberOfSitesNT = new Random().nextInt();

	}
	
	public float[] getEntropies() {
		return this.entropies;
	}
	
	public String[] getSequences(){
		return this.sequences;
	}
	
	public String toString(){
		return this.name;
	}

	public String getName() {
		return this.name;
	}
	
	protected void setName(String newName) {
		this.name = newName;
	}

	public JScrollPane getAlignmentScroller(){
		JScrollPane scroller;
        StyledDocument doc = new DefaultStyledDocument();
        JTextPane textPane = new JTextPane(doc);
        StringBuffer sb = new StringBuffer();
        int numTaxa = this.sequences.length;	// getting num Taxa from sequences.length not taxa.length, hopefully it's safer	..
        int numPosn = this.sequences[0].length();
        int totalChars = (25 + numPosn + 1) * numTaxa; // an extra char per line, for the line endings!!
        boolean[] skipColourChars = new boolean[totalChars]; // boolean: colour chars or not?
        if(this.sequences != null){
        	for(int t=0;t<numTaxa;t++){
        		String taxon = this.taxa[t];
        		if(taxon.length() > 22){
        			taxon = taxon.substring(0,22);
        		}
        		sb.append(taxon);
        		// pad taxon names..
        		for(int s = taxon.length();s<25;s++){
        			sb.append(" ");
        		}
        		for(int c=0;c<25;c++){
        			int i = ((25 + numPosn + 1) * t) + c; // +1 for \n char
        			skipColourChars[i] = true;
        		}
        		sb.append(this.sequences[t]+"\r");
        	}  
        }else{
        	for(int t=0;t<this.sequences.length;t++){
        		String taxon = this.taxa[t];
        		if(taxon.length() > 22){
        			taxon = taxon.substring(0,22);
        		}
        		sb.append(taxon);
        		// pad taxon names..
        		for(int s = taxon.length();s<25;s++){
        			sb.append(" ");
        		}
        		for(int c=0;c<25;c++){
        			int i = ((25 + numPosn + 1) * t) + c; // +1 for \n char
        			skipColourChars[i] = true;
        		}
        		sb.append("null\r");
        	}
        }
        textPane.setText(sb.toString());
        //Random random = new Random();
//        for (int i = 0; i < textPane.getDocument().getLength(); i++) {
        for (int i = 0; i < textPane.getDocument().getLength(); i++) {
            SimpleAttributeSet set = new SimpleAttributeSet();
            StyleConstants.setFontFamily(set, "Courier");
         //   StyleConstants.setBackground(set, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))); //BG colour
         //   StyleConstants.setForeground(set, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))); //FG colour
         //   StyleConstants.setFontSize(set, random.nextInt(12) + 12);
          //  StyleConstants.setBold(set, random.nextBoolean());
           // StyleConstants.setItalic(set, random.nextBoolean());
           // StyleConstants.setUnderline(set, random.nextBoolean());

            char c;
            String s = null;
            try {
				s = doc.getText(i, 1);
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if((s != null)&&(!skipColourChars[i])){
        		c = s.toCharArray()[0];
                StyleConstants.setFontFamily(set, "Andale mono");
        		switch(c){
        		case 'a':
        		case 'A':{   
        			StyleConstants.setBackground(set, new Color(255, 100, 100));
        			break;
        			}
        		case 'c':
        		case 'C':{   
        			StyleConstants.setBackground(set, new Color(100, 255, 100));
        			break;
        			}
        		case 'g':
        		case 'G':{   
        			StyleConstants.setBackground(set, new Color(100, 100, 255));
        			break;
        			}
        		case 't':
        		case 'T':
        		case 'u':
        		case 'U':{   
        			StyleConstants.setBackground(set, new Color(255, 100, 255));
        			break;
        			}
        		case '-':{   
        			StyleConstants.setBackground(set, Color.LIGHT_GRAY);
        			break;
        			}
        		}
        	}
            doc.setCharacterAttributes(i, 1, set, true);
        }

        /*
        Dimension dim = new Dimension();
        dim.height = 150;
        dim.width = 200;
        textPane.setPreferredSize(dim);
        textPane.setSize(200,150);
        */
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,textPane);
        scroller = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setViewportView(panel);
		return scroller;
	}

	public JScrollPane getAlignmentScrollerAA(){
		JScrollPane scroller;
        StyledDocument doc = new DefaultStyledDocument();
        JTextPane textPane = new JTextPane(doc);
        StringBuffer sb = new StringBuffer();
        int numTaxa = this.sequencesAA.length;	// getting num Taxa from sequencesAA.length not taxa.length, hopefully it's safer	..
        int numPosn = this.sequencesAA[0].length();
        int totalChars = (25 + numPosn + 1) * numTaxa; // an extra char per line, for the line endings!!
        boolean[] skipColourChars = new boolean[totalChars]; // boolean: colour chars or not?
      if(this.sequencesAA != null){
			for (int t = 0; t < this.sequencesAA.length; t++) {
				String taxon = this.taxa[t];
				if (taxon.length() > 22) {
					taxon = taxon.substring(0, 22);
				}
				sb.append(taxon);
				// pad taxon names..
				for (int s = taxon.length(); s < 25; s++) {
					sb.append(" ");
				}
        		for(int c=0;c<25;c++){
        			int i = ((25 + numPosn + 1) * t) + c; // +1 for \n char
        			skipColourChars[i] = true;
        		}
				sb.append(this.sequencesAA[t] + "\r");
			}
		}else{
			for (int t = 0; t < this.taxa.length; t++) {
				String taxon = this.taxa[t];
				if (taxon.length() > 22) {
					taxon = taxon.substring(0, 22);
				}
				sb.append(taxon);
				// pad taxon names..
				for (int s = taxon.length(); s < 25; s++) {
					sb.append(" ");
				}
        		for(int c=0;c<25;c++){
        			int i = ((25 + numPosn + 1) * t) + c; // +1 for \n char
        			skipColourChars[i] = true;
        		}
				sb.append("null\r");
			}
		}
		textPane.setText(sb.toString());
        //Random random = new Random();
        for (int i = 0; i < textPane.getDocument().getLength(); i++) {
            SimpleAttributeSet set = new SimpleAttributeSet();
            StyleConstants.setFontFamily(set, "Courier");
            //StyleConstants.setForeground(set, Color.WHITE);
            //StyleConstants.setBackground(set, Color.LIGHT_GRAY);
         //   StyleConstants.setForeground(set, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
         //   StyleConstants.setFontSize(set, random.nextInt(12) + 12);
          //  StyleConstants.setBold(set, random.nextBoolean());
           // StyleConstants.setItalic(set, random.nextBoolean());
           // StyleConstants.setUnderline(set, random.nextBoolean());

            char c;
            String s = null;
            try {
				s = doc.getText(i, 1);
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if((s != null)&&(!skipColourChars[i])){
                StyleConstants.setFontFamily(set, "Andale mono");
                StyleConstants.setForeground(set, Color.WHITE);
                StyleConstants.setBackground(set, Color.LIGHT_GRAY);
        		c = s.toCharArray()[0];
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
        			StyleConstants.setBackground(set, Color.MAGENTA);
        			break;
        			}
        		case 'c':
        		case 'C':{   
        			StyleConstants.setBackground(set, Color.RED);
        			break;
        			}
        		case 'f':
        		case 'F':
        		case 'w':
        		case 'W':
        		case 'y':
        		case 'Y':{   
        			StyleConstants.setBackground(set, Color.BLUE);
        			break;
        			}
        		case 'h':
        		case 'H':
        		case 'k':
        		case 'K':
        		case 'r':
        		case 'R':{   
        			StyleConstants.setBackground(set, new Color(0,183,247));
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
        			StyleConstants.setBackground(set, new Color(0,132,4));
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
        			StyleConstants.setBackground(set, Color.BLACK);
        			break;
        			}
        		case 'x':
        		case 'X':{   
        			StyleConstants.setBackground(set, Color.DARK_GRAY);
        			break;
        			}
        		}
        	}
            doc.setCharacterAttributes(i, 1, set, true);
        }

        /*
        Dimension dim = new Dimension();
        dim.height = 150;
        dim.width = 200;
        textPane.setPreferredSize(dim);
        textPane.setSize(200,150);
        */
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,textPane);
        scroller = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setViewportView(panel);
		return scroller;
	}

	
	public int getNumberOfTaxa() {
		return this.numberOfTaxa;
	}

	public int getNumberOfSites() {
		return this.numberOfSitesNT;
	}

	public float[] getEntropiesAA() {
		return entropiesAA;
	}

	public XYSeriesCollection getEntropyNTData() {
		return entropyNTData;
	}

	public XYSeriesCollection getEntropyAAData() {
		return entropyAAData;
	}

	public String[] getSequencesAA() {
		return sequencesAA;
	}

	public int getNumberOfSitesNT() {
		return numberOfSitesNT;
	}

	public int getNumberOfInvariantSitesNT() {
		return numberOfInvariantSitesNT;
	}

	public int getNumberOfSitesAA() {
		return numberOfSitesAA;
	}

	public int getNumberOfInvariantSitesAA() {
		return numberOfInvariantSitesAA;
	}

	public float getMeanSitewiseEntropyNT() {
		return meanSitewiseEntropyNT;
	}

	public float getMeanSitewiseEntropyAA() {
		return meanSitewiseEntropyAA;
	}

	public float getMeanTaxonwiseLongestUngappedSequenceNT() {
		return meanTaxonwiseLongestUngappedSequenceNT;
	}

	public float getMeanTaxonwiseLongestUngappedSequenceAA() {
		return meanTaxonwiseLongestUngappedSequenceAA;
	}

	public float getLongestNonZeroEntropyRunNT() {
		return longestNonZeroEntropyRunNT;
	}

	public float getWhichNonZeroEntropyRunNT() {
		return whichNonZeroEntropyRunNT;
	}

	public float getLongestNonZeroEntropyRunAA() {
		return longestNonZeroEntropyRunAA;
	}

	public float getWhichNonZeroEntropyRunAA() {
		return whichNonZeroEntropyRunAA;
	}

	/**
	 * Guesses the locus name from the alignment name
	 * @return
	 */
	public String getNameGuess() {
		// TODO Auto-generated method stub
		if(this.name != null){
			String[] separated = this.name.split(".");
			if(separated.length > 0){
				return separated[0];			
			}else{
				return name;
			}
		}else{
			return name;
		}
	}
	
	public String getFirstSequence(){
		return this.sequences[0];
	}
}