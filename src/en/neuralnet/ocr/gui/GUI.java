package en.neuralnet.ocr.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import en.neuralnet.ocr.NeuralNetwork;
import acm.program.GraphicsProgram;

public class GUI extends GraphicsProgram {
    private static final long serialVersionUID = -1655816033198879264L;
    
    public static void main(String[] args) {
        new GUI().start(args);
    }
    
    private GPen currentPen;
    private int lastX;
    private int lastY;
    
    public GUI() {
        setTitle("OCR via Neural Network");
        
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(currentPen == null) {
            		JOptionPane.showMessageDialog(GUI.this, "Nothing to submit!");
            	} else {
            		//char guess = NeuralNetwork.guess(currentPen.getPenImage());
            		System.out.printf("curr pen is %f x %f%n", currentPen.getWidth(), currentPen.getHeight());
            		//System.out.printf("bounds are %f x %f%n", currentPen.getBounds().getWidth(), currentPen.getBounds().getHeight());
            		BufferedImage bi = new BufferedImage((int) currentPen.getWidth(), (int) currentPen.getHeight(), BufferedImage.TYPE_INT_ARGB);
            		NeuralNetwork.saveImage(bi, "before-paint.png");
            		Graphics2D g = bi.createGraphics();
            		g.setColor(Color.BLACK);
            		//g.drawRect(5, 5, 5, 5);
            		currentPen.paint(g);
            		g.dispose();
            		NeuralNetwork.saveImage(bi, "after-paint.png");
            		char guess = NeuralNetwork.guess(bi);
            		JOptionPane.showMessageDialog(GUI.this, "Best guess: " + guess);
            		remove(currentPen);
            		currentPen = null;
            	}
            }});
        add(submit, NORTH);
        
        addMouseListeners();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        if(currentPen == null) {
        	currentPen = new GPen(lastX, lastY);
        	add(currentPen);
        } else {
        	currentPen.setLocation(lastX, lastY);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if(currentPen == null) return;
        
        int x = e.getX();
        int y = e.getY();
        currentPen.drawLine(x - lastX, y - lastY);
        lastX = x;
        lastY = y;
    }
}
