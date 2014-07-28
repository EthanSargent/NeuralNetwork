package en.neuralnet.ocr.gui;

import acm.graphics.GRectangle;

/**
 * A version of the ACM GPen class that actually reports width/height and bounds. Works alright. Not designed for use outside this project.
 * 
 * @author Greg Carlin
 *
 */
public class GPen extends acm.graphics.GPen {
	private static final long serialVersionUID = 6044780169779334545L;

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	
	public GPen(double x, double y) {
		super(x, y);
		minX = x;
		maxX = x;
		minY = y;
		maxY = y;
	}
	
	private void moved(double x, double y) {
		if(x < minX) minX = x;
		if(x > maxX) maxX = x;
		if(y < minY) minY = y;
		if(y > maxY) maxY = y;
	}
	
	@Override
	public void drawLine(double dx, double dy) {
		super.drawLine(dx, dy);
		
		moved(dx, dy);
	}
	
	@Override
	public GRectangle getBounds() {
		return new GRectangle(getX(), getY(), getWidth(), getHeight());
	}
	
	@Override
	public void setLocation(double x, double y) {
		super.setLocation(x, y);
		
		moved(x, y);
	}
	
	@Override
	public double getWidth() {
		return maxX - minX;
	}
	
	@Override
	public double getHeight() {
		return maxY - minY;
	}
}
