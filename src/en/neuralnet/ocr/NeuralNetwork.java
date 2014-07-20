package en.neuralnet.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import en.neuralnet.ocr.characters.CharacterMatcher;
import en.neuralnet.ocr.factors.Factor;

public class NeuralNetwork {
	private static final String   characters = "abcdefghijklmnopqrstuzwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final Factor[] factors    = {}; // TODO fill with factors
	
	public static void main(String[] args) {
		if(args.length < 1) throw new IllegalArgumentException("Must specify image path.");
		
		try {
			BufferedImage bi = ImageIO.read(new File(args[0]));
			int w = bi.getWidth();
			int h = bi.getHeight();
			int[] rgbs = bi.getRGB(0, 0, w, h, null, 0, w);
			float[][] grayscale = new float[h][w];
			for(int i=0; i<grayscale.length; i++) {
				for(int j=0; j<grayscale[i].length; j++) {
					Color c = new Color(rgbs[i * w + j]);
					// average integer based rgbs and divide by 255 to get float-based grayscale approximation
					grayscale[i][j] = (c.getRed() + c.getGreen() + c.getBlue()) / 765.0f;
				}
			}
			
			// maps potential characters to the probability that the mystery character matches that potential character
			HashMap<Character,Float> probabilities = new HashMap<Character,Float>();
			for(char character : characters.toCharArray()) {
				probabilities.put(character, 0.0f);
			}
			
			for(Factor f : factors) {
				float val = f.calculate(grayscale);
				for(Entry<Character,Float> e : probabilities.entrySet()) {
					char key = e.getKey();
					float match = CharacterMatcher.match(f.getName(), val, key);
					probabilities.put(key, e.getValue() + match * f.getWeight());
				}
			}
			
			// TODO pick best match
		} catch (IOException e) {
			System.err.println("Error reading " + args[0]);
		}
	}
}
