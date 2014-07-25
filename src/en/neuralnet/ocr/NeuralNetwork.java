package en.neuralnet.ocr;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
	
	//Number of loops of training to be completed; 0 if in test mode
	private static final int TRAINING_LOOPS = 0;
	
	//Final Learning Speed
	private static final double ETA = 0.2;
	
	//Variable learning speed
	private static double eta = ETA;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		//Retrieving image labels (correct answers that backstop the training
		//process of the neural network)
		int[] labels = ImageManager.getLabels();
		
		//Retrieves the map of grayscale values for each image in a 2d array
		//(Each image gets a row)
		ImageData imageData = ImageManager.getImages();
		double[][] images = imageData.getImages();
		int imgSize = imageData.getNumCols() * imageData.getNumRows();

		//Makes sure each image is assigned a correct answer label
		if(labels.length != images.length) throw new IllegalArgumentException("Number of labels is not equal to number of images.");
		
		//Progress marker for debugging purposes (did the image data retrieval succeed?)
		System.out.println("retrieved image data");
		
		WeightManager weightManager = new WeightManager(imgSize, CHARS);

		//Initializes the output neurons, 1 for each character to be tested for,
		//as represented in the CHARS array above
		Neuron[] neurons = new Neuron[CHARS.length];
		for(int j=0; j<neurons.length; j++) {
			neurons[j] = new Neuron();
			neurons[j].setWeights(weightManager.getWeights(CHARS[j]));
			neurons[j].setBias(weightManager.getBias(CHARS[j]));
		}

		//Another progress marker for debugging purposes (did the neurons initialize properly?)
		System.out.println("initialized neurons");

		int[] results = new int[CHARS.length];
		
		//Determines whether the user is training the network or formally testing it's accuracy
		int loops = TRAINING_LOOPS > 0 ? TRAINING_LOOPS : 1;

		//This block will run the training algorithm as many times as specified by the TRAINING_LOOPS
		//Variable if in training mode (TRAINING_LOOPS > ); if TRAINING_LOOPS == 0, the method
		//will execute the formal testing subroutine of the block which displays an overall accuracy figure.
		for(int a=0; a<loops; a++) {
			System.out.println("a = " + a);
			for(int i=0; i<images.length; i++) {
				//Propagate the inputs forward to compute the outputs
				double[] weightedSums = new double[neurons.length];
				double[] outputs = new double[neurons.length];
				for(int j=0; j<weightedSums.length; j++) {
					//Performs the core neural network calculation
					//Of computing the output of each neuron based on
					//the relative weight of each pixel
					weightedSums[j] = neurons[j].getOutput(images[i]);
					outputs[j] = sigmoidFunction(weightedSums[j]);
				}

				//The Backpropagation Algorithm adjusts the weights of each output neuron's input pixels
				//based on the results of a forward propagation of a single image
				if(TRAINING_LOOPS > 0) {

					//The error measures (deltas) are calculated first
					//Delta_j = Error_j * SigmoidPrime(weighted neuron sum), where j is the output
					//neuron index
					double[] deltas = new double[neurons.length];
					for(int j=0; j<deltas.length; j++) {
						deltas[j] = sigmoidPrime(weightedSums[j]) * ((labels[i] == j ? 1.0 : 0.0) - outputs[j]);
					}

					//Update rule for weights: w_i,j <- w_i,j + eta * activation of neuron i * delta_j
					//Iteratively goes through all weights in the network, updating according to the
					//above rule
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
						
						//Storing updated weights in the WeightManager
						weightManager.setWeights(c, weights);
						weightManager.setBias(c, bias);
					}

				} else {
					//Prints out the actual correct image label, followed by the network's
					//top 5 guesses.
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
			//Saves updated weights to archived text file ("weights.txt")
			System.out.println("saving weights");
			weightManager.save();
		} else {
			for (int i = 0; i < 10; i ++) {
				//Prints out the percentage of correct answers ranked at each rank
				System.out.printf("%f3%% guessed as %d%n", ((double) results[i]) / images.length * 100, i + 1);
			}
		}
	}

	private static double sigmoidPrime(double x) {
		double temp = sigmoidFunction(x);
		return temp * (1 - temp);
	}
	//The sigmoid function converts weighted sums into neuron outputs
	public static double sigmoidFunction(double in) {
		return (1/(1 + Math.exp(-in)));
	}
	//Updates the learning rate as a function of the training progress; slowly anneals the learning rate 
	public static void updateLearningRate(double loopCount, double totalLoops) {
		eta = ETA / (1 + (loopCount+1)/totalLoops);
	}
}
