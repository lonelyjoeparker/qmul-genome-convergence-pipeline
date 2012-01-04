package uk.ac.qmul.sbcs.evolution.sandbox;

public class TestClass {

	/**
	 * @param args - some args
	 *
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String userdir = System.getProperty("user.dir");
		String user = System.getProperty("user");
		foo(new Integer(args[0]));
	}
	
	public static void foo(int limit){
		String line = "poo";
		for(int i=0; i<limit;i++){
			line = "poo "+i;
			System.out.println(line);
		}
	}
}
