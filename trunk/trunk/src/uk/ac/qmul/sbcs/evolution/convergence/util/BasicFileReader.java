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
						if(line.length() > 1){
							System.out.println("buffer reading (length "+line.length()+"):\t"+line.substring(0,readlength));
							rawInput.add(line.toUpperCase());
							lines++;
						} // LINE SEPARATORS REMOVED, 30/09/2011
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

	public ArrayList<String> loadSequences(File inputFile, boolean reportBufferStatus, boolean capitaliseInput){
		file = inputFile;
		int lines = 0;
		if(reportBufferStatus){
			System.out.println("Trying to read file "+file.getAbsolutePath());
		}
		try{
			if(file.canRead()){
				BufferedReader inputBuffer = new BufferedReader(new FileReader(file));
				int readlength = 20;
				try{
					String line = null;
					while((line = inputBuffer.readLine()) != null){
						if(reportBufferStatus){
							if(line.length()<20){
								readlength = line.length();
								if(line =="\n"){assert(false);}
								if(line =="\r"){assert(false);}
							}
							System.out.println("buffer reading:\t"+line.substring(0,readlength));
						}
						if(line.length() > 1){
							if(reportBufferStatus){
								System.out.println("buffer reading (length "+line.length()+"):\t"+line.substring(0,readlength));
							}
							if(capitaliseInput){
								rawInput.add(line.toUpperCase());
							}else{
								rawInput.add(line);
							}
					//		lines++;
						} // LINE SEPARATORS REMOVED, 30/09/2011
						lines++;
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				finally{
					inputBuffer.close();
				}
			}else{
				System.out.println("SERIOUS: unable to open this file.");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		if(reportBufferStatus){
			System.out.println("BasicFileReader read "+lines+" lines.");
		}
		return rawInput;
	}
}
