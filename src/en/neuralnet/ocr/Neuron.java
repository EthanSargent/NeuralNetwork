package en.neuralnet.ocr;

public class Neuron {
	
	double[] inputWeights;
	double bias;
	
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
		return sigmoidFunction(weightedSum);
	}
	
	public void setWeights(double[] inputWeights) {
		this.inputWeights = inputWeights;
	}
	
	public void setBias(double bias) {
		this.bias = bias;
	}
	
	private static double sigmoidFunction(double in) {
		return (1/(1 + Math.exp(in)));
	}
	
}
