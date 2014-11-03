package en.neuralnet.ocr.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import en.neuralnet.ocr.NeuralNetwork;
import acm.graphics.GOval;
import acm.program.GraphicsProgram;
import static en.neuralnet.ocr.NeuralNetwork.*;

public class GUI extends GraphicsProgram {
	public static final int APPLICATION_WIDTH = 800;
	public static final int APPLICATION_HEIGHT = 600;
	
    private static final long serialVersionUID = -1655816033198879264L;
    
    private final double penSize;
    private final NeuralNetwork network = new NeuralNetwork();
    private List<GOval> path = new ArrayList<GOval>();
    private double minX = Double.MAX_VALUE;
    private double maxX = -Double.MAX_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxY = -Double.MAX_VALUE;
    
    public GUI() {
        setTitle("OCR via Neural Network");
        penSize = 2.0 / (28.0 / Math.max(APPLICATION_WIDTH, APPLICATION_HEIGHT));
        
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(path.size() <= 0) {
            		JOptionPane.showMessageDialog(GUI.this, "Nothing to submit!");
            	} else {
            		//System.out.printf("x = [%f,%f], y = [%f,%f]%n", minX, maxX, minY, maxY);
            		int w = (int) (maxX - minX);
            		int h = (int) (maxY - minY);
            		double factor = IMAGE_SUB_SIDE / (double) Math.max(w, h);
            		//System.out.printf("cut image is %d x %d%n", w, h);
            		//System.out.printf("max is %d%n", Math.max(w, h));
            		//System.out.printf("scale factor is %f%n", factor);
            		
            		BufferedImage subImage = new BufferedImage(IMAGE_SUB_SIDE, IMAGE_SUB_SIDE, BufferedImage.TYPE_INT_ARGB);
            		int subImageWidth = subImage.getWidth();
            		int subImageHeight = subImage.getHeight();
            		Graphics2D g = subImage.createGraphics();
            		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            		g.setColor(Color.WHITE);
            		g.fillRect(0, 0, subImageWidth, subImageHeight);
            		g.setColor(Color.BLACK);
            		for(GOval oval : path) {
            			g.fillOval((int) ((oval.getX() - minX) * factor), (int) ((oval.getY() - minY) * factor), (int) (oval.getWidth() * factor), (int) (oval.getHeight() * factor));
            			remove(oval);
            			//System.out.printf("drew oval at (%d,%d)%n", (int) ((oval.getX() - minX) * factor), (int) ((oval.getY() - minY) * factor));
            			//System.out.printf("drew %d x %d oval%n", (int) (oval.getWidth() * factor), (int) (oval.getHeight() * factor));
            		}
            		g.dispose();
            		//saveImage(subImage, "sub_image.png", false);
            		
            		// compute center of mass of character
            		int[] rawSubImage = subImage.getRGB(0, 0, subImageWidth, subImageHeight, null, 0, subImageWidth);
            		double avgX = 0.0;
            		double avgY = 0.0;
            		double totalWeight = 0.0;
            		for(int i=0; i<rawSubImage.length; i++) {
            			int y = i / subImageWidth;
            			int x = i % subImageWidth;
            			//System.out.printf("coord: (%d,%d)%n", x, y);
            			
            			double gray = 1.0 - convertRGB(rawSubImage[i]);
            			//System.out.printf("%d converted to %f%n", rawSubImage[i], gray);
            			avgX += x * gray;
            			avgY += y * gray;
            			totalWeight += gray;
            			//System.out.printf("avgX=%f,avgY=%f,totalWeight=%f%n", avgX, avgY, totalWeight);
            		}
            		//System.out.printf("avgX=%f,avgY=%f%n", avgX, avgY);
            		avgX /= totalWeight;
            		avgY /= totalWeight;
            		//System.out.printf("center is (%f,%f)%n", avgX, avgY);
            		//subImage.setRGB((int) avgX, (int) avgY, Color.RED.getRGB());
            		
            		BufferedImage placed = new BufferedImage(IMAGE_SIDE, IMAGE_SIDE, BufferedImage.TYPE_INT_ARGB);
            		int placedW = placed.getWidth();
            		int placedH = placed.getHeight();
            		Graphics2D gPlaced = placed.createGraphics();
            		gPlaced.setColor(Color.WHITE);
            		gPlaced.fillRect(0, 0, placedW, placedH);
            		gPlaced.drawImage(subImage, (int) (placedW / 2 - avgX), (int) (placedH / 2 - avgY), null);
            		gPlaced.dispose();
            		//saveImage(placed, "placed.png", false);
            		
            		int[] raw = placed.getRGB(0, 0, placedW, placedH, null, 0, placedW);
            		double[] image = new double[IMAGE_SIZE];
            		for(int i=0; i<image.length; i++) {
            			image[i] = 1.0 - convertRGB(raw[i]);
            		}
            		
            		char guess = network.guess(image);
            		JOptionPane.showMessageDialog(GUI.this, "Best guess: " + guess);
            		path.clear();
            		
            		minX = Double.MAX_VALUE;
            	    maxX = -Double.MAX_VALUE;
            	    minY = Double.MAX_VALUE;
            	    maxY = -Double.MAX_VALUE;
            	}
            }});
        add(submit, NORTH);
        
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(GOval goval : path) {
					remove(goval);
				}
				path.clear();

        	    minX = Double.MAX_VALUE;
        	    maxX = -Double.MAX_VALUE;
        	    minY = Double.MAX_VALUE;
        	    maxY = -Double.MAX_VALUE;
			}});
        add(clear, NORTH);
        
        addMouseListeners();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        mouseMove(e.getX(), e.getY());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    	mouseMove(e.getX(), e.getY());
    }
    
    private void mouseMove(int x, int y) {
    	GOval oval = new GOval(x - penSize / 2, y - penSize / 2, penSize, penSize);
    	oval.setFilled(true);
    	add(oval);
        path.add(oval);
        
        double bigX = x + penSize / 2;
        double smallX = x - penSize / 2;
        double bigY = y + penSize / 2;
        double smallY = y - penSize / 2;
        if(smallX < minX) minX = smallX;
        if(bigX > maxX) maxX = bigX;
        if(smallY < minY) minY = smallY;
        if(bigY > maxY) maxY = bigY;
    }
    
    /**
     * Converts an RGB int value to a grayscale float value between 0 and 1
     * 
     * @param rgb
     * @return
     */
    private static final float convertRGB(int rgb) {
    	Color c = new Color(rgb);
    	return (c.getRed() + c.getGreen() + c.getBlue()) / 765.0f;
    }
    
    public static final void saveImage(BufferedImage i, String out) {
    	saveImage(i, out, false);
    }
    
    /**
     * Used for debugging.
     * 
     * @param i
     * @param out
     * @param drawBorders
     */
    public static final void saveImage(BufferedImage i, String out, boolean drawBorders) {
    	try {
    		if(drawBorders) {
    			Graphics2D g = i.createGraphics();
    			g.setColor(Color.BLACK);
    			g.drawRect(0, 0, i.getWidth() - 1, i.getWidth() - 1);
    			g.dispose();
    		}
    		
    		System.out.println("saving image with extension " + out.substring(out.lastIndexOf(".") + 1) + " to " + out);
    		boolean rt = ImageIO.write(i, out.substring(out.lastIndexOf(".") + 1), new File(out));
			System.out.println(rt ? "success" : "failure");
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
