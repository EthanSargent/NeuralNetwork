package en.neuralnet.ocr.factors;

public interface Factor {
	/**
	 * Calculates the value of this factor for the given image (represented by a 2D array of grayscale values 0-1)
	 * 
	 * @param img
	 * @return
	 */
	public float calculate(float[][] img);
	
	/**
	 * The name of the factor (for reference in files and such).
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * The weight of the factor.
	 * 
	 * @return A value between 0 and 1 (inclusive). All weights should add up to 1.
	 */
	public float getWeight();
}
