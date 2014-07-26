package en.neuralnet.ocr.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import en.neuralnet.ocr.NeuralNetwork;
import acm.graphics.GPen;
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
                char guess = NeuralNetwork.guess(currentPen.getPenImage());
                JOptionPane.showMessageDialog(GUI.this, "Best guess: " + guess);
                remove(currentPen);
            }});
        add(submit, NORTH);
        
        addMouseListeners();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        currentPen = new GPen(lastX, lastY);
        add(currentPen);
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
