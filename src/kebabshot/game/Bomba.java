package kebabshot.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Bomba extends Sprite{
	
	public Bomba(int x, int y, int speed) {
		super(x, y, speed);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void draw(Graphics2D g2D) {
		g2D.setColor(Color.ORANGE);
		g2D.fillOval(getX(), getY(), 10, 10);
	}

	public void update() {
		setY(getY() + getSpeed());
	}
	
	public Rectangle getBound(){
		return new Rectangle(getX(),getY(),10,10); //10x10, bo takie sa wymiary kuli
	}
}
