package en.neuralnet.ocr;

public class ImageData {
	private final double[][] images;
	private final int rows;
	private final int cols;
	
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
