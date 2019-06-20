package kebabshot.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class GamePanel extends Canvas implements Runnable {

	private Thread gameThread;
    private final Sprite background = new Background(0, 0, 0);
    private final KebabGun kebabgun = new KebabGun(GAME__WIDTH / 2 - 23, GAME__HEIGHT - 75, 2);
    private boolean isRunning;
    private final int hampos [] = {0,45,100,150}; //pozycje hamburgerow
    private int currentPos;
    private int zycia = 5;
    int result = 0;
    private long wynik;
    private String name;
    private long startTime,endTime,waitTime;
    private double start,end;
    Object[][] data;

    String[] columnNames = {"Name",
            "Score",
            "Time",};

    ArrayList<Integer> keys=new ArrayList();
    
    private ArrayList<Kula> kule = new ArrayList<>();
    private ArrayList<Hamburger> hamburgery = new ArrayList<>();
    private ArrayList<Bomba> bomby = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(GAME__WIDTH, GAME__HEIGHT));
    }

    @Override
    protected void onKeyUp(KeyEvent event) {
        if(keys.contains(event.getKeyCode())){
            keys.remove(keys.indexOf(event.getKeyCode()));
        }
    }

    @Override
    protected void onKeyPressed(KeyEvent event) {
        if(!keys.contains(event.getKeyCode())){
            keys.add(event.getKeyCode());
        }else if(keys.contains(KeyEvent.VK_RIGHT) && keys.contains(KeyEvent.VK_X)){
            kebabgun.moveRight();
            kule.add(new Kula(kebabgun.getX()+20,kebabgun.getY(),getRandomSpeed()));kule.add(new Kula(kebabgun.getX()+20,kebabgun.getY(),getRandomSpeed()));
        }else if(keys.contains(KeyEvent.VK_LEFT) && keys.contains(KeyEvent.VK_X)){
            kebabgun.moveLeft();
            kule.add(new Kula(kebabgun.getX()+20,kebabgun.getY(),getRandomSpeed()));kule.add(new Kula(kebabgun.getX()+20,kebabgun.getY(),getRandomSpeed()));
        }else if(keys.contains(KeyEvent.VK_RIGHT)){
            kebabgun.moveRight();
        }else if(keys.contains(KeyEvent.VK_X)){
            kule.add(new Kula(kebabgun.getX()+20,kebabgun.getY(),getRandomSpeed()));
        }else if(keys.contains(KeyEvent.VK_LEFT)){
            kebabgun.moveLeft();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (gameThread == null) {
            gameThread = new Thread(GamePanel.this);
        }
        gameThread.start();
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
        if (!options()){
            System.exit(0);
        }
        start = System.currentTimeMillis();
        while (isRunning) {

            startTime = System.currentTimeMillis();

            updateGame();
            renderGame();

            endTime = System.currentTimeMillis() - startTime;
            waitTime = (MILLISECOND / FPS) - endTime / MILLISECOND;

            try {
                Thread.sleep(waitTime);
            } catch (Exception ignored) {
            }
        }
    }

    private boolean options() {
        JFrame frame = new JFrame("Welcome");

        // prompt the user to enter their name
        name = JOptionPane.showInputDialog(frame, "What's your name?");
        if(name != null){
            return true;
        }
            // get the user's input. note that if they press Cancel, 'name' will be null
            System.out.printf("The user's name is '%s'.\n", name);
        return false;
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
    		    //you can make it in a different way
                end = System.currentTimeMillis() - start;
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con= DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/Gra","root","");
                    Statement stmt=con.createStatement();
                    //String x = Integer.toString(wynik);
                    stmt.executeUpdate("INSERT INTO gameval " + "VALUES ('"+end+"','"+name+"', '"+wynik+"' )");
                    int xdd =1;
                    ResultSet rs1=stmt.executeQuery("SELECT * FROM gameval ORDER BY score DESC LIMIT 5");
                    ResultSet rs2=rs1;
                    ResultSet rs3=rs1;
                    ResultSet rs4=rs1;
                    ResultSet rs5=rs1;
                    rs1.absolute(1);
                    rs2.absolute(2);
                    rs2.absolute(3);
                    rs2.absolute(4);
                    rs2.absolute(5);
                    data = new Object[][]{
                            {rs1.getString(2), rs1.getInt(3), rs1.getDouble(1)},
                            {rs2.getString(2), rs2.getInt(3), rs2.getDouble(1)},
                            {rs3.getString(2), rs3.getInt(3), rs3.getDouble(1)},
                            {rs4.getString(2), rs4.getInt(3), rs4.getDouble(1)},
                            {rs5.getString(2), rs5.getInt(3), rs5.getDouble(1)},
                    };
                    con.close();
                }catch(Exception e){ System.out.println(e);}
    			//int result = JOptionPane.showConfirmDialog(this, "You lose. Play again?");
                final JTable table = new JTable(data, columnNames);
                JPanel jPanel = new JPanel();
                jPanel.setLayout(new GridLayout());
                JScrollPane sp = new JScrollPane(table);
                JButton btn = new JButton();
                btn.setText("Close");
                jPanel.add(sp);
                jPanel.add(btn);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        result = 1;
                    }
                });
                JDialog jdialog = new JDialog();
                jdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                jdialog.setContentPane(jPanel);
                jdialog.pack();
                if ( result == 0 ) {
                    jdialog.setVisible(true);
                    result = 3;
				}else if(result == 1){
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