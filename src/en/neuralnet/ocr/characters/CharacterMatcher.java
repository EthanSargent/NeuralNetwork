package en.neuralnet.ocr.characters;

import en.neuralnet.ocr.data.DataManager;
import en.neuralnet.ocr.data.DataNotFoundException;

public class CharacterMatcher {
	private final DataManager dm;
	
	public CharacterMatcher(DataManager dm) {
		this.dm = dm;
	}
	
	/**
	 * Calculates the probability that a given factor value indicates that the mystery character is the given character.
	 * 
	 * @param factor The name of the factor being tested.
	 * @param factorValue The value of the factor being tested.
	 * @param character The potential character being matched.
	 * @return The match probability.
	 */
	public float match(String factor, float factorValue, char character) {
		try {
			//System.out.printf("%s = %f for %c%n", factor, dm.getAvgValue(factor, character), character);
			//System.out.printf("Diff for %c is %f%n", character, 1.0f - Math.abs(dm.getAvgValue(factor, character) - factorValue));
			return 1.0f - Math.abs(dm.getAvgValue(factor, character) - factorValue);
		} catch (DataNotFoundException e) {
			e.printStackTrace();
		}
		return 0.0f;
	}
}
