package en.neuralnet.ocr.gui;

import java.awt.Color;
import java.util.ArrayList;

import en.neuralnet.util.Guess;
import acm.graphics.*;

public class ResultsDisplay extends GCanvas {
	private static final long serialVersionUID = -6792370645641000429L;		
    private final int displayWidth, displayHeight;
	
    private static final int X_GAP = 10;
    
    private static final double VALUE_FOR_GREEN = 0.5;
    private static final double VALUE_FOR_YELLOW = 0.1;
    
	
    public ResultsDisplay(int displayWidth, int displayHeight) {   	
    	this.displayWidth = displayWidth;
    	this.displayHeight = displayHeight;
    	clearDisplay();

    }
    
    public void report(ArrayList<Guess> guesses) {
    	double blockHeight = displayHeight/(double)guesses.size();
    	double barHeight = 0.9 * blockHeight;
    	double barTopOffset = (blockHeight - barHeight) / 2.0;
    	
    	for (int i = 0; i < guesses.size(); i++) {
    		char c = guesses.get(i).getCharacter();
    		double v = guesses.get(i).getValue();
    		
    		GLabel nameLabel = new GLabel(Character.toString(c));
    		nameLabel.setFont("Courier-*-16");
    		add(nameLabel, X_GAP, blockHeight*i + blockHeight/2 + nameLabel.getAscent()/2);
    		
    		GLabel valueLabel = new GLabel(String.format("%.4f", (v)));
    		valueLabel.setFont("Courier-*-12");
    		
        	double maxBarLength = displayWidth - 3*X_GAP - nameLabel.getWidth();
        	double barLength = v * maxBarLength;
    		
        	GRect bar = new GRect(barLength, barHeight);
        	Color col = ((v >= VALUE_FOR_GREEN) ? Color.GREEN : ((v >= VALUE_FOR_YELLOW) ? Color.ORANGE : Color.LIGHT_GRAY));
    		bar.setColor(col);
    		bar.setFilled(true);
    		bar.setFillColor(col);
        	add(bar, X_GAP*2 + nameLabel.getWidth(), blockHeight*i + barTopOffset);
    		
    		add(valueLabel, X_GAP*3 + nameLabel.getWidth() + 50,  blockHeight*i + blockHeight/2 + valueLabel.getAscent()/2);
    	}	
    }
    
    public void clearDisplay() {
    	this.removeAll();
    	
    	GRect r = new GRect(0, 0, displayWidth, displayHeight);
    	r.setFilled(true);
    	r.setFillColor(new Color(240,240,240));
    	r.setColor(new Color(240,240,240));
    	this.add(r);
    }
}
