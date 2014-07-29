package en.neuralnet.ocr.gui;

import java.awt.Color;
import java.awt.Graphics2D;
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
	public static final int APPLICATION_WIDTH = 100;
	public static final int APPLICATION_HEIGHT = 200;
	
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
            		System.out.printf("x = [%f,%f], y = [%f,%f]%n", minX, maxX, minY, maxY);
            		int w = (int) (maxX - minX);
            		int h = (int) (maxY - minY);
            		double factor = 28.0 / Math.max(w, h);
            		System.out.printf("cut image is %d x %d%n", w, h);
            		System.out.printf("max is %d%n", Math.max(w, h));
            		System.out.printf("scale factor is %f%n", factor);
            		
            		BufferedImage bi = new BufferedImage(IMAGE_SIDE, IMAGE_SIDE, BufferedImage.TYPE_INT_ARGB);
            		Graphics2D g = bi.createGraphics();
            		g.setColor(Color.BLACK);
            		for(GOval oval : path) {
            			g.fillOval((int) ((oval.getX() - minX) * factor), (int) ((oval.getY() - minY) * factor), (int) (oval.getWidth() * factor), (int) (oval.getHeight() * factor));
            			remove(oval);
            			//System.out.printf("drew oval at (%d,%d)%n", (int) ((oval.getX() - minX) * factor), (int) ((oval.getY() - minY) * factor));
            			//System.out.printf("drew %d x %d oval%n", (int) (oval.getWidth() * factor), (int) (oval.getHeight() * factor));
            		}
            		g.dispose();
            		saveImage(bi, "original.png");
            		
            		int[] raw = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, w);
            		
            		double[] image = new double[IMAGE_SIZE];
            		for(int i=0; i<image.length; i++) {
            			Color c = new Color(raw[i]);
            			image[i] = 1.0 - ((c.getRed() + c.getGreen() + c.getBlue()) / 765.0); // 765 = 255 * 3
            			System.out.print(image[i] + ",");
            		}
            		
            		char guess = network.guess(image);
            		JOptionPane.showMessageDialog(GUI.this, "Best guess: " + guess);
            		path.clear();
            	}
            }});
        add(submit, NORTH);
        
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
    	GOval oval = new GOval(x, y, penSize, penSize);
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
     * Used for debugging.
     * 
     * @param i
     * @param out
     */
    public static final void saveImage(BufferedImage i, String out) {
    	try {
    		System.out.println("saving image with extension " + out.substring(out.lastIndexOf(".") + 1) + " to " + out);
    		boolean rt = ImageIO.write(i, out.substring(out.lastIndexOf(".") + 1), new File(out));
			System.out.println(rt ? "success" : "failure");
			System.out.println();
			/*for(int w=0; w<i.getWidth(); w++) {
				for(int h=0; h<i.getHeight(); h++) {
					System.out.print(i.getRGB(w, h) + ",");
				}
				System.out.println();
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
