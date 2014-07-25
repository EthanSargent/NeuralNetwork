package en.neuralnet.ocr;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageManager {
	private static final String DATA_FOLDER = "C:\\Users\\scamper\\My Documents\\Neural network\\data\\";
	private static final boolean IS_LARGE_DATA = false;
	private static final String IMAGE_FILE = IS_LARGE_DATA ? "train-images-idx3-ubyte" : "t10k-images-idx3-ubyte";
	private static final String LABEL_FILE = IS_LARGE_DATA ? "train-labels-idx1-ubyte" : "t10k-labels-idx1-ubyte";
	
	//Reads the labels of the input images and returns them in an array to the main class
	public static int[] getLabels() {
		DataInputStream labelInputStream;
		try {
			labelInputStream = new DataInputStream(new FileInputStream(DATA_FOLDER + LABEL_FILE));
			labelInputStream.readInt(); // magic number
			int numLabels = labelInputStream.readInt();
			
			//Creates the array of training/testing image labels which 
			//serve as correct answers as a framework for the NN's guesses
			int[] labels = new int[numLabels];
			for(int i=0; i<labels.length; i++) {
				labels[i] = labelInputStream.readUnsignedByte();
			}
			labelInputStream.close();
			
			return labels;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	//Returns important image data to the main class
	public static ImageData getImages() {
		try {
			DataInputStream imgInputStream = new DataInputStream(new FileInputStream(DATA_FOLDER + IMAGE_FILE));
			imgInputStream.readInt(); // magic number
			int numImgs = imgInputStream.readInt();
			int numRows = imgInputStream.readInt();
			int numCols = imgInputStream.readInt();
			
			//Reduces each image pixel to it's grayscale value for processing by the neural network
			double[][] images = new double[numImgs][numRows * numCols];
			for( int i=0; i<images.length; i++) {
				for(int j=0; j<images[i].length; j++) {
					images[i][j] = imgInputStream.readUnsignedByte() / 255.0;
				}
			}
			imgInputStream.close();
			
			return new ImageData(images, numRows, numCols);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
