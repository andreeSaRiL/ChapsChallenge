package nz.ac.vuw.ecs.swen225.gp20.recordReplay;
import nz.ac.vuw.ecs.swen225.gp20.application.Game;
import nz.ac.vuw.ecs.swen225.gp20.maze.ChapTile;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;
import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Replay from the recorded file Replay in auto mode according to the selected
 * speed Replay in step by step mode where the chap moves one step each time
 * 
 * @author Gelan Ejeta
 * @author Elgene Menon Leo Anthony
 */
public class Replay {

	private Game game;
	private TreeMap<Long, String> moves = new TreeMap<Long, String>(); // stores the player movement from the load file
	private File replyFile = new File("SavedGameFile/recordedGame.json");
	private ChapTile chap;
	private List<String> moveDirections; // a list of the player movement from the recorded file
	String replayMode;

	/**
	 * Constructor of the replay game
	 * 
	 * @param game takes from the Game class
	 */
	public Replay(Game game) {
		this.game = game;
	}

	/**
	 * The process of replay from the recorded file Tracks the Chap tile and moves
	 * according to the direction
	 */
	public void replay() {

		replayMode = game.gameGUI.getReplayMode();
		// loads the entire game from the recorded file
		Tile[][] replayTile = game.getPersistence().loadLevels(replyFile, game.getLevel());
		// tracks the player movement from the recorded file
		moves = game.getPersistence().loadPlayerMovement(replyFile);


		for (int i = 0; i < replayTile.length; i++) {
			for (int j = 0; j < replayTile[0].length; j++) {
				game.getGameMaze().setTileAt(i, j, replayTile[i][j]); // Set game maze according the chap tile
				if (replayTile[i][j] instanceof ChapTile) {
					chap = (ChapTile) replayTile[i][j]; // Takes the current position of the chap
				}
			}
		}

		game.getGameMaze().setChap(chap); // set the chap in the maze to keep track on the tile
		moveDirections = new ArrayList<>(); // Stores the entire player movements

		if (!moves.isEmpty()) { // checks whether the player movement is not empty
			moves.forEach((k, v) -> moveDirections.add(v));
			
		} else {
			return; // return when there is no player movement
		}
	
	}
	/**
	 * The process of step by step replay
	 * Make sure that the player looks at the right direction
	 */
	public void makeStepByStepMove() {
		if (moveDirections.size() >=1 && replayMode != null && replayMode.equals("step by step replay")) {
			
			String currentMoveDic = moveDirections.remove(0);
			game.getGameGUI().setDirection(currentMoveDic); //the player looks at the same direction
			game.getGameMaze().move(currentMoveDic);
		} else
			JOptionPane.showMessageDialog(null, "No more steps left", "Next Step", JOptionPane.PLAIN_MESSAGE);
	}


	/**
	 * The process of auto replay
	 * Check if the player looks at right direction
	 * @return the movement without any interruptions
	 */
	public boolean makeAutoMove() {
		boolean isMoved = false;

		if (moveDirections.size() >=1 && replayMode != null && replayMode.equals("Auto play")) {
			timeElapsed(); // waiting time
			
			String currentMoveDic = moveDirections.remove(0);
			game.getGameGUI().setDirection(currentMoveDic); //the player looks at the same direction
			game.getGameMaze().move(currentMoveDic);
			
			isMoved = true;
		} else {
			isMoved = false;
			JOptionPane.showMessageDialog(null, "Finished replaying Auto mode", "Finished Auto replay",
					JOptionPane.PLAIN_MESSAGE);
		}
		return isMoved;
	}

	/**
	 * Calculate the delay time for replaying - to make smooth movement
	 */
	private void timeElapsed() {
		double displaySpeed = game.getGameGUI().getReplaySpeed();
		double startTime = Double.parseDouble(game.getGameMaze().getTimeAsString());
		double oldCurrentTime = startTime;

		while ((oldCurrentTime - startTime) < 1) {
			oldCurrentTime += displaySpeed;
		}
	}

}
