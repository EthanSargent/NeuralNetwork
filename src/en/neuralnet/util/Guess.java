package en.neuralnet.util;

/**
 *	Class containing a guess - a character and the output from the network for that character
 *
 */

public class Guess implements Comparable<Guess> {
	private final char character;
	private final double value;
	
	public Guess(char character, double value) {
		this.character = character;
		this.value = value;
	}

	public char getCharacter() {
		return character;
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public int compareTo(Guess arg0) {
		return new Double(arg0.getValue()).compareTo(getValue());
	}
}
