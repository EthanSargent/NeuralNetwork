package en.neuralnet.ocr.mains;

import en.neuralnet.ocr.NeuralNetwork;
import en.neuralnet.ocr.data.ImageData;
import en.neuralnet.ocr.data.ImageManager;

public class Tester {
	public static void main(String[] args) {
		//Retrieving image labels (correct answers that backstop the training
		//process of the neural network)
		System.out.println("reading image labels");
		int[] labels = ImageManager.getLabels();

		//Retrieves the map of grayscale values for each image in a 2d array
		//(Each image gets a row)
		System.out.println("reading images");
		ImageData imageData = ImageManager.getImages();
		double[][] images = imageData.getImages();

		//Makes sure each image is assigned a correct answer label
		if(labels.length != images.length) throw new IllegalArgumentException("Number of labels is not equal to number of images.");
		
		
		System.out.println("creating neural network");
		NeuralNetwork network = new NeuralNetwork();
		
		int correct = 0;
		for(int i=0; i<images.length; i++) {
			System.out.println("ANSWER: " + labels[i]);
			char guess = network.guess(images[i]);
			System.out.println(" GUESS: " + guess);
			if(guess == Character.forDigit(labels[i], 10)) correct++;
		}
		
		System.out.printf("%d out of %d correct.%n", correct, images.length);
	}
}
