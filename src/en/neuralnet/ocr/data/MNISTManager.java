package en.neuralnet.ocr.data;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class MNISTManager extends ImageManager {
	private static final String DATA_FOLDER = "C:\\Users\\scamper\\My Documents\\Neural network\\data\\";
	private static final String IMAGE_FILE_LARGE = "train-images-idx3-ubyte";
	private static final String IMAGE_FILE_SMALL = "t10k-images-idx3-ubyte";
	private static final String LABEL_FILE_LARGE = "train-labels-idx1-ubyte";
	private static final String LABEL_FILE_SMALL = "t10k-labels-idx1-ubyte";
	
	private final String labelFilePath;
	private final String imageFilePath;
	
	public MNISTManager(boolean useLargeData) {
		labelFilePath = DATA_FOLDER + (useLargeData ? LABEL_FILE_LARGE : LABEL_FILE_SMALL);
		imageFilePath = DATA_FOLDER + (useLargeData ? IMAGE_FILE_LARGE : IMAGE_FILE_SMALL);
	}
	
	//Reads the labels of the input images and returns them in an array to the main class
	@Override
	public int[] getLabels(int limit) {
		DataInputStream labelInputStream;
		try {
			labelInputStream = new DataInputStream(new FileInputStream(labelFilePath));
			labelInputStream.readInt(); // magic number
			int numLabels = labelInputStream.readInt();
			if(limit >= 0 && limit <= numLabels) numLabels = limit;
			
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
	@Override
	public ImageData getImages(int limit) {
		try {
			DataInputStream imgInputStream = new DataInputStream(new FileInputStream(imageFilePath));
			imgInputStream.readInt(); // magic number
			int numImgs = imgInputStream.readInt();
			if(limit >= 0 && limit <= numImgs) numImgs = limit;
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
