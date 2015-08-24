package uk.ac.qmul.sbcs.evolution.sandbox;

public class TestClass {

	double[] vals;
	
	/**
	 * @param args - some args
	 *
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String userdir = System.getProperty("user.dir");
		String user = System.getProperty("user");
		TestClass c = new TestClass();
		c.foo(new Integer(args[0]));
		double mean = c.getMean();
		System.out.println(mean);
	}
	
	public void foo(int limit){
		this.vals = new double[limit];
		String line = "poo";
		for(int i=0; i<limit;i++){
			line = "poo "+i;
			System.out.println(line);
			if(i<5){
				vals[i] = i;
			}else{
				vals[i] = Double.NaN;
			}
		}
	}
	
	public double getMean(){
		double sum = 0.0d;
		int count = 0;
		for(int i=0;i<vals.length;i++){
			if(!(new Double(vals[i]).isNaN())){
				sum += vals[i];
				count ++;
			}
		}
		sum = (sum / (new Double(count)));
		return sum;
	}
	
	public void testCommit(){
		// A dummy method to test git commits
	}
}
