package en.neuralnet.ocr.mains;

import en.neuralnet.ocr.NeuralNetwork;
import en.neuralnet.ocr.data.ImageData;
import en.neuralnet.ocr.data.ImageManager;

public class Trainer {
	public static void main(String[] args) {
		//Retrieving image labels (correct answers that backstop the training
		//process of the neural network)
		System.out.println("reading image labels");
		int[] labels = ImageManager.getLabels(10);

		//Retrieves the map of grayscale values for each image in a 2d array
		//(Each image gets a row)
		System.out.println("reading images");
		ImageData imageData = ImageManager.getImages(10);
		double[][] images = imageData.getImages();

		//Makes sure each image is assigned a correct answer label
		if(labels.length != images.length) throw new IllegalArgumentException("Number of labels is not equal to number of images.");


		//new java.io.File("weights.txt").delete();
		System.out.println("creating neural network");
		NeuralNetwork network = new NeuralNetwork();
		
		for(int i=0; i<images.length; i++) {
			network.train(images[i], Character.forDigit(labels[i], 10));
		}
		
		network.saveWeights();
	}
}
