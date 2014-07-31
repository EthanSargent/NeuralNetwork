package en.neuralnet.ocr.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import acm.graphics.GOval;

public class UCIManager extends ImageManager {
	private static final int PEN_SIZE = 2;
	private final List<Character> chars = new ArrayList<Character>();
	
	public UCIManager() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("ujipenchars2.txt"));
			String line;
			while((line = br.readLine()) != null) {
				if(!line.startsWith("WORD")) continue;
				char character = line.charAt("WORD ".length());
				chars.add(character);
				
				int numStrokes = Integer.parseInt(br.readLine().substring("  NUMSTROKES ".length()));
				GOval[][] pts = new GOval[numStrokes][];
				for(int i=0; i<pts.length; i++) {
					String ptsLine = br.readLine();
					int searchStart = "  NUMSTROKES ".length();
					int searchEnd = ptsLine.indexOf(" ", searchStart);
					int numPts = Integer.parseInt(ptsLine.substring(searchStart, searchEnd));
					pts[i] = new GOval[numPts];
					String[] split = ptsLine.substring(ptsLine.indexOf("#") + 2).split(" ");
					if(split.length != pts[i].length * 2) throw new IllegalArgumentException("Reported number of points does not match actual number of points.");
					
					int maxX = Integer.MIN_VALUE;
					int maxY = Integer.MIN_VALUE;
					for(int j=0; j<pts[i].length; j++) {
						int x = Integer.parseInt(split[j * 2]);
						if(x > maxX) maxX = x;
						int y = Integer.parseInt(split[j * 2 + 1]);
						if(y > maxY) maxY = y;
						pts[i][j] = new GOval(x, y, PEN_SIZE, PEN_SIZE);
					}
					
					BufferedImage bi = new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = bi.createGraphics();
					g.setColor(Color.BLACK);
					for(int j=0; j<pts[i].length; j++) {
						g.fillOval((int) pts[i][j].getX(), (int) pts[i][j].getY(), PEN_SIZE, PEN_SIZE);
					}
					g.dispose();
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int[] getLabels(int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageData getImages(int limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
