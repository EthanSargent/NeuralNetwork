package en.neuralnet.ocr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TrainingDataGen {
	private static final String BASE_DIR = "training-data\\";
	private static final int SIDE = 16;
	
	public static void main(String[] args) {
		for(char c : NeuralNetwork.CHARACTERS.toCharArray()) {
			String folderName = Character.isUpperCase(c) ? (c + "\\upper") : (c + "\\lower");
			File folder = new File(BASE_DIR + folderName + "\\");
			folder.mkdirs();
			BufferedImage bi = new BufferedImage(SIDE, SIDE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, SIDE, SIDE);
			g.setColor(Color.BLACK);
			g.drawString(Character.toString(c), 0, SIDE - 2);
			try {
				ImageIO.write(bi, "png", new File(BASE_DIR + folderName + "\\default.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
