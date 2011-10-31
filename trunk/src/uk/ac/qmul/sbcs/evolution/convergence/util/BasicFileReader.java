package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class BasicFileReader {

	private File file;
	private ArrayList<String> rawInput = new ArrayList<String>();

	public ArrayList<String> loadSequences(File inputFile){
		file = inputFile;
		int lines = 0;
		try{
			if(file.canRead()){
				BufferedReader inputBuffer = new BufferedReader(new FileReader(file));
				try{
					String line = null;
					while((line = inputBuffer.readLine()) != null){
						int readlength = 5;
						if(line.length()<5){
							readlength = line.length();
						}
						System.out.println("buffer reading:\t"+line.substring(0,readlength));
						rawInput.add(line); // LINE SEPARATORS REMOVED, 30/09/2011
						lines++;
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
		System.out.println("BasicFileReader read "+lines+" lines.");
		return rawInput;
	}

	public ArrayList<String> loadSequences(File inputFile, boolean reportBuffer){
		file = inputFile;
		int lines = 0;
		try{
			if(file.canRead()){
				BufferedReader inputBuffer = new BufferedReader(new FileReader(file));
				try{
					String line = null;
					while((line = inputBuffer.readLine()) != null){
						if(reportBuffer){
							int readlength = 20;
							if(line.length()<20){
								readlength = line.length();
							}
							System.out.println("buffer reading:\t"+line.substring(0,readlength));
						}
						rawInput.add(line); // LINE SEPARATORS REMOVED, 30/09/2011
						lines++;
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
		System.out.println("BasicFileReader read "+lines+" lines.");
		return rawInput;
	}
}
