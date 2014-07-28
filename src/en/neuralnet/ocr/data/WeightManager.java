package en.neuralnet.ocr.data;

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
	 *  Layer_id:Neuron_id:Bias:Weight_1,Weight_2,...,Weight_n
	 */
	
	private static final String SEP_MAJOR = ":";			// Character separating the parts of each line (character, bias, list of weights)
	private static final String SEP_MINOR = ",";			// Character separating each weight in list of weights
	private static final String FILE_PATH = "weights.txt";	// Name of file containing weight archive
	//private static final double INITIAL_WEIGHT = 0.0;       // initial weight of a new neuron
	
	private final Map<Integer,Map<Integer,double[]>> weightMap = new HashMap<Integer,Map<Integer,double[]>>();	// Maps name of output neuron to array containing its input weights
	private final Map<Integer,Map<Integer,Double>>   biasMap   = new HashMap<Integer,Map<Integer,Double>>();    // Maps name of output neuron to bias
	
	/**
	 * Construct the weight manager.
	 * 
	 * @param imgSize The number of pixels in the input images.
	 * @param hiddenLayerSizes An array containing the sizes of the hidden layers. For example, two hidden layers of size 100 and 300 would be represented by {100,300}.
	 * @param characters The characters we are testing for. One output neuron will be set up for each character.
	 */
	public WeightManager(int imgSize, int[] hiddenLayerSizes, char[] characters) {
		File weightFile = new File(FILE_PATH);
		
		// If no weight archive exists, initialize all edge weights and biases to 
		if(!weightFile.exists()) {
			Random rand = new Random();
			int lastLayerSize = imgSize;
			// for each hidden layer
			for(int i=0; i<hiddenLayerSizes.length; i++) {
				Map<Integer,double[]> layerWeightMap = new HashMap<Integer,double[]>();
				Map<Integer,Double>   layerBiasMap   = new HashMap<Integer,Double>();
				// for each neuron in this hidden layer
				for(int j=0; j<hiddenLayerSizes[i]; j++) {
					double[] weights = new double[lastLayerSize];
					// for each weight in this neuron
					for(int k=0; k<weights.length; k++) {
						weights[k] = rand.nextDouble() * 0.02 - 0.01;
					}
					layerWeightMap.put(j, weights);
					layerBiasMap.put(j, rand.nextDouble() * 0.02 - 0.01);
				}
				weightMap.put(i, layerWeightMap);
				biasMap.put(i, layerBiasMap);
				lastLayerSize = hiddenLayerSizes[i];
			}
			
			Map<Integer,double[]> outputWeightMap = new HashMap<Integer,double[]>();
			Map<Integer,Double>   outputBiasMap   = new HashMap<Integer,Double>();
			// for each output neuron
			for(char c : characters) {
				double[] weights = new double[lastLayerSize];
				// for each weight in each output neuron
				for(int i=0; i<weights.length; i++) {
					weights[i] = rand.nextDouble() * 0.02 - 0.01;
				}
				outputWeightMap.put(Character.getNumericValue(c), weights);
				outputBiasMap.put(Character.getNumericValue(c), rand.nextDouble() * 0.02 - 0.01);
			}
			weightMap.put(hiddenLayerSizes.length, outputWeightMap);
			biasMap.put(hiddenLayerSizes.length, outputBiasMap);
			
			return;
		}
		
		// Read in text file containing weights
		try {
			BufferedReader br = new BufferedReader(new FileReader(weightFile));
			String line;
			while((line = br.readLine()) != null) {
				String[] majorSplit = line.trim().split(SEP_MAJOR); // Split each line into parts separated by the SEP_MAJOR character
				if(majorSplit.length != 4 || majorSplit[0].length() != 1) continue;
				
				int layerID  = Integer.parseInt(majorSplit[0]); // read layer id
				int neuronID = Integer.parseInt(majorSplit[1]); // Read in neuron id
				
				Double bias = Double.valueOf(majorSplit[2]); // Read in bias
				
				String[] minorSplit = majorSplit[3].split(SEP_MINOR);
				
				// Iteratively read each weight into an array
				double[] weights = new double[minorSplit.length];
				for(int i=0; i<weights.length; i++) {
					weights[i] = Double.parseDouble(minorSplit[i]);
				}
				
				if(!weightMap.containsKey(layerID)) weightMap.put(layerID, new HashMap<Integer,double[]>());
				if(!biasMap.containsKey(layerID)) biasMap.put(layerID, new HashMap<Integer,Double>());
				
				weightMap.get(layerID).put(neuronID, weights);
				biasMap.get(layerID).put(neuronID, bias);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Getters and setters
	
	public double[] getWeights(int layerID, int neuronID) {
		return weightMap.get(layerID).get(neuronID);
	}
	
	public void setWeights(int layerID, int neuronID, double[] weights) {
		weightMap.get(layerID).put(neuronID, weights);
	}
	
	public double getBias(int layerID, int neuronID) {
		return biasMap.get(layerID).get(neuronID);
	}
	
	public void setBias(int layerID, int neuronID, double bias) {
		biasMap.get(layerID).put(neuronID, bias);
	}
	
	
	// Save weights and biases currently stored in weightMap and biasMap to the archive file, overwriting previous contents
	public void save() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
			for(Entry<Integer,Map<Integer,double[]>> layer : weightMap.entrySet()) {
				int layerID = layer.getKey();
				for(Entry<Integer,double[]> neuron : layer.getValue().entrySet()) {
					bw.write(layerID + SEP_MAJOR);
					int neuronID = neuron.getKey();
					bw.write(neuronID + SEP_MAJOR);	// Write name of neuron followed by SEP_MAJOR character
					bw.write(biasMap.get(layerID).get(neuronID) + SEP_MAJOR);	// Write bias of neuron followed by SEP_MAJOR character
					// Iteratively write each weight followed by SEP_MINOR character
					for(double d : neuron.getValue()) {
						bw.write(d + SEP_MINOR);
					}
					bw.newLine();
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
