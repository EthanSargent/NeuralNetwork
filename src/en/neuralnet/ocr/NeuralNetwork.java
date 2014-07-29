package en.neuralnet.ocr;

import java.util.Arrays;
import java.util.PriorityQueue;

import en.neuralnet.ocr.data.WeightManager;
/*
 * The NeuralNetwork main class retrieves the grayscale value maps for
 * each input image from the ImageManager class.
 * 
 * It initializes the neurons that each accept the map as their input; the neurons are 
 * trained to recognize configurations that most closely resemble their designated
 * output character through a backpropagation learning algorithm.
 * 
 * Authors: Greg Carlin, Pratap Singh, and Ethan Sargent
 * 
 */
public class NeuralNetwork {
	//Array of all possible outputs (guesses of the neural network)
	private static final char[] CHARS = "0123456789".toCharArray();
	
	//Final Learning Speed
	private static final double ETA = 0.2;
	
	// defines the hidden layers of the network. each number is the size of a hidden layer.
	private static final int[] HIDDEN_LAYERS = {300,200};
	
	// the side of the image used
	public static final int IMAGE_SIDE = 28;
	
	// the length of the side of a square that can contain the entire contents of the character in the image
	public static final int IMAGE_SUB_SIDE = 20;
	
	// the number of pixels in the image
	public static final int IMAGE_SIZE = IMAGE_SIDE * IMAGE_SIDE;
	
	private final WeightManager weightManager;
	private final Neuron[][] neurons = new Neuron[HIDDEN_LAYERS.length + 1][];
	
	public NeuralNetwork() {
		this.weightManager = new WeightManager(IMAGE_SIZE, HIDDEN_LAYERS, CHARS);
		
		for(int j=0; j<neurons.length; j++) {
			// add neurons to this layer. if this is the output layer, number of neurons is taken from CHARS constant.
			neurons[j] = new Neuron[j < HIDDEN_LAYERS.length ? HIDDEN_LAYERS[j] : CHARS.length];
			for(int k=0; k<neurons[j].length; k++) {
				neurons[j][k] = new Neuron(j < HIDDEN_LAYERS.length ? k : Character.getNumericValue(CHARS[k]));
				int neuronID = neurons[j][k].getID();
				neurons[j][k].setWeights(weightManager.getWeights(j, neuronID));
				neurons[j][k].setBias(weightManager.getBias(j, neuronID));
			}
		}
	}
	
	/**
	 * Propagates the input image through the network.
	 * 
	 * @param input The initial input image.
	 * @return The outputs of the last layer of the network.
	 */
	private double[] forwardPropagate(double[] input) {
		//System.out.println("forward propagating");
		// for each layer i in the network
		double[] lastOutput = input;
		for(int i=0; i<neurons.length; i++) {
			//System.out.printf("propagating layer with %d nodes. lastOutput is size %d.%n", neurons[i].length, lastOutput.length);
			// for each neuron j in the layer
			double[] thisOutput = new double[neurons[i].length];
			for(int j=0; j<neurons[i].length; j++) {
				double weightedSum = neurons[i][j].getOutput(lastOutput);
				thisOutput[j] = sigmoidFunction(weightedSum);
			}
			lastOutput = thisOutput;
		}
		//System.out.println();
		return lastOutput;
	}
	
	public void train(double[] image, char answer) {
		double[] outputs = forwardPropagate(image);
		
		Neuron[] outputNeurons = neurons[neurons.length - 1];
		//System.out.printf("there are %d output neurons%n", outputNeurons.length);
		// for each neuron in the output layer
		for(int i=0; i<outputNeurons.length; i++) {
			Neuron neuron = outputNeurons[i];
			neuron.setDelta(sigmoidPrime(neuron.getWeightedSum()) * ((Character.forDigit(neuron.getID(), 10) == answer ? 1.0 : 0.0) - outputs[i]));
			//System.out.printf("setting delta of output neuron %c with id %d to %f (answer = %s)%n", Character.forDigit(neuron.getID(), 10), neuron.getID(), neuron.getDelta(), Character.forDigit(neuron.getID(), 10) == answer ? "yes" : "no");
			//System.out.printf("activation = %f, outputs[%d] = %f%n", sigmoidPrime(neuron.getWeightedSum()), i, outputs[i]);
		}
		
		// going backwards, for each layer except the output layer
		for(int i=neurons.length-2; i>=0; i--) {
			//System.out.println("backing hidden layer " + i);
			// for each neuron in this layer
			for(int j=0; j<neurons[i].length; j++) {
				double weightDeltaSum = 0.0;
				// for each neuron in the layer after
				for(int k=0; k<neurons[i+1].length; k++) {
					weightDeltaSum += neurons[i+1][k].getDelta() * neurons[i+1][k].getWeights()[j];
				}
				neurons[i][j].setDelta(sigmoidPrime(neurons[i][j].getWeightedSum()) * weightDeltaSum);
			}
		}
		
		// for each layer in the network
		for(int i=0; i<neurons.length; i++) {
			// for each neuron in the layer
			for(int j=0; j<neurons[i].length; j++) {
				
				// update weights
				double[] weights = neurons[i][j].getWeights();
				// for each weight in the neuron
				for(int k=0; k<weights.length; k++) {
					weights[k] += ETA * neurons[i][j].getInputs()[k] * neurons[i][j].getDelta();
					//System.out.println(ETA * sigmoidFunction(neurons[i][j].getInputs()[k]) * neurons[i][j].getDelta());
				}
				neurons[i][j].setWeights(weights);
				weightManager.setWeights(i, neurons[i][j].getID(), weights);
				
				// update bias
				double bias = neurons[i][j].getBias();
				bias += ETA * 1.0 * neurons[i][j].getDelta();
				neurons[i][j].setBias(bias);
				weightManager.setBias(i, neurons[i][j].getID(), bias);
				
			}
		}
	}
	
	public void saveWeights() {
		weightManager.save();
	}
	
	private static class Guess implements Comparable<Guess> {
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
	
	public char guess(double[] image) {
		double[] outputs = forwardPropagate(image);
		System.out.println("outputs: " + Arrays.toString(outputs));
		
		PriorityQueue<Guess> queue = new PriorityQueue<Guess>();
		for(int i=0; i<outputs.length; i++) {
			queue.add(new Guess(CHARS[i], outputs[i]));
		}
		
		char answer = queue.peek().getCharacter();
		
		Guess g;
		int i = 0;
		while((g = queue.poll()) != null) {
			System.out.printf("\tGuess %d: %c (%f)%n", i, g.getCharacter(), g.getValue());
			i++;
			if(i > 4) break;
		}
		
		return answer;
	}

	private static double sigmoidPrime(double x) {
		double temp = sigmoidFunction(x);
		return temp * (1 - temp);
	}
	
	//The sigmoid function converts weighted sums into neuron outputs
	public static double sigmoidFunction(double in) {
		return (1/(1 + Math.exp(-in)));
	}
}
