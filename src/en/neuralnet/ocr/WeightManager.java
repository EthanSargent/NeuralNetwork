package en.neuralnet.ocr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * 	Class containing methods and data members for managing weights and biases in the neural network.
 * 	Operations are:
 * 		- Reading in an archive file of weights, or randomly initializing all weights if no archive found
 * 		- Storing and updating weights during runtime of the network
 * 		- Saving all weights to an archive file 21
 */
public class WeightManager {
	
	/* 	WEIGHT ARCHIVE FILE FORMAT 
	 * 	One line for each output neuron:
	 * 	Character of output neuron:Bias:Weight1,Weight2,...
	 */
	
	private static final String SEP_MAJOR = ":";			// Character separating the parts of each line (character, bias, list of weights)
	private static final String SEP_MINOR = ",";			// Character separating each weight in list of weights
	private static final String FILE_PATH = "weights.txt";	// Name of file containing weight archive
	
	private final Map<Character,double[]> weightMap = new HashMap<Character,double[]>();	// Maps name of output neuron to array containing its input weights
	private final Map<Character,Double>   biasMap   = new HashMap<Character,Double>();		// Maps name of output neuron to bias
	
	public WeightManager(int imgSize, char[] characters) {
		File weightFile = new File(FILE_PATH);
		
		// If no weight archive exists, randomly initialize all edge weights and biases to doubles in the range [-1,1]
		if(!weightFile.exists()) {
			Random rand = new Random();
			for(char c : characters) {
				double[] weights = new double[imgSize];
				for(int i=0; i<weights.length; i++) {
					weights[i] = rand.nextDouble() * 2 - 1;
				}
				weightMap.put(c, weights);
				biasMap.put(c, rand.nextDouble() * 2 - 1);
			}
			return;
		}
		
		// Read in text file containing weights
		try {
			BufferedReader br = new BufferedReader(new FileReader(weightFile));
			String line;
			while((line = br.readLine()) != null) {
				String[] majorSplit = line.trim().split(SEP_MAJOR); // Split each line into parts separated by the SEP_MAJOR character
				if(majorSplit.length != 3 || majorSplit[0].length() != 1) continue;
				char character = majorSplit[0].charAt(0); // Read in neuron name
				
				Double bias = Double.valueOf(majorSplit[1]); // Read in bias
				
				String[] minorSplit = majorSplit[2].split(SEP_MINOR);
				if(minorSplit.length != imgSize) continue; // There must be the same number of weights as inputs to each neuron
				
				// Iteratively read each weight into an array
				double[] weights = new double[imgSize];
				for(int i=0; i<weights.length; i++) {
					weights[i] = Double.parseDouble(minorSplit[i]);
				}
				
				weightMap.put(character, weights); 	// Store weights read in
				biasMap.put(character, bias);		// Store bias read in
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Getters and setters
	
	public double[] getWeights(char character) {
		return weightMap.get(character);
	}
	
	public void setWeights(char character, double[] weights) {
		weightMap.put(character, weights);
	}
	
	public double getBias(char character) {
		return biasMap.get(character);
	}
	
	public void setBias(char character, double bias) {
		biasMap.put(character, bias);
	}
	
	
	// Save weights and biases currently stored in weightMap and biasMap to the archive file, overwriting previous contents
	public void save() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
			for(Entry<Character,double[]> e : weightMap.entrySet()) {
				Character key = e.getKey();
				bw.write(key + SEP_MAJOR);	// Write name of neuron followed by SEP_MAJOR character
				bw.write(biasMap.get(key) + SEP_MAJOR);	// Write bias of neuron followed by SEP_MAJOR character
				// Iteratively write each weight followed by SEP_MINOR character
				for(double d : e.getValue()) {
					bw.write(d + SEP_MINOR);
				}
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
