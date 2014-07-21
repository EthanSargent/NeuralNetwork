package en.neuralnet.ocr.factors;

public class Creases implements Factor {
	public double tolerance;
	
	@Override
	public float calculate( float[][] img ) {
		//returns the number of creases
			//if number has a curve, return 0
		while (tolerance < 1) {
			
		}
		return 10.6f;
	}
	
	@Override
	public String getName() {
		return "Creases";
	}
}

