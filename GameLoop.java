package projectVacMan;

import java.awt.event.KeyEvent;
import java.io.IOException;

import acm.program.GraphicsProgram;
import acm.util.JTFTools;
import de.cau.infprogoo.lighthouse.LighthouseDisplay;

public class GameLoop extends GraphicsProgram {
	private Controller controller = new Controller(this);
	private Model model = new Model(this);
	private static final int FPS = 60;
	private static final double MS_PER_UPDATE = 1000.0 / FPS;
	private int frameCount = 10;
	private int endgame = 1000000000;

	// HERE IS DIRECTION BUT IF POSSIBLE, IT WOULD BE NICER TO HAVE AN OWN
	// CONTROLLER CLASS.
	private int direction = 0;
	
	/**
	 * Initialize the window size and adds key listeners.
	 */
	public void init() {
		if(!model.isLighthouse()) {
		setSize(View.getBlockNumHorizontal() * View.getBlockSize() + (int) (View.getBlockSize() * 0.5),
				(View.getBlockNumVertical() + 4) * View.getBlockSize());
		}
		addKeyListeners();
	}

	/**
	 * Starts the game loop.
	 * 
	 * @param args boiler.
	 */
public static void main(String args[]) {
		new GameLoop().start();
	}

	/**
	 * Here is the game loop based on the concept mentioned in the linked article
	 * about game loops.
	 */
public void run() {
	/*
	 * Lighthouse variant.
	 */
	if(model.isLighthouse()) {
		LighthouseDisplay display = null;
		add(model.getlView());
		try {
			display = LighthouseDisplay.getDisplay();
			display.setUsername("AlexanderZ");
			display.setToken("API-TOK_4uf3-CsMd-4l2W-1Qq2-07sL");
		} catch (Exception e) {
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
		while(true) {
			double previous = System.currentTimeMillis();
			if(frameCount == 0) {
				model.update();
				add(model.getlView());
				frameCount=10;
				// Send data to the display
				
				try {
					// This array contains for every window (14 rows, 28 columns) three
					// bytes that define the red, green, and blue component of the color
					// to be shown in that window. See documentation of LighthouseDisplay's
					// send(...) method.
					display.sendImage(model.getData());
				} catch (IOException e) {
					System.out.println("Connection failed: " + e.getMessage());
					e.printStackTrace();
			}
			
			}
			frameCount--;
			endgame--;
			double current = System.currentTimeMillis();
			double lag = MS_PER_UPDATE-(current-previous);
			/* For testing purpose, decomment this code.
			if(lag<10) {
				System.out.println(lag);
			}
			*/
			if(lag>0) JTFTools.pause(lag);
			
			if(endgame==0) display.close();
		}
		}
		
	 else {
		/*
		 * Normal high-resolution variant.
		 */
		add(model.getView());
		while(true) {
			double previous2 = System.currentTimeMillis();
			if(frameCount == 0) {
				model.update();//Im Model wird der Controller auch ausgeführt.
				add(model.getView());
				frameCount = 10;
			}
			frameCount--;
			double current2 = System.currentTimeMillis();
			double lag2 = MS_PER_UPDATE-(current2-previous2);
			/* For testing purpose, decomment this code.
			if(lag2<10) {
				System.out.println(lag);
			}
			*/
			if(lag2>0) JTFTools.pause(lag2);
		}
	}
	}

	

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @return the msPerUpdate
	 */
	public static double getMsPerUpdate() {
		return MS_PER_UPDATE;
	}

	/**
	 * @return the fps
	 */
	public static int getFps() {
		return FPS;
	}

	/**
	 * @return the controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * method to control the vacman with keyboard arrows
	 * 
	 * @param e
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		int blockNumHorizontal = View.getBlockNumHorizontal();
		if(model.isLighthouse()) {
			blockNumHorizontal = LighthouseView.getBlockNumHorizontal();
		}

		if (key == KeyEvent.VK_LEFT) {
			direction = -1;
		} else if (key == KeyEvent.VK_UP) {
			direction = -blockNumHorizontal;
		} else if (key == KeyEvent.VK_RIGHT) {
			direction = 1;
		} else if (key == KeyEvent.VK_DOWN) {
			direction = blockNumHorizontal;
		}

	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}
}
