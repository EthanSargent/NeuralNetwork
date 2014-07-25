package en.neuralnet.ocr;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class NeuralNetwork {
	//List of possible outputs
	private static final char[] CHARS = "0123456789".toCharArray();
	
	//Number of loops of training to be completed; 0 if in test mode
	private static final int TRAINING_LOOPS = 5;
	
	//Final Learning Speed
	private static final double ETA = 0.2;
	
	//Variable learning speed
	private static double eta = ETA;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		int[] labels = ImageManager.getLabels();
		ImageData imageData = ImageManager.getImages();
		double[][] images = imageData.getImages();
		int imgSize = imageData.getNumCols() * imageData.getNumRows();

		if(labels.length != images.length) throw new IllegalArgumentException("Number of labels is not equal to number of images.");

		System.out.println("retrieved image data");

		WeightManager weightManager = new WeightManager(imgSize, CHARS);

		Neuron[] neurons = new Neuron[CHARS.length];
		for(int j=0; j<neurons.length; j++) {
			neurons[j] = new Neuron();
			neurons[j].setWeights(weightManager.getWeights(CHARS[j]));
			neurons[j].setBias(weightManager.getBias(CHARS[j]));
		}

		System.out.println("initialized neurons");

		int[] results = new int[CHARS.length];
		int loops = TRAINING_LOOPS > 0 ? TRAINING_LOOPS : 1;

		for(int a=0; a<loops; a++) {
			System.out.println("a = " + a);
			for(int i=0; i<images.length; i++) {
				// Propagate the inputs forward to compute the outputs
				double[] weightedSums = new double[neurons.length];
				double[] outputs = new double[neurons.length];
				for(int j=0; j<weightedSums.length; j++) {
					weightedSums[j] = neurons[j].getOutput(images[i]);
					outputs[j] = sigmoidFunction(weightedSums[j]);
				}

				if(TRAINING_LOOPS > 0) {

					double[] deltas = new double[neurons.length];
					for(int j=0; j<deltas.length; j++) {
						deltas[j] = sigmoidPrime(weightedSums[j]) * ((labels[i] == j ? 1.0 : 0.0) - outputs[j]);
					}

					for(int j=0; j<neurons.length; j++) {
						double[] weights = neurons[j].getWeights();
						double bias = neurons[j].getBias();
						for(int k=0; k<weights.length; k++) {
							weights[k] += eta * images[i][k] * deltas[j];
							bias += eta * images[i][k] * deltas[j];
							//System.out.printf("neuron %d weight %d set to %f%n", j, k, weights[k]);
						}
						neurons[j].setWeights(weights);
						neurons[j].setBias(bias);
						char c = Character.forDigit(j, 10);
						weightManager.setWeights(c, weights);
						weightManager.setBias(c, bias);
					}

				} else {
					
					SortedMap<Double, Integer> solutions = new TreeMap<Double, Integer>();
					for (int j = 0; j < CHARS.length; j ++) {
						solutions.put(outputs[j],j);
					}
					System.out.println("ANSWER: " + labels[i]);
					Set<Entry<Double, Integer>> solSet = solutions.entrySet();
					int j = 0;
					for (Entry<Double, Integer> e : solSet) {
						if(j >= 5) System.out.println("\tGuess " + (results.length - j) + ": " + e.getValue());
						if(e.getValue() == labels[i]) results[results.length - j - 1]++;
						j++;
					}
				}
			}
			//updateLearningRate((double) a, (double) loops);
		}

		if(TRAINING_LOOPS > 0) {
			System.out.println("saving weights");
			weightManager.save();
		} else {
			for (int i = 0; i < 10; i ++ ) {
				System.out.printf("%f%% guessed as %d%n", ((double) results[i]) / images.length * 100, i + 1);
			}
		}
	}

	private static double sigmoidPrime(double x) {
		double temp = sigmoidFunction(x);
		return temp * (1 - temp);
	}

	public static double sigmoidFunction(double in) {
		return (1/(1 + Math.exp(-in)));
	}
	//Updates the learning rate as a function of the training progress; slowly anneals the learning rate 
	public static void updateLearningRate(double loopCount, double totalLoops) {
		eta = ETA / (1 + (loopCount+1)/totalLoops);
	}
}
