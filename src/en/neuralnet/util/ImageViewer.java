package en.neuralnet.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

import en.neuralnet.ocr.data.ImageData;
import en.neuralnet.ocr.data.ImageManager;
import en.neuralnet.ocr.gui.GUI;

/**
 * Not used by any other classes. Converts an image of handwriting to a viewable bufferedimage.
 * 
 * @author Greg Carlin
 *
 */
public class ImageViewer {
	public static void main(String[] args) {
		ImageManager im = new ImageManager(false);
		ImageData imageData = im.getImages(1);
		double[] image = imageData.getImages()[0];
		
		int[] rgbs = new int[image.length];
		for(int i=0; i<rgbs.length; i++) {
			float flip = 1.0f - (float) image[i];
			Color c = new Color(flip, flip, flip);
			rgbs[i] = c.getRGB();
		}
		
		BufferedImage out = new BufferedImage(imageData.getNumCols(), imageData.getNumRows(), BufferedImage.TYPE_INT_ARGB);
		out.setRGB(0, 0, out.getWidth(), out.getHeight(), rgbs, 0, out.getWidth());
		GUI.saveImage(out, "test.png");
	}
}
