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
				XYSeries seriesAA = new XYSeries(Math.random()+"_NTentopies_"+name);
				for(int i=0;i<entropiesAA.length;i++){
					seriesAA.add(i,entropiesAA[i]);
				}
				this.entropyAAData = new XYSeriesCollection();
				this.entropyAAData.addSeries(series);
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
	
	public JScrollPane getAlignmentScroller(){
		JScrollPane scroller;
        StyledDocument doc = new DefaultStyledDocument();
        JTextPane textPane = new JTextPane(doc);
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<this.sequences.length;i++){
        	sb.append(this.sequences[i]+"\r");
        }
        textPane.setText(sb.toString());
        //Random random = new Random();
        for (int i = 0; i < textPane.getDocument().getLength(); i++) {
            SimpleAttributeSet set = new SimpleAttributeSet();
            StyleConstants.setFontFamily(set, "Courier");
         //   StyleConstants.setBackground(set, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
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
        	if(s != null){
        		c = s.toCharArray()[0];
        		switch(c){
        		case 'a':{   
        			StyleConstants.setBackground(set, new Color(255, 100, 100));
        			break;
        			}
        		case 'A':{   
        			StyleConstants.setBackground(set, new Color(255, 100, 100));
        			break;
        			}
        		case 'c':{   
        			StyleConstants.setBackground(set, new Color(100, 255, 100));
        			break;
        			}
        		case 'C':{   
        			StyleConstants.setBackground(set, new Color(100, 255, 100));
        			break;
        			}
        		case 'g':{   
        			StyleConstants.setBackground(set, new Color(100, 100, 255));
        			break;
        			}
        		case 'G':{   
        			StyleConstants.setBackground(set, new Color(100, 100, 255));
        			break;
        			}
        		case 't':{   
        			StyleConstants.setBackground(set, new Color(255, 100, 255));
        			break;
        			}
        		case 'T':{   
        			StyleConstants.setBackground(set, new Color(255, 100, 255));
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
}