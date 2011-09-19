package uk.ac.qmul.sbcs.evolution.convergence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class BasicFileReader {

	private File file;
	private ArrayList<String> rawInput = new ArrayList<String>();

	public ArrayList<String> loadSequences(File inputFile){
		file = inputFile;
		try{
			if(file.canRead()){
				BufferedReader inputBuffer = new BufferedReader(new FileReader(file));
				try{
					String line = null;
					while((line = inputBuffer.readLine()) != null){
						rawInput.add(line + System.getProperty("line.separator"));
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				finally{
					inputBuffer.close();
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return rawInput;
	}
}
