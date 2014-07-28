package en.neuralnet.ocr.data;

//This class stores all image data and key image getter and setter methods
public class ImageData {
	private final double[][] images;
	private final int rows;
	private final int cols;
	
	//This constructor packages information to be returned to the main class
	public ImageData(double[][] images, int rows, int cols) {
		this.images = images;
		this.rows = rows;
		this.cols = cols;
	}
	
	public double[][] getImages() {
		return images;
	}
	
	public int getNumRows() {
		return rows;
	}
	
	public int getNumCols() {
		return cols;
	}
	
	public int getNumImages() {
		return images.length;
	}
}
