package en.neuralnet.ocr.factors;

/**
 * Calculates the percent volume that the image is drawn on.
 * 
 * @author Greg Carlin
 *
 */
public class Volume implements Factor {
	private static final float THRESHOLD = 0.5f; // pixels with values less than this are considered "drawn on"

	@Override
	public float calculate(float[][] img) {
		int filled = 0;
		for(int i=0; i<img.length; i++) {
			for(int j=0; j<img[i].length; j++) {
				if(img[i][j] < THRESHOLD) filled++;
			}
		}
		return (float) filled / (float) (img.length * img[0].length);
	}

	@Override
	public String getName() {
		return "Volume";
	}
}
