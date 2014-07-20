package en.neuralnet.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import en.neuralnet.ocr.factors.Factor;

public class NeuralNetwork {
	public static void main(String[] args) {
		if(args.length < 1) throw new IllegalArgumentException("Must specify image path.");
		
		BufferedImage bi = ImageIO.read(new File(args[0]));
		
		
		// maps potential characters to the probability that the mystery character matches that potential character
		HashMap<Character,Float> probabilities = new HashMap<Character,Float>();
		Factor[] factors = {}; // TODO fill with factors
		for(Factor f : factors) {
			float val = f.calculate(img);
		}
	}
}
