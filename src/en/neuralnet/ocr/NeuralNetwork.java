package en.neuralnet.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import en.neuralnet.ocr.characters.CharacterMatcher;
import en.neuralnet.ocr.data.DataManager;
import en.neuralnet.ocr.data.DataNotFoundException;
import en.neuralnet.ocr.factors.*;

public class NeuralNetwork {
	protected static final String   CHARACTERS  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private   static final String   WORKING_DIR = "";
	private   static final Factor[] FACTORS     = {new Volume()}; // TODO fill with factors
	
	public static void read(String imgPath) {
		train(imgPath, ' ');
	}
	
	public static void train(String imgPath, char answer) {
		try {
			// set up data manager and read image
			DataManager dm = new DataManager(WORKING_DIR);
			CharacterMatcher cm = new CharacterMatcher(dm);
			BufferedImage bi = ImageIO.read(new File(imgPath));
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
			final HashMap<Character,Float> probabilities = new HashMap<Character,Float>();
			for(char character : CHARACTERS.toCharArray()) {
				probabilities.put(character, 0.0f);
			}
			
			// this is where the magic happens
			for(Factor f : FACTORS) {
				float val = f.calculate(grayscale);
				if(answer != ' ') System.out.println("Factor '" + f.getName() + "' for " + answer + " is " + val);
				if(answer != ' ') dm.setAvgValue(f.getName(), answer, val);
				for(Entry<Character,Float> e : probabilities.entrySet()) {
					char key = e.getKey();
					float match = cm.match(f.getName(), val, key);
					//System.out.printf("put %s => %f%n", key, e.getValue() + match * dm.getWeight(f.getName()));
					probabilities.put(key, e.getValue() + match * dm.getWeight(f.getName()));
				}
			}
			
			// find and print out the top 5 most likely matches
			System.out.println(answer == ' ' ? "Top 5 matches:" : ("Top 5 matches for " + answer + ":"));
			List<Character> keys = new ArrayList<Character>(probabilities.keySet());
			Collections.sort(keys, new Comparator<Character>() {
				@Override
				public int compare(Character o1, Character o2) {
					//return probabilities.get(o1).compareTo(probabilities.get(o2));
					return probabilities.get(o2).compareTo(probabilities.get(o1));
				}});
			assert keys.size() > 5;
			for(int i=0; i<5; i++) {
				char c = keys.get(i);
				System.out.printf("%d: %c (%f%%)%n", i+1, c, probabilities.get(c)*100);
			}
			
			dm.save();
		} catch (IOException e) {
			System.err.println("Error reading " + imgPath);
		} catch (DataNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
