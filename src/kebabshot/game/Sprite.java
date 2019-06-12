package kebabshot.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

//tlo
public abstract class Sprite implements Helper{
	private int x, y, speed;

    public Sprite(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    
    protected abstract void draw(Graphics2D g2D);
    

}
