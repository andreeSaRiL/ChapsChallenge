package nz.ac.vuw.ecs.swen225.gp20.application;

/**
 * TO DO:
 * <p>
 * DOING:
 * <p>
 * DONE:
 * > Managing the application window(s) creation of GUI
 * > Starting new games
 * > Loading/saving games
 * > Moving the player
 * > Managing the application window(s) creation of GUI
 * > Use input in managed by this module, this includes mapping key strokes back to objects in the game world, etc.
 */

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.Persistence;

import java.util.Timer;
import java.util.TimerTask;

//import nz.ac.vuw.ecs.swen225.gp20.persistence.Persistence;

/**
 * The Game Class :
 * <p>
 * contains the executable Main method so that the game can be run creates the
 * gui and manages the timer that renders, checks and calls methods depending on
 * a range of conditions. Also responsible for loading the initial level.
 *
 * @author zachkingsford
 */
public class Game {

	// ********************************VARIABLES
	// ETC*******************************************\\

	// Max level time:
	public int INITIAL_LEVEL_TIME;

	// Store the game elemnts:
	private Maze gameMaze;
	public GUI gameGUI;

	// store the level elements:
	private int level = 1;
	public int chipsLeft = 20;

	// store the persistence variable:
	Persistence persistence;

	/**
	 * setup the game object - create element to load and save objects (persistence)
	 * create a board (Maze) alongside managing and setting up the gui (renderer)
	 */
	public Game() {


		persistence = new Persistence(this);
		getPersistence().loadLevels(null, 1);
		gameMaze = new Maze((getPersistence()));
		setGameMaze(gameMaze);
		new GUI(this);

		// Set the time to begin
		boardtimer.schedule(countdownPlayer, 10, 10);
	}

	/**
	 * Method to redisplay the time countdown every second
	 */

	private void updateTime() {
		getGameMaze().timeAdjust();
		gameGUI.iD.repaint();
	}

	/**
	 * return the GUI that relates to this game object
	 *
	 * @return - GUI object that relates to the linked GUI
	 */

	public GUI getGameGUI() {
		return gameGUI;
	}

	// ********************************TIMING*******************************************\\

	/**
	 * return the initial level time
	 *
	 * @return - int the initial level time
	 */
	public int getINITIAL_LEVEL_TIME() {
		return INITIAL_LEVEL_TIME;
	}

	/**
	 * set the inital game time remaining will be done via persitance when the level
	 * is loaded
	 *
	 * @param INITIAL_LEVEL_TIME - time to set
	 */
	public void setINITIAL_LEVEL_TIME(int INITIAL_LEVEL_TIME) {
		this.INITIAL_LEVEL_TIME = INITIAL_LEVEL_TIME;
	}

	/**
	 * return the time remaining as a formatted string
	 *
	 * @return - string max time remaining as a formatted string
	 */
	public String getMAX_LEVEL_TIME_STRING() {
		String value = String.format("%03d", getINITIAL_LEVEL_TIME());

		return value;
	}

	// ********************************GAME
	// TIMER*******************************************\\

    Timer boardtimer = new Timer();
    TimerTask countdownPlayer = new TimerTask() {
        @Override
        public void run() {

            gameGUI.iD.repaint();

            // Auto replay
            if (gameGUI.isReplaying() && gameGUI.getReplayMode().equals("Auto play")) {
                boolean finishedWating = gameGUI.replay.makeAutoMove();

                if (!finishedWating)
                    gameGUI.setIsPlaying(false);
            }

            if (getGameMaze().needsRendering()) {
                gameGUI.bD.repaint();
            }
            if (gameMaze.hasEnded()) {
                gameGUI.displayGameOver();
            }
            if (getGameMaze().isDisplayMessage()) {
                gameGUI.displayHintDialog();
            }
        }
    };

	// ********************************LEVEL*******************************************\\

	/**
	 * return the current level
	 *
	 * @return - an integer value that represents the current level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * set the current level
	 *
	 * @param level - interger value to represent the current level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * return the level as a formatted string
	 *
	 * @return - returns a string
	 */
	public String getLevelAsString() {
		String value = String.format("%03d", getLevel());

		return value;
	}

	// ********************************MAZE + PERSISTANCE +
	// GUI*******************************************\\

	/**
	 * return the current maze object
	 *
	 * @return - a maze object
	 */
	public Maze getGameMaze() {
		return gameMaze;
	}

	/**
	 * return the current persistance object
	 *
	 * @return - a persistance object
	 */
	public Persistence getPersistence() {
		return persistence;
	}

	/**
	 * set the current game GUI
	 *
	 * @param gameGUI - takes a GUI object
	 */
	public void setGameGUI(GUI gameGUI) {
		this.gameGUI = gameGUI;
	}

	/**
	 * set the current game maze
	 *
	 * @param gameMaze - takes a maze object
	 */
	public void setGameMaze(Maze gameMaze) {
		this.gameMaze = gameMaze;
	}

	// **********************************GAME
	// ELEMENTS*****************************************\\

	/**
	 * get the number of chips left to collect.
	 *
	 * @return - returns an interger
	 */
	public int getChipsLeft() {
		return chipsLeft;
	
	}

	/**
	 * Set the number of chips left is called when a level is loaded
	 *
	 * @param chipsLeft - takes an interger that represents the chips left
	 */
	public void setChipsLeft(int chipsLeft) {
		this.chipsLeft = chipsLeft;
	}

	/**
	 * return the chips left as a formatted string
	 *
	 * @return - returns a string
	 */
	public String getChipsLeftAsString() {
		String value = String.format("%03d", getChipsLeft());

		return value;

	}
	// **********************************MAIN METHOD AND POINT OF GAME
	// START*****************************************\\

	/**
	 * Create and initialize a GUI object
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		new Game();
	}
}
