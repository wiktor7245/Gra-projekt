package kebabshot.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GamePanel extends Canvas implements Runnable {

	private Thread gameThread;
    private final Sprite background = new Background(0, 0, 0);
    private final KebabGun kebabgun = new KebabGun(GAME__WIDTH / 2 - 23, GAME__HEIGHT - 75, 2);
    private boolean isRunning;
    private final int hampos [] = {0,45,100,150}; //pozycje hamburgerow
    private int currentPos;
    private int zycia = 5;
    private int wynik;
    
    private ArrayList<Kula> kule = new ArrayList<>();
    private ArrayList<Hamburger> hamburgery = new ArrayList<>();
    private ArrayList<Bomba> bomby = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(GAME__WIDTH, GAME__HEIGHT));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (gameThread == null) {
            gameThread = new Thread(GamePanel.this);
        }
        gameThread.start();
    }

    @Override
    protected void onKeyUp(KeyEvent event) {
    	kebabgun.resetSpeed();
    }

    @Override
    protected void onKeyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_SPACE) {
        	kule.add(new Kula(kebabgun.getX()+20,kebabgun.getY(),getRandomSpeed()));
        	//kebabgun.shoot();
        } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            kebabgun.moveLeft();
        } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            kebabgun.moveRight();
        } else if(event.getKeyCode() == KeyEvent.VK_ESCAPE){
        	System.exit(0);
        }else{
        	
        }
    }

    private int getRandomSpeed() {
		return new Random().nextInt(5) + 1; //zeby nie bylo zera
	}

	@Override
    protected void onDraw(Graphics2D g2D) {
        g2D.setColor(Color.red);
        background.draw(g2D);
        kebabgun.draw(g2D);
        
        for(Hamburger hamburger: hamburgery){
        	hamburger.draw(g2D);
        }
   
        for(Kula kula: kule){
        	kula.draw(g2D);
        }
        
        for(Bomba bomba: bomby){
        	bomba.draw(g2D);
        }
        
        g2D.setColor(Color.WHITE);
		g2D.drawString("¯ycia: " + zycia , 10, 30);
		g2D.drawString("Wynik: " + wynik , GAME__WIDTH - 100, 30);
    }

    @Override
    public void run() {
        init();
        while (isRunning) {

            long startTime = System.currentTimeMillis();

            updateGame();
            renderGame();

            long endTime = System.currentTimeMillis() - startTime;
            long waitTime = (MILLISECOND / FPS) - endTime / MILLISECOND;

            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }
        }
    }

    private void init() {
        isRunning = true;
    }

    private void updateGame() {
    	
    	if(hamburgery.size() < 5){
    		hamburgery.add(new Hamburger(GAME__WIDTH, getHamYPostion(), getRandomSpeed()));
    		currentPos++;
    	}
    	
    	for(int i=0;i<hamburgery.size();i++){
    		Hamburger hamburger = hamburgery.get(i);
    		hamburger.update();
    		
    		for(int j=0;j<kule.size();j++){
        		Kula kula = kule.get(j);
        		if(hamburger.getBound().intersects(kula.getBound())){
        			hamburgery.remove(hamburger);
        			wynik++;
        		}
    		}
    		
    		if(hamburger.getX() == new Random().nextInt(500) || hamburger.getX() == new Random().nextInt(250)){
        		bomby.add(new Bomba(hamburger.getX()+20,hamburger.getY(),getRandomSpeed()));
        	}
    		
    		if(hamburger.getX() < -90 ){
    			hamburgery.remove(hamburger);
    		}
    	}
    	
    	
    	
    	for(int i=0;i<kule.size();i++){
    		Kula kula = kule.get(i);
    		kula.update();
    		if(kula.getY() < 0){
    			kule.remove(kula);
    		}
    	}
    	
    	for(int i=0;i<bomby.size();i++){
    		Bomba bomba = bomby.get(i);
    		bomba.update();
    		
    		if(kebabgun.getBound().intersects(bomba.getBound()) && zycia > 0){
    			zycia--;
    			bomby.remove(bomba);
    		}
    		else if (zycia == 0){
    				//isRunning = false;
    			JOptionPane pane = new JOptionPane();
    			int result = pane.showConfirmDialog(this, "You lose. Play again?");
    			if ( result == JOptionPane.OK_OPTION ) {
					zycia = 5;
					wynik = 0;
				}else {
					System.exit(0);
				}
    			
    		}
    		
    		if(bomba.getY() < 0){
    			bomby.remove(bomba);
    		}
    	}
    }
    
	private int getHamYPostion() {
    	if(currentPos >= hampos.length){
    		currentPos = 0;
    	}
    	return hampos[currentPos];
	}

	private void renderGame() {
        repaint();
    }
	
}