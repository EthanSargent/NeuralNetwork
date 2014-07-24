package en.neuralnet.ocr;

public class NeuralNetwork {
	private static final double ALPHA = 0.9;
	private static final char[] CHARS = "0123456789".toCharArray();
	private static final boolean TRAINING = false;
	
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
		
		int correct = 0;
		
		for(int i=0; i<images.length; i++) {
			// Propagate the inputs forward to compute the outputs
			double[] weightedSums = new double[neurons.length];
			double[] outputs = new double[neurons.length];
			for(int j=0; j<weightedSums.length; j++) {
				weightedSums[j] = neurons[j].getOutput(images[i]);
				outputs[j] = sigmoidFunction(weightedSums[j]);
			}
			
			if(TRAINING) {
			
				double[] deltas = new double[neurons.length];
				for(int j=0; j<deltas.length; j++) {
					deltas[j] = sigmoidPrime(weightedSums[j]) * ((labels[i] == j ? 1.0 : 0.0) - outputs[j]);
				}
				
				for(int j=0; j<neurons.length; j++) {
					double[] weights = neurons[j].getWeights();
					double bias = neurons[j].getBias();
					for(int k=0; k<weights.length; k++) {
						weights[k] += ALPHA * images[i][k] * deltas[j];
						bias += ALPHA * images[i][k] * deltas[j];
						//System.out.printf("neuron %d weight %d set to %f%n", j, k, weights[k]);
					}
					neurons[j].setWeights(weights);
					neurons[j].setBias(bias);
					char c = Character.forDigit(j, 10);
					weightManager.setWeights(c, weights);
					weightManager.setBias(c, bias);
				}
			
			} else {
				
				double max = -Double.MAX_VALUE;
				int maxIndex = -1;
				for(int j=0; j<outputs.length; j++) {
					if(outputs[j] > max) {
						max = outputs[j];
						maxIndex = j;
					}
				}
				assert maxIndex >= 0;
				if(maxIndex == labels[i]) correct++;
				
				System.out.println(" GUESS: " + maxIndex);
				System.out.println("ANSWER: " + labels[i]);
				System.out.println();
			}
		}
		
		if(TRAINING) {
			System.out.println("saving weights");
			weightManager.save();
		} else {
			System.out.printf("%d/%d correct.%n", correct, images.length);
			System.out.printf("That's %f%%%n", ((double) correct) / images.length * 100);
		}
	}
	
	private static double sigmoidPrime(double x) {
		double temp = sigmoidFunction(x);
		return temp * (1 - temp);
	}
	
	public static double sigmoidFunction(double in) {
		return (1/(1 + Math.exp(-in)));
	}
}
