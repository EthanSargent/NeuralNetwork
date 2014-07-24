package en.neuralnet.ocr;

public class Neuron {
	
	private double[] inputWeights;
	private double bias;
	
	/**
	 * Compute the output of this neuron given the inputs from the previous layer
	 * 
	 * @param inputs List of inputs
	 * @return
	 */
	public double getOutput(double[] inputs) {
		double weightedSum = bias;
		assert inputWeights.length == inputs.length;
		for (int i = 0; i < inputWeights.length; i++) {
			weightedSum += inputWeights[i] * inputs[i];
		}
		return weightedSum;
	}
	
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
}
