package kebabshot.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class KebabGun extends Sprite {

	private final Image image;

    public KebabGun(int x, int y, int speed) {
        super(x, y, speed);
        this.image = new ImageIcon(getClass().getResource("/kebabshot/game/asset/images/kebab.png")).getImage();
    }

    @Override
    protected void draw(Graphics2D g2D) {
        g2D.drawImage(this.image, getX(), getY(), null);
    }

    public void shoot() {
    	
    	System.out.println("XD");
    }

    public void moveLeft() {
        if (getX() < 0) {
            return;
        }
        incSpeed();
        setX(getX() - getSpeed());
    }

    public void moveRight() {
        if (getX() > GAME__WIDTH - 50) {
            return;
        }
        incSpeed();
        setX(getX() + getSpeed());
    }
    
    private void incSpeed() {
        if (getSpeed() < AUTO_SPEED) {
            setSpeed(getSpeed() + 1);
        }
    }

    void resetSpeed() {
        setSpeed(0);
    }

    public Rectangle getBound() {
        return new Rectangle(getX(), getY(), this.image.getWidth(null), this.image.getHeight(null));
    }
}