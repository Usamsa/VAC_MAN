package projectVacMan;

import java.awt.Color;
import java.awt.Font;

import acm.graphics.GArc;
import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GPoint;
import acm.graphics.GPolygon;
import acm.graphics.GRect;

/**
 * This is the High-Resolution View.
 *
 */
public class LighthouseView extends GCompound {
	/**
	 * The model which points to this view.
	 */
	private Model model;
	/**
	 * Block size is the pixel width(height) of one block.
	 */
	private static int blockSize = 30;
	/**
	 * blockNumHorizontal is the number of blocks in a row
	 */
	private static int blockNumHorizontal = 28;
	/**
	 * blockNumVertical is the number of blocks in a column
	 */
	private static int blockNumVertical = 14;

	/**
	 * Map Array is the map at the beginning.
	 */
	private final int mapArray[] = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
			0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
			0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
			0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0,
			0, 1, 1, 1, 1, 1, 1, 0, 3, 3, 3, 3, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
			0, 1, 1, 1, 1, 1, 1, 3, 3, 5, 6, 3, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0,
			0, 1, 0, 0, 1, 1, 1, 3, 3, 8, 7, 3, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0,
			0, 1, 1, 1, 1, 1, 1, 0, 3, 3, 3, 3, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
			0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0,
			0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
			0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	/**
	 * Dynamic map is the map that gets regularly updated.
	 */
	private int[] dynamicMapArray = mapArray.clone();
	/**
	 * The hearts to display.
	 */
	private GPolygon heart1;
	private GPolygon heart2;
	private GPolygon heart3;

	/**
	 * Score information. Needs counter to only update when necessary.
	 */
	private GLabel scoreLabel;
	private int viewScore = 0;;

	/**
	 * Constructor for view takes the model to get information about game states.
	 * Also initially draws the map.
	 * 
	 * @param model model of this MVC pattern.
	 */
	public LighthouseView(Model model) {
		this.model = model;
		drawStartMap();
	}

	/**
	 * Updates the View based on the current dynamic map state.
	 */
	public void updateView() {
		for (int i = 0; i < dynamicMapArray.length; i++) {
			if (dynamicMapArray[i] != 0) {
				decideObject(i);
			}
		}
		updateHearts();
		updateScore();
	}

	/**
	 * Better version of updateView, only draws object on positions where some
	 * action might happened.
	 * 
	 * @param changes array of positions where objects might have moved.
	 */
	public void updateView(int[] changes) {
		// TESTING double previous = System.currentTimeMillis();

		for (int i : changes) {
			if (dynamicMapArray[i] != 0) {
				decideObject(i);
			}
		}
		updateHearts();
		updateScore();

		/*
		 * TESTING PURPOSE double current = System.currentTimeMillis(); double lag =
		 * (current-previous); if(lag>10) { System.out.println(lag); }
		 */
	}

	/**
	 * Is here to draw the map at the start of the game. Is another method because
	 * it includes drawing a background.
	 */
	private void drawStartMap() {
		GRect background = new GRect((blockNumHorizontal) * blockSize, (blockNumVertical + 2) * blockSize);
		background.setFilled(true);
		add(background);
		for (int i = 0; i < mapArray.length; i++) {
			decideObject(i);
		}
		addHeartsInfo();
		addScoreInfo();
	}

	/**
	 * This method determines and draws the object depending on its position in the
	 * map.
	 * 
	 * @param position position in the map
	 */
	private void decideObject(int position) {
		GPoint point = decideCoordinates(position);
		double x = point.getX();
		double y = point.getY();
		int code = dynamicMapArray[position];
		GRect nothing = new GRect(x, y, blockSize, blockSize);
		nothing.setFilled(true);
		nothing.setFillColor(Color.BLACK);
		add(nothing);
		/*
		 * 0=Wall, 1=Coin, 2=Big Coin, 3= black matter aka nothing, 4=Pacman, 5=ghost1,
		 * 6=ghost2, 7=ghost3, 8=ghost4
		 */
		if (code == 0) {
			GRect wall = new GRect(x, y, blockSize, blockSize);
			wall.setFilled(true);
			wall.setFillColor(Color.BLUE);
			add(wall);
		}
		if (code == 1) {
			int coinSize = (int) (blockSize / 4.0);
			double coinDisplacement = blockSize / 4.0 * 1.5;
			GArc coin = new GArc(x + coinDisplacement, y + coinDisplacement, coinSize, coinSize, 0, 360);
			coin.setFilled(true);
			coin.setColor(Color.ORANGE);
			coin.setFillColor(Color.ORANGE);
			add(coin);
		}
		if (code == 2) {
			int coinSize = (int) (blockSize / 1.5);
			GArc coin = new GArc(x + blockSize / 4.0, y + blockSize / 4.0, coinSize, coinSize, 0, 360);
			coin.setFilled(true);
			coin.setColor(Color.ORANGE);
			coin.setFillColor(Color.ORANGE);
			add(coin);
		}
		if (code == 3) {
			// DO NOTHING BECAUSE NOTHING GETS ADDED ANYWAY
		}
		if (code == 4) {
			GArc pacman = new GArc(x, y, blockSize, blockSize, 0, 360);
			pacman.setFilled(true);
			pacman.setFillColor(Color.YELLOW);
			pacman.setColor(Color.YELLOW);
			add(pacman);
		}
		if (code == 5 || code == 6 || code == 7 || code == 8) {
			GPoint[] triangle = { new GPoint(x + blockSize / 2.0, y), new GPoint(x, y + blockSize),
					new GPoint(x + blockSize, y + blockSize), new GPoint(x + blockSize / 2.0, y) };
			GPolygon ghost = new GPolygon(triangle);
			ghost.setFilled(true);
			Color ghostColor = Color.GREEN;
			switch (code) {
			case 5:
				ghostColor = new Color(204, 0, 204);
				break;// Purple
			case 6:
				ghostColor = new Color(255, 51, 51);
				break;// Red
			case 7:
				ghostColor = new Color(102, 255, 255);
				break;// Cyan
			case 8:
				ghostColor = new Color(0, 255, 0);
				break;// Green
			}
			ghost.setFillColor(ghostColor);
			add(ghost);
		}
	}

