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
	private static final boolean DRAW_BORDER = true;
	private static final int SAMPLES = 25;
	private static final String FOLDER = "example_data\\";
	
	public static void main(String[] args) {
		ImageManager im = new ImageManager(false);
		ImageData imageData = im.getImages(SAMPLES);
		double[][] images = imageData.getImages();
		
		for(int j=0; j<images.length; j++) {
			int[] rgbs = new int[images[j].length];
			for(int i=0; i<rgbs.length; i++) {
				if(DRAW_BORDER) {
					int x = i / imageData.getNumCols();
					int y = i % imageData.getNumCols();
					if(x == 0 || x == imageData.getNumCols() - 1 || y == 0 || y == imageData.getNumRows() - 1) {
						rgbs[i] = Color.BLACK.getRGB();
						continue;
					}
				}
				float flip = 1.0f - (float) images[j][i];
				Color c = new Color(flip, flip, flip);
				rgbs[i] = c.getRGB();
			}

			BufferedImage out = new BufferedImage(imageData.getNumCols(), imageData.getNumRows(), BufferedImage.TYPE_INT_ARGB);
			out.setRGB(0, 0, out.getWidth(), out.getHeight(), rgbs, 0, out.getWidth());
			GUI.saveImage(out, FOLDER + j + ".png");
		}
	}
}
