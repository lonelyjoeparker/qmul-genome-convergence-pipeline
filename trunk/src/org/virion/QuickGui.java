package org.virion;

import java.awt.FlowLayout;
import javax.swing.*;


public class QuickGui extends JFrame{

	JLabel label;
	JPanel panel;
	JTextArea text;
	/**
	 * @param args
	 */
	public QuickGui(){
		setLayout(new FlowLayout());
		setTitle("a frame");
		label = new JLabel("somelabel");
		text = new JTextArea();
		text.setText("lalalalalala\nlalalalala\ns\ns\ne\nd\nr\nkjhkjhkjhkjhkjh\n");

		panel = new JPanel();
		panel.add(label);
		panel.add(text);
		add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800,400);
	}

	public QuickGui(String init){
		setLayout(new FlowLayout());
		setTitle("a frame");
		label = new JLabel("initl");
		text = new JTextArea();
		text.setText("lalalalalala\nlalalalala\ns\ns\ne\nd\nr\nkjhkjhkjhkjhkjh\n");

		panel = new JPanel();
		panel.add(label);
		panel.add(text);
		add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800,400);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuickGui gui = new QuickGui("set me up");
		gui.setVisible(true);
	}

}
