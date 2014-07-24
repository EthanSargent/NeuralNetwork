package en.neuralnet.ocr;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NeuralNetwork {
	
	private static final String DATA_FOLDER = "C:\\Users\\scamper\\My Documents\\Neural network\\data\\";
	private static final String IMAGE_FILE = "t10k-images-idx3-ubyte";
	private static final String LABEL_FILE = "t10k-labels-idx1-ubyte";
	
	public static void main(String[] args) {
		/*Path imagePath = Paths.get(DATA_FOLDER + IMAGE_FILE);
		Path labelPath = Paths.get(DATA_FOLDER + LABEL_FILE);*/
		try {
			/*byte[] imageInput = Files.readAllBytes(imagePath);
			byte[] labelInput = Files.readAllBytes(labelPath);*/
			
			DataInputStream labelInputStream = new DataInputStream(new FileInputStream(DATA_FOLDER + LABEL_FILE));
			int magicLabelNum = labelInputStream.readInt();
			int numItems = labelInputStream.readInt();
			
			byte[] labels = new byte[numItems];
			for(int i=0; i<labels.length; i++) {
				labels[i] = labelInputStream.readByte();
			}
			
			DataInputStream imgInputStream = new DataInputStream(new FileInputStream(DATA_FOLDER + IMAGE_FILE));
			int magicImgNum = imgInputStream.readInt();
			int numImgs = imgInputStream.readInt();
			int numRows = imgInputStream.readInt();
			int numCols = imgInputStream.readInt();
			
			double[][] images = new double[numImgs][numRows * numCols];
			for(int i=0; i<images.length; i++) {
				for(int j=0; j<images[i].length; j++) {
					images[i][j] = imgInputStream.readByte();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
