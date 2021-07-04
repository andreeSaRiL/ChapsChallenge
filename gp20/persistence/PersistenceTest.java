//package nz.ac.vuw.ecs.swen225.gp20.persistence;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.*;
//import java.util.TreeMap;
//import org.junit.jupiter.api.Test;
//
//import nz.ac.vuw.ecs.swen225.gp20.application.Game;
//import nz.ac.vuw.ecs.swen225.gp20.maze.BombTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.ChapTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.EnemyTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.ExitLockTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.ExitTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.FreeTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.InfoFieldTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.KeyTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.LockedDoorTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.ShieldTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.TreasureTile;
//import nz.ac.vuw.ecs.swen225.gp20.maze.WallTile;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
///**
// * JUnit Test for persistence package
// * 
// * @author Gelan Ejeta
// * 
// */
//public class PersistenceTest {
//
//	// initialize persistence
//	Persistence persistence = new Persistence(null);
//	Tile[][] tiles;
//	 
//
//	/**
//	 * Test loading game level from the JSON file
//	 */
//	@Test
//	public void loadLevel1FromJson() {
//		
//		//change loading to a testing mode
//		persistence.setTesting(true);
//
//		// level 1
//		int level = 1;
//		tiles = persistence.loadLevels(null, level);
//
//		// check tiles
//		assertTrue(tiles[19][20] instanceof KeyTile);
//		assertTrue(tiles[7][15] instanceof ExitTile);
//		assertTrue(tiles[31][31] instanceof FreeTile);
//		assertTrue(tiles[21][11] instanceof WallTile);
//		assertTrue(tiles[19][15] instanceof ChapTile);
//		assertTrue(tiles[8][15] instanceof ExitLockTile);
//		assertTrue(tiles[23][16] instanceof TreasureTile);
//		assertTrue(tiles[17][15] instanceof InfoFieldTile);
//		assertTrue(tiles[16][16] instanceof LockedDoorTile);
//
//		// check level, timer and chispLeft
//		assertEquals(level, persistence.getLevelNumber());
//		assertEquals(100, persistence.getStartingTime());
//		assertEquals(11, persistence.getChispsLeft());
//	}
//
//	/**
//	 * TestLoading level 2 from jar file
//	 */
//	@Test
//	public void loadLeve21FromJson() {
//		
//		// change loading to a testing mode
//		persistence.setTesting(true);
//
//		// level 1
//		int level = 2;
//		persistence.loadLevels(null, level);
//		tiles = persistence.getTiles();
//
//		// check tiles
//		assertTrue(tiles[0][0] instanceof FreeTile);
//		assertTrue(tiles[16][9] instanceof BombTile);
//		assertTrue(tiles[12][10] instanceof KeyTile);
//		assertTrue(tiles[10][15] instanceof ExitTile);
//		assertTrue(tiles[11][14] instanceof WallTile);
//		assertTrue(tiles[14][15] instanceof ChapTile);
//		assertTrue(tiles[16][17] instanceof EnemyTile);
//		assertTrue(tiles[20][14] instanceof ShieldTile);
//		assertTrue(tiles[11][15] instanceof ExitLockTile);
//		assertTrue(tiles[14][13] instanceof TreasureTile);
//		assertTrue(tiles[13][15] instanceof InfoFieldTile);
//		assertTrue(tiles[12][18] instanceof LockedDoorTile);
//
//		// check level, timer and chispLeft
//		assertEquals(level, persistence.getLevelNumber());
//		assertEquals(100, persistence.getStartingTime());
//		assertEquals(11, persistence.getChispsLeft());
//	}
//
//	/**
//	 * Test saving and Loading Game state to and from a JSON file
//	 */
//	@Test
//	public void savingAndLoadingGameStateTest() {
//		
//		// change loading to a testing mode
//		persistence.setTesting(true);
//
//		// load level
//		persistence.loadLevels(null, 1);
//		tiles = persistence.getTiles();
//		tiles[31][31] = new ChapTile(31, 31);
//		tiles[0][0] = new KeyTile(0, 0, "red");
//
//		// save it
//		String fileName = "SavedGameFile/saveGame.json";
//		persistence.saveGame(fileName, false, tiles);
//
//		// check the file is not empty
//		File saveFileName = new File(fileName);
//		assertTrue(saveFileName.exists() && saveFileName.length() != 0);
//
//		// load back the game in
//		Tile[][] savedTiles = persistence.loadLevels(new File(fileName), 1);
//		assertEquals(tiles[0][0] instanceof ChapTile, savedTiles[0][0] instanceof ChapTile);
//		assertEquals(tiles[31][31] instanceof KeyTile, savedTiles[31][31] instanceof KeyTile);
//		assertEquals(tiles[20][20] instanceof WallTile, savedTiles[20][20] instanceof WallTile);
//	}
//
//	/**
//	 * Test saving and loading player Movement to and from a file
//	 */
//	@Test
//	public void savingAndLoadingPlayerMovement() {
//
//		// change loading to a testing mode
//		persistence.setTesting(true);
//
//		// load level
//		persistence.loadLevels(null, 1);
//		tiles = persistence.getTiles();
//
//		// save the game state
//		String fileName = "SavedGameFile/recordedGame.json";
//		persistence.saveGame(fileName, true, tiles); 
//
//		// Save player movement
//		persistence.savePlayerMovement(fileName, "L", 001, true);
//		persistence.savePlayerMovement(fileName, "R", 002, false);
//		persistence.savePlayerMovement(fileName, "D", 003, false);
//		persistence.savePlayerMovement(fileName, "U", 004, false);
//
//		try { // append the last closing ]} brackets to a recordedGame.json
//			StringBuilder move = new StringBuilder();
//			FileWriter fileWriter = new FileWriter(fileName, true);
//			BufferedWriter out = new BufferedWriter(fileWriter);
//			move.append("\n\t]}  ");
//			out.append(move.toString());
//			out.close();
//			fileWriter.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		// check recordedGame file is not empty
//		File saveFileName = new File(fileName);
//		assertTrue(saveFileName.exists() && saveFileName.length() != 0);
//
//		// load player moves back
//		TreeMap<Long, String> playerMoves = new TreeMap<Long, String>();
//		playerMoves = persistence.loadPlayerMovement(new File(fileName));
//
//		// extract the direction and times
//		List<String> directions = new ArrayList<>();
//		List<Long> times = new ArrayList<>();
//		playerMoves.forEach((k, v) -> times.add(k));
//		playerMoves.forEach((k, v) -> directions.add(v));
//
//		// test the directions
//		assertTrue(directions.contains("U"));
//		assertTrue(directions.contains("D"));
//		assertTrue(directions.contains("L"));
//		assertTrue(directions.contains("R"));
//		assertFalse(directions.contains("M"));
//
//		// Test times
//		for (int i = 0; i < times.size(); i++)
//			assertTrue(times.get(i) == 00 + i + 1);
//
//		persistence.setTesting(false);
//	}
//
//}