	/**
	 * This method decides the true pixel coordinates for an object on the map and
	 * is set as the upper left bound.
	 * 
	 * @param position position on the map
	 * @return the GPoint data which holds information about the pixel coordinates.
	 */
	private GPoint decideCoordinates(int position) {
		GPoint coordinates = new GPoint(blockSize * (position % blockNumHorizontal),
				blockSize * (position / blockNumHorizontal));
		return coordinates;
	}

	/**
	 * This method adds the score to the view. TODO: Refresh must be adjusted so
	 * only the score gets changed.
	 */
	private void addScoreInfo() {
		GPoint infoBoardPoints = decideCoordinates(mapArray.length);
		double x = infoBoardPoints.getX();
		double y = infoBoardPoints.getY();
		/*
		 * In the following, the score gets added based on the current game score.
		 */
		scoreLabel = new GLabel("Score:" + " " + viewScore, x, y + blockSize);
		scoreLabel.setColor(Color.WHITE);
		scoreLabel.setFont(new Font("SansSerif", Font.BOLD, blockSize));
		add(scoreLabel);
	}

	/**
	 * This method updates the score information only if necessary, so when a change
	 * happened.
	 */
	private void updateScore() {
		int currentScore = model.getScore();
		if (viewScore != currentScore) {
			scoreLabel.setLabel("Score: " + currentScore);
			viewScore = currentScore;
		}
	}

	/**
	 * This method adds heart information to the view at the start of the program.
	 */
	private void addHeartsInfo() {
		/*
		 * In the following, hearts get added. They get filled based on the hearts
		 * counter.
		 */
		int hearts = 3;
		GPoint heartCoordinates = decideCoordinates(mapArray.length - 2 + blockNumHorizontal);
		double hx = heartCoordinates.getX();
		double hy = heartCoordinates.getY();
		GPoint[] heartPoints = { new GPoint(hx + blockSize, hy + 2 * blockSize), new GPoint(hx, hy + blockSize),
				new GPoint(hx, hy + (int) (blockSize * 0.5)), new GPoint(hx + (int) (blockSize * 0.5), hy),
				new GPoint(hx + blockSize, hy + (int) (blockSize * 0.5)), new GPoint(hx + (int) (blockSize * 1.5), hy),
				new GPoint(hx + blockSize * 2, hy + (int) (blockSize * 0.5)),
				new GPoint(hx + 2 * blockSize, hy + blockSize), new GPoint(hx + blockSize, hy + 2 * blockSize), };
		heart1 = new GPolygon(heartPoints);
		heart2 = new GPolygon(heartPoints);
		heart3 = new GPolygon(heartPoints);
		GPolygon[] heartArray = { heart1, heart2, heart3 };
		for (int b = 0; b < heartArray.length; b++) {
			heartArray[b].setColor(Color.RED);
			if (hearts == 3) {
				heartArray[b].setFilled(true);
				heartArray[b].setFillColor(Color.RED);
			} else if (hearts == 2) {
				if (b > 0) {
					heartArray[b].setFilled(true);
					heartArray[b].setFillColor(Color.RED);
				}
			} else if (hearts == 1) {
				if (b > 1) {
					heartArray[b].setFilled(true);
					heartArray[b].setFillColor(Color.RED);
				}
			}
			add(heartArray[b]);
			heartArray[b].move(-2 * blockSize * (2 - b), 0);
		}
	}

	/**
	 * This methods only updates the heart information displayed, if the status of
	 * the hearts has changed, thus making the program more efficient.
	 */
	private void updateHearts() {
		int modelHearts = model.getHearts();
		if (modelHearts != currentHearts()) {
			heart1.setFilled(true);
			heart2.setFilled(true);
			heart3.setFilled(true);
			if (modelHearts <= 2) {
				heart1.setFilled(false);
			}
			if (modelHearts <= 1) {
				heart2.setFilled(false);
			}
			if (modelHearts == 0) {
				heart3.setFilled(false);
			}
		}
	}

	/**
	 * This method counts, how many of the displayed hearts are filled, thus giving
	 * information about the current heart status.
	 * 
	 * @return current display hearts info
	 */
	private int currentHearts() {
		GPolygon[] heartsArray = { heart1, heart2, heart3 };
		int count = 0;
		for (int i = 0; i < heartsArray.length; i++) {
			if (heartsArray[i].isFilled()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return the blockNumHorizontal
	 */
	public static int getBlockNumHorizontal() {
		return blockNumHorizontal;
	}

	/**
	 * @return the blockNumVertical
	 */
	public static int getBlockNumVertical() {
		return blockNumVertical;
	}

	/**
	 * @return the dynamicMapArray
	 */
	public int[] getDynamicMapArray() {
		return dynamicMapArray;
	}

	/**
	 * @param dynamicMapArray the dynamicMapArray to set
	 */
	public void setDynamicMapArray(int[] dynamicMapArray) {
		this.dynamicMapArray = dynamicMapArray;
	}

	/**
	 * @return the mapArray
	 */
	public int[] getMapArray() {
		return mapArray;
	}

	/**
	 * @return the blockSize
	 */
	public static int getBlockSize() {
		return blockSize;
	}

}