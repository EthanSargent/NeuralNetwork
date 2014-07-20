package en.neuralnet.ocr.data;

/**
 * Thrown when data is not found when it should be.
 * 
 * @author Greg Carlin
 *
 */
public class DataNotFoundException extends Exception {
	private static final long serialVersionUID = -8823018389303060103L;
	
	public DataNotFoundException() {
		super("Error accessing data files!");
	}
	
	public DataNotFoundException(String factorName) {
		super("Could not find data for factor " + factorName);
	}
	
	public DataNotFoundException(String factorName, char c) {
		super("Could not find data for factor " + factorName + " and character " + c);
	}
	
	public DataNotFoundException(String fileName, int line) {
		super("Error parsing line " + line + " of " + fileName);
	}
}
