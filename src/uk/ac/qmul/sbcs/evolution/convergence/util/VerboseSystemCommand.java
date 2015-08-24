package uk.ac.qmul.sbcs.evolution.convergence.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Wrapper class for executing risky system commands via perl wrapper script (runCmd.pl) and capturing stdout stream.
 * @author <a href="mailto:joe@kitson-consulting.co.uk">Joe Parker, Kitson Consulting / Queen Mary University of London</a>
 *
 */
public class VerboseSystemCommand {
	public final String exeString;
	public StringBuffer output;
	
	public VerboseSystemCommand(String exeString){
		this.exeString = exeString;
		this.output = new StringBuffer();
		System.out.println("\n\nattempting command "+exeString);
		try {
			Process p = Runtime.getRuntime().exec(exeString);
			BufferedReader iReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = iReader.readLine();
			int i = 0;
			while(line != null){
				line = line + "\n";
				System.out.print("o"+i+" "+line);
				output.append(line);
				line = iReader.readLine();
				i++;
			}
			System.out.println("done with output\nerror:");
			BufferedReader eReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = eReader.readLine();
			int e = 0;
			while(line != null){
				System.out.println("e"+e+" "+line);
				line = eReader.readLine();
				e++;
			}
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public StringBuffer getiReader() {
		return output;
	}
}
