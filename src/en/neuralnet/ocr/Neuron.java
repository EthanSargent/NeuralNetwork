package en.neuralnet.ocr;

/**
 *	Class containing the methods and data members associated with a single neuron in the network
 *	NB: Does not contain the sigmoid function 
 *
 */

public class Neuron {
	private final int id;
	private double[] inputs;
	private double[] inputWeights; // Weights for the inputs to this neuron
	private double bias; // This neuron's bias weight
	private double weightedSum;
	private double delta;
	
	public Neuron(int id) {
		this.id = id;
	}
	
	// Compute the output of this neuron given the inputs from the previous layer
	public double getOutput(double[] inputs) {
		this.inputs = inputs;
		double weightedSum = bias; // Output includes bias weight
		assert inputWeights.length == inputs.length; // no.of weights must equal no.of inputs
		// Add the product of each input and its respective weight
		for (int i = 0; i < inputWeights.length; i++) {
			weightedSum += inputWeights[i] * inputs[i];
		}
		this.weightedSum = weightedSum;
		return weightedSum;
	}
	
	// Getters and setters
	
	public double[] getWeights() {
		return inputWeights;
	}
	
	public void setWeights(double[] inputWeights) {
		this.inputWeights = inputWeights;
	}
	
	public void setBias(double bias) {
		this.bias = bias;
	}
	
	public double getBias() {
		return bias;
	}
	
	public double getWeightedSum() {
		return weightedSum;
	}
	
	public double getDelta() {
		return delta;
	}
	
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	public double[] getInputs() {
		return inputs;
	}
	
	public int getID() {
		return id;
	}
}
