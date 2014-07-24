package en.neuralnet.ocr;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class NeuralNetwork {
	
	private static final String DATA_FOLDER = "C:\\Users\\scamper\\My Documents\\Neural network\\data\\";
	private static final String IMAGE_FILE = "t10k-images-idx3-ubyte";
	private static final String LABEL_FILE = "t10k-labels-idx1-ubyte";
	private static final double ALPHA = 0.9;
	
	public static void main(String[] args) {
		try {
			DataInputStream labelInputStream = new DataInputStream(new FileInputStream(DATA_FOLDER + LABEL_FILE));
			labelInputStream.readInt(); // magic number
			int numLabels = labelInputStream.readInt();
			
			int[] labels = new int[numLabels];
			for(int i=0; i<labels.length; i++) {
				labels[i] = labelInputStream.readUnsignedByte();
			}
			labelInputStream.close();
			
			DataInputStream imgInputStream = new DataInputStream(new FileInputStream(DATA_FOLDER + IMAGE_FILE));
			imgInputStream.readInt(); // magic number
			int numImgs = imgInputStream.readInt();
			int numRows = imgInputStream.readInt();
			int numCols = imgInputStream.readInt();
			
			double[][] images = new double[numImgs][numRows * numCols];
			for(int i=0; i<images.length; i++) {
				for(int j=0; j<images[i].length; j++) {
					images[i][j] = imgInputStream.readUnsignedByte() / 255.0;
				}
			}
			imgInputStream.close();
			
			if(numLabels != numImgs) throw new IllegalArgumentException("Number of labels is not equal to number of images.");
			
			
			Random random = new Random();
			Neuron[] neurons = new Neuron[10];
			for(int j=0; j<neurons.length; j++) {
				neurons[j] = new Neuron();
				double[] weights = new double[numRows * numCols];
				for(int i=0; i<weights.length; i++) {
					weights[i] = random.nextDouble() * 2 - 1;
				}
				neurons[j].setWeights(weights);
				neurons[j].setBias(random.nextDouble() * 2 - 1);
			}
			
			for(int i=0; i<images.length; i++) {
				// Propagate the inputs forward to compute the outputs
				double[] weightedSums = new double[neurons.length];
				double[] outputs = new double[neurons.length];
				for(int j=0; j<weightedSums.length; j++) {
					weightedSums[j] = neurons[j].getOutput(images[i]);
					outputs[j] = sigmoidFunction(weightedSums[j]);
				}
				
				double[] deltas = new double[neurons.length];
				for(int j=0; j<deltas.length; j++) {
					deltas[j] = sigmoidPrime(weightedSums[j]) * ((labels[i] == j ? 1.0 : 0.0) - outputs[j]);
				}
				
				for(int j=0; j<neurons.length; j++) {
					double[] weights = neurons[j].getWeights();
					for(int k=0; k<weights.length; k++) {
						weights[k] += ALPHA * images[i][k] * deltas[j];
						System.out.printf("neuron %d weight %d set to %f%n", j, k, weights[k]);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
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
