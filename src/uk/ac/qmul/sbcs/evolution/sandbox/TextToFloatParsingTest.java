package uk.ac.qmul.sbcs.evolution.sandbox;

/**
 * Class to test the Float.parseFloat() method performance on text data
 * <br/>In particular odd strings which should be equal, e.g.
 * <ul><li>"0.01"</li><li>"00.01"</li><li>" 0.01" (note space)</li><li>"0.0100"</li></ul>
 * <p>NB uses assertions to test - run JVM with '-ea' argument. The first three tests should pass in the orthodox manner. The fourth should throw assertion errors to pass. 
 * @author joeparker
 *
 */
public class TextToFloatParsingTest {

	/**
	 * Default no-arg constructor
	 */
	public TextToFloatParsingTest(){
		/* Set up the floats as strings*/
		String[] floatsToConvert = {"0.01","00.01"," 0.01","0.0100"};
		Float[] floatObjects = new Float[4];
		float[] floatPrimitives = new float[4];

		/* Convert the floats, first to Float objects and also cast to float primitives */
		for(int i=0;i<4;i++){
			floatObjects[i] = Float.parseFloat(floatsToConvert[i]);
			floatPrimitives[i] = floatObjects[i];
		}

		/* Are they all equal? They should be: test this. Should PASS */
		/* Iterate through the triangle */
		System.out.println("Testing conversions: test 1/4 (should pass)...");
		for(int i=0;i<4;i++){
			for(int j=1;j<4;j++){
				assert(floatPrimitives[i] == floatPrimitives[j]);
				assert(floatObjects[i] == floatPrimitives[j]);
			}
		}
		System.out.println("Test 1/4 passed OK");

		/* Test the numerical equivalent */
		System.out.println("Testing conversions: test 2/4 (should pass)...");
		for(int i=0;i<4;i++){
			assert(floatPrimitives[i] == 0.01f);
		}
		System.out.println("Test 2/4 passed OK");

		/* Test the numerical equivalent inequality. Should PASS */
		System.out.println("Testing conversions: test 3/4 (should pass)...");
		for(int i=0;i<4;i++){
			assert(floatPrimitives[i] != 0.02f);
		}
		System.out.println("Test 3/4 passed OK");

		/* Test the inversion */
		/* These assertions should FAIL*/
		System.out.println("Testing conversions: test 4/4 (should fail with java.lang.AssertionError)...");
		boolean test_4_pass_flag = false;
		try{
			for(int i=0;i<4;i++){
				for(int j=1;j<4;j++){
					assert(floatPrimitives[i] != floatPrimitives[j]);
					assert(floatObjects[i] != floatPrimitives[j]);
					test_4_pass_flag = true;	// If AssertionErrors are thrown as we expect they will be, this is never reached.
				}
			}
		}finally{
			// test_4_pass_flag should never be set true (line 62) if AssertionErrors have been thrown correctly.
			if(test_4_pass_flag){
				System.err.println("Test 3/4 passed! This constitutes a logical FAILURE");
			}else{
				System.out.println("Test 4/4 passed OK (expected assertion errors occured as planned.");
			}
		}	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TextToFloatParsingTest();
	}

}
