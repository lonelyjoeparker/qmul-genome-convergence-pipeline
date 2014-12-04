package uk.ac.qmul.sbcs.evolution.convergence.gui.wireframe;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * The view object just sets up and displays the GUI (nb could be a text-only interface; 'view' here means 'rendering of I/O functions' not literal view.)
 * The ActionListeners etc to give the buttons and other UI components functionality are specified in the controller object
 * Instead of performing any operations itself, the view merely gets/sets values from the UI, and sets the ActionListner for the button
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class MinimalMVCView extends JFrame {

	private JLabel label;
	private JButton button;
	private JTextField textfield;
	
	
	public MinimalMVCView() throws HeadlessException {
		// TODO Auto-generated constructor stub
		label = new JLabel("starting text label");
		button = new JButton("push me");
		textfield = new JTextField("starting text area");
		setLayout(new BorderLayout());
		add(label,BorderLayout.NORTH);
		add(button,BorderLayout.SOUTH);
		add(textfield,BorderLayout.EAST);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300,300);
	}

	public MinimalMVCView(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public MinimalMVCView(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public MinimalMVCView(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	// passes the new value for label text to the internal label
	public void setText(String text){
		label.setText(text);
	}
	
	// gets the value of the text in textarea
	public String getText(){
		return textfield.getText();
	}
	
	public void setButtonActionListerner(ActionListener al){
		button.addActionListener(al);
	}
}
