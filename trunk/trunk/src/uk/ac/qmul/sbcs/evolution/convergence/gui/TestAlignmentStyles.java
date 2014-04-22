package uk.ac.qmul.sbcs.evolution.convergence.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TestAlignmentStyles {
    private void initUI() {
        JFrame frame = new JFrame(TestAlignmentStyles.class.getSimpleName());
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        StyledDocument doc = new DefaultStyledDocument();
        JTextPane textPane = new JTextPane(doc);
        textPane.setText((
      		  	  "CCACAGGGAAGCACCTGGTGGACTTG-CCGGCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCGCAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGC\r"
                + "CCACA-----GCACCTGGTGGACCTG-CCGGCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGC\r"
                + "CCACAGGGAAGCACCTGGTGGACTTGGCCGGCCCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCGCAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGC\r"
                + "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAAAGAAGCACCTGGTGGACCTG---CCCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAGGGAAGCACCTGGTAAACCTGGCCGGCTCAGAGCGAGTGGGCAAGTCGGGGGCCGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGATGTTATTGCTGCCCTGC\r"
                + "CCACAGGGAAACACCTGGTGGACCTGGCCGGCTCAGAGCGAGTGGGCAAGTCGGGGGCTGAGGGCAGCCGTCTGCGGGAGGCACAGCACATCAACAAGTCACTGTCAGCCCTGGGGGACGTTATTGCTGCCCTGCA\r").toUpperCase());
        Random random = new Random();
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
        JScrollPane scroller = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setViewportView(panel);
        panel.add(BorderLayout.CENTER,textPane);
        
        JViewport view = scroller.getViewport();
/*
        Dimension dim = new Dimension();
        dim.height = 100;
        dim.width = 600;
        view.setMaximumSize(dim);
        scroller.setViewport(view);
*/
 //       scroller.add(panel);
        scroller.setSize(600, 180);
        frame.add(BorderLayout.CENTER,scroller);

        JLabel label = new JLabel("label");
        label.setText("Aha!");
        frame.add(BorderLayout.SOUTH,label);

        frame.setSize(620, 300);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestAlignmentStyles().initUI();
            }
        });
    }

}