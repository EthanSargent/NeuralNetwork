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

public class GUI extends GraphicsProgram {
    private static final long serialVersionUID = -1655816033198879264L;
    private static final double PEN_SIZE = 2.0;
    
    private final NeuralNetwork network = new NeuralNetwork();
    private List<GOval> path = new ArrayList<GOval>();
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    
    public GUI() {
        setTitle("OCR via Neural Network");
        
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(path.size() <= 0) {
            		JOptionPane.showMessageDialog(GUI.this, "Nothing to submit!");
            	} else {
            		BufferedImage bi = new BufferedImage((int) (maxX - minX), (int) (maxY - minY), BufferedImage.TYPE_INT_ARGB);
            		Graphics2D g = bi.createGraphics();
            		g.setColor(Color.BLACK);
            		for(GOval oval : path) {
            			g.fillOval((int) (oval.getX() - minX), (int) (oval.getY() - minY), (int) oval.getWidth(), (int) oval.getHeight());
            			remove(oval);
            		}
            		g.dispose();
            		//saveImage(bi, "painted.png");
            		int w = bi.getWidth();
            		int h = bi.getHeight();
            		double[] image = new double[w * h];
            		int[] raw = new int[w * h];
            		raw = bi.getRGB(0, 0, w, h, raw, 0, w);
            		for(int i=0; i<image.length; i++) {
            			Color c = new Color(raw[i]);
            			image[i] = (c.getRed() + c.getGreen() + c.getBlue()) / 765.0; // 765 = 255 * 3
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
    	GOval oval = new GOval(x, y, PEN_SIZE, PEN_SIZE);
    	oval.setFilled(true);
    	add(oval);
        path.add(oval);
        
        if(x < minX) minX = x;
        if(x > maxX) maxX = x;
        if(y < minY) minY = y;
        if(y > maxY) maxY = y;
    }
    
    /**
     * Used for debugging.
     * 
     * @param i
     * @param out
     */
    private static final void saveImage(BufferedImage i, String out) {
    	try {
    		System.out.println("saving image with extension " + out.substring(out.lastIndexOf(".") + 1) + " to " + out);
    		boolean rt = ImageIO.write(i, out.substring(out.lastIndexOf(".") + 1), new File(out));
			System.out.println(rt ? "success" : "failure");
			System.out.println();
			for(int w=0; w<i.getWidth(); w++) {
				for(int h=0; h<i.getHeight(); h++) {
					System.out.print(i.getRGB(w, h) + ",");
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
