package en.neuralnet.ocr;

import en.neuralnet.ocr.data.ImageData;
import en.neuralnet.ocr.data.ImageManager;
import en.neuralnet.ocr.gui.GUI;

public class Main {
	private static final boolean GUI = false;
	private static final boolean LARGE_DATA = false;
	private static final boolean DEBUG = false;
	
	public static void main(String[] args) {
		if(GUI) {
			new GUI().start(args);
		} else {
			Main cli = new Main(LARGE_DATA/*, 10*/);
			//cli.train(6);
			cli.test();
		}
	}
	
	private final ImageManager im;
	private final int[] labels;
	private final double[][] images;
	private final NeuralNetwork network;
	
	public Main(boolean useLargeData) {
		this(useLargeData, -1);
	}
	
	public Main(boolean useLargeData, int limit) {
		this.im = new ImageManager(useLargeData);
		
		//Retrieving image labels (correct answers that backstop the training
		//process of the neural network)
		System.out.println("reading image labels");
		this.labels = im.getLabels(limit);

		//Retrieves the map of grayscale values for each image in a 2d array
		//(Each image gets a row)
		System.out.println("reading images");
		ImageData imageData = im.getImages(limit);
		this.images = imageData.getImages();

		//Makes sure each image is assigned a correct answer label
		if(labels.length != images.length) throw new IllegalArgumentException("Number of labels is not equal to number of images.");
		
		this.network = new NeuralNetwork(DEBUG);
	}
	
	public void train() {
		train(1);
	}
	
	public void train(int iterations) {
		System.out.println("training network");
		for(int j=0; j<iterations; j++) {
			System.out.println("j = " + j);
			for(int i=0; i<images.length; i++) {
				//System.out.println("---------");
				network.train(images[i], Character.forDigit(labels[i], 10));
				//System.out.println("---------");
			}
		}
		
		System.out.println("saving weights");
		network.saveWeights();
	}
	
	public void test() {
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
