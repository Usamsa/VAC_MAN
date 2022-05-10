package projectVacMan;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Model {
	/**
	 * If the lighthouseView shall be selected, then this variable must be true
	 */
	private boolean lighthouse = true;//SET TRUE FOR LIGHTHOUSE VARIANT
	private byte[] data;
	/**
	 * GameLoop and View are structures.
	 */
	private GameLoop gl;
	private View view = new View(this);
	private LighthouseView lView = new LighthouseView(this);
	/**
	 * Game States
	 */
	private int hearts = 3;
	private int score = 0;

	/**
	 * Positions of the gameobjects, to be precise: Pacman and the ghosts.
	 */
	private int p_pac = getPosition(4);
	private int[] ghost = { getPosition(5), getPosition(6), getPosition(7), getPosition(8) };

	/**
	 * Positions of diverse objects: small coins, big coins. Each item needs an
	 * array with the positions where these items were collected.
	 */
	private int[] coin = getCoin(1);
	ArrayList<Integer> emptyCoin = new ArrayList<Integer>();
	private int[] bigCoin = getCoin(2);
	ArrayList<Integer> emptybigCoin = new ArrayList<Integer>();

	/**
	 * Array for standard empty fields. Only useful at the beginning.
	 */
	private int[] nothingness = getNothing();

	public Model(GameLoop gl) {
		this.gl = gl;
	}

	/**
	 * @param code for the ghosts/vacman
	 * @return the position of the selected code
	 */
	private int getPosition(int code) {
		int position = -1;
		for (int i = 0; i < chooseMap().length; i++) {
			if (chooseMap()[i] == code) {
				position = i;
				return position;
			}
		}
		return position;
	}
	private boolean nextLevel() {
		if ((coin.length == emptyCoin.size()) && (bigCoin.length == emptybigCoin.size())) {
			score = score + 50;
			if(hearts < 3) {
				hearts++;
			}
			if(isLighthouse()) {
				lView.setDynamicMapArray(chooseMap().clone());
			} else {
				view.setDynamicMapArray(chooseMap().clone());
			}
			gl.setDirection(0);
			p_pac = getPosition(4);
			for (int i = 5; i < 9; i++) {
				ghost[i - 5] = getPosition(i);
			}
			coin = getCoin(1);
			emptyCoin = new ArrayList<Integer>();
			bigCoin = getCoin(2);
			emptybigCoin = new ArrayList<Integer>();
			if(isLighthouse()) {
				lView.updateView();
			} else {
				view.updateView();
			}
			return true;
		}
		return false;
	}
	/**
	 * start position of the level/chooseDynamicMap() reset
	 * controller.getDirection() to 0
	 */
	private void start_position() {
		if(isLighthouse()) {
			lView.setDynamicMapArray(chooseMap().clone());
		} else {
			view.setDynamicMapArray(chooseMap().clone());
		}
		for (int i : emptyCoin) {
			chooseDynamicMap()[i] = 3;
		}
		for (int i : emptybigCoin) {
			chooseDynamicMap()[i] = 3;
		}
		p_pac = getPosition(4);
		for (int i = 5; i < 9; i++) {
			ghost[i - 5] = getPosition(i);
		}
		gl.setDirection(0);
		if(isLighthouse()) {
			lView.updateView();
			changeData();
		} else {
			view.updateView();
		}
		
	}

	/**
	 * reseting all variables and the chooseDynamicMap() if vacman dies.
	 */

	private void reset() {
		if(isLighthouse()) {
			lView.setDynamicMapArray(chooseMap().clone());
		} else {
			view.setDynamicMapArray(chooseMap().clone());
		}
		gl.setDirection(0);
		hearts = 3;
		score = 0;
		p_pac = getPosition(4);
		for (int i = 5; i < 9; i++) {
			ghost[i - 5] = getPosition(i);
		}
		coin = getCoin(1);
		emptyCoin = new ArrayList<Integer>();
		bigCoin = getCoin(2);
		emptybigCoin = new ArrayList<Integer>();
		changeData();
	}

	/**
	 * method to get the position of the used coin in the coin-array and putting it
	 * into the new coin2-array-chooseDynamicMap() to know which coin is used
	 * 
	 * @param code 1 for a normal coin, code 2 for a big coin
	 * @return the new coin-chooseDynamicMap()
	 */
	private int[] getCoin(int code) {
		ArrayList<Integer> coin = new ArrayList<Integer>();
		for (int i = 0; i < chooseMap().length; i++) {
			if (chooseMap()[i] == code) {
				coin.add(i);
			}
		}
		int[] coin2 = new int[coin.size()];
		for (int i = 0; i < coin.size(); i++) {
			coin2[i] = coin.get(i);
		}
		return coin2;
	}

	/**
	 * This method is like the coin method, but for the fields which are black.
	 * 
	 */
	private int[] getNothing() {
		int[] startNothing = getCoin(3);
		int[] nothing = new int[startNothing.length + 5];
		for (int i = 0; i < nothing.length - 5; i++) {
			nothing[i] = startNothing[i];
		}
		for (int i = 0; i < 5; i++) {
			nothing[nothing.length - 5 + i] = getPosition(4 + i);
		}
		return nothing;
	}

	/**
	 * method for death. If you die you will get -1 heart until you have no hearts
	 * left. If you have no hearts left the game resets.
	 */
	private void death() {
		hearts--;
		if (hearts == 0) {
			reset();
		}
		start_position();
	}

	/**
	 * method to update the chooseDynamicMap(). if pacman collides with ghost
	 * : -1 heart if pacman gets a coin : set code 1 to code 3 if pacman moves : set
	 * location of pacman from code X to code 4 if ghosts move : set location of
	 * ghostX from code X to code 5/6/7/8
	 */
	public void update() {
		// To make the view update more efficient:

		int[] changes = { p_pac, ghost[0], ghost[1], ghost[2], ghost[3], 0, 0, 0, 0, 0 }; // 10: old pacman/ghost
																							// position(5) + new
																							// positions(5).

		for (int i = 0; i < 4; i++) {
			if (p_pac == ghost[i]) {
				death();
				return;
			}
		}
		for (int i : coin) {
			chooseDynamicMap()[i] = 1;
		}
		for (int i : bigCoin) {
			chooseDynamicMap()[i] = 2;
		}
		for (int i : emptyCoin) {
			chooseDynamicMap()[i] = 3;
		}
		for (int i : emptybigCoin) {
			chooseDynamicMap()[i] = 3;
		}

		pacman_move();
		if(nextLevel() == true) {
			return;
		}
		for (int i = 0; i < 4; i++) {
			if (p_pac == ghost[i]) {
				death();
				return;
			}
		}
		ghost_move();
		for (int i = 0; i < 4; i++) {
			if (p_pac == ghost[i]) {
				death();
				return;
			}
		}
		for (int i : nothingness) {
			chooseDynamicMap()[i] = 3;
		}
		chooseDynamicMap()[p_pac] = 4;
		chooseDynamicMap()[ghost[0]] = 5;
		chooseDynamicMap()[ghost[1]] = 6;
		chooseDynamicMap()[ghost[2]] = 7;
		chooseDynamicMap()[ghost[3]] = 8;

		changes[5] = p_pac;
		changes[6] = ghost[0];
		changes[7] = ghost[1];
		changes[8] = ghost[2];
		changes[9] = ghost[3];

		if(isLighthouse()) {
			lView.updateView(changes);
			changeData();
		} else {
			view.updateView(changes);
		}

	}

	/**
	 * method to randomize the movement of the ghosts. if ghosts next position is
	 * not on a 0: move further.
	 */
	private void ghost_move() {
		int blockNumHorizontal = view.getBlockNumHorizontal();
		if(isLighthouse()) {
			blockNumHorizontal = lView.getBlockNumHorizontal();
		}
		for (int i = 0; i < 4; i++) {
			int next_position = ghost[i];
			switch ((int) (Math.random() * 4)) {
			case 0:
				next_position = next_position - 1;
				break;
			case 1:
				next_position = next_position - blockNumHorizontal;
				break;
			case 2:
				next_position = next_position + 1;
				break;
			case 3:
				next_position = next_position + blockNumHorizontal;
				break;
			}
			if (chooseDynamicMap()[next_position] != 0) {
				ghost[i] = next_position;
			}
		}
	}

	/**
	 * method to control the vacman with the keyboard arrows if his next position is
	 * a code 0: stand still if his next position is a code 1: +1 score if his next
	 * position is a code 2: +2 score
	 */
	private void pacman_move() {
		if (gl.getDirection() == 0) {
			return;
		}
		int next_position = p_pac + gl.getDirection();
		if (chooseDynamicMap()[next_position] != 0) {
			p_pac = next_position;
			if (chooseDynamicMap()[next_position] == 1) {
				emptyCoin.add(next_position);
				score++;
			} else if (chooseDynamicMap()[next_position] == 2) {
				emptybigCoin.add(next_position);
				score = score + 2;
			}
		}
	}

/**
 * This method updates the whole view by setting up the data array for
 * the lighthouse.
 */
public void changeData() {
	byte[] data = new byte[lView.getBlockNumVertical()*lView.getBlockNumHorizontal()*3];
	for(int mapIndex=0;mapIndex<lView.getDynamicMapArray().length;mapIndex++) {
			int[] color = decideColor(lView.getDynamicMapArray()[mapIndex]);
			data[mapIndex*3 + 0] = (byte) color[0];
			data[mapIndex*3 + 1] = (byte) color[1];
			data[mapIndex*3 + 2] = (byte) color[2];
		
	}
	 this.data = data;
	 
}
private int[] decideColor(int code) {
	/*
	 * 0=Wall, 1=Coin, 2=Big Coin, 3= black matter aka nothing, 4=Pacman, 5=ghost1,
	 * 6=ghost2, 7=ghost3, 8=ghost4
	 */
	switch(code) {
	case 0: return new int[] {0,0,255}; 
	case 1: return new int[] {230,165,53}; 
	case 2: return new int[] {255,128,0}; 
	case 3: return new int[] {0,0,0}; 
	case 4: return new int[] {255,255,0}; 
	case 5: return new int[] {204, 0, 204}; 
	case 6: return new int[] {255, 51, 51}; 
	case 7: return new int[] {102, 255, 255}; 
	case 8: return new int[] {0, 255, 0}; 
	default:return new int[] {0,0,0}; 
	}

}
	
	/**
	 * Chooses the constant map of the activated view.
	 * @return the map
	 */
	private int[] chooseMap() {
		if(isLighthouse()) {
			return lView.getMapArray();
		}
		else { 
			return view.getMapArray();
		}
	}
	/**
	 * Chooses the dynamic map of the activated view.
	 * @return the dynamic map
	 */
	private int[] chooseDynamicMap() {
		if(isLighthouse()) {
			return lView.getDynamicMapArray();
		}
		else { 
			return view.getDynamicMapArray();
		}
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}
	

	/**
	 * @return the lView
	 */
	public LighthouseView getlView() {
		return lView;
	}

	/**
	 * @return the hearts
	 */
	public int getHearts() {
		return hearts;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return the lighthouse
	 */
	public boolean isLighthouse() {
		return lighthouse;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	

}
