package en.neuralnet.ocr.data;

public abstract class ImageManager {
	public final int[] getLabels() {
		return getLabels(-1);
	}
	
	public abstract int[] getLabels(int limit);
	
	public final ImageData getImages() {
		return getImages(-1);
	}
	
	public abstract ImageData getImages(int limit);
}
