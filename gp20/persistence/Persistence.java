package nz.ac.vuw.ecs.swen225.gp20.persistence;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.swing.JOptionPane;
import nz.ac.vuw.ecs.swen225.gp20.application.Game;
import nz.ac.vuw.ecs.swen225.gp20.maze.FreeTile;
import nz.ac.vuw.ecs.swen225.gp20.maze.InfoFieldTile;
import nz.ac.vuw.ecs.swen225.gp20.maze.KeyTile;
import nz.ac.vuw.ecs.swen225.gp20.maze.LockedDoorTile;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;



/**
 * This class allows loading and saving game levels, game state and recorded game history from and to a file
 *
 * @author Gelan Ejeta
 * @author Elgene Menon Leo Anthony
 */

public class Persistence {

	//current game state
	private Game currentGame;
	private Tile[][] tiles;
	private final int TILE_SIZE;
	private int startingTime;
	private int levelNumber;
	private int chipsLeft;
	private boolean testingMode;
	private JarFile level2Jar; 


	/** Constructor
	 * @param game - current state of the game
	 */
	public Persistence(Game game) {
		this.TILE_SIZE = 32;
		this.currentGame = game;
		testingMode = false;
	}


	
   /**================================== LOADING THE GAME LEVELS AND MOVEMENTS FROM A JSON FILE ==============================*/


	/**
	 * Load game level and stored game state from a JSON file
	 * @param fileName - the name of the file to write to
	 * @param level - the level of the fame to load
	 * @return the state/ level of the game
	 */

	public Tile[][] loadLevels(File fileName, int level)  { 
		
		
		// Initialize 2D array with a FreeTile
		tiles = new Tile[TILE_SIZE][TILE_SIZE];
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length; j++)
				tiles[i][j] = new FreeTile(i, j);
		
		// read saved game states (for replay)  and game levels from a JSON file
		try {
			BufferedReader buffer = null;
			InputStream inputS = null;
			
			if (fileName != null) {
				buffer = new BufferedReader(new FileReader(fileName)); // read saved game
			} else { 
				
				 // read game levels
				if (level > 2 || level < 1) { // check weather the requested level is valid
					JOptionPane.showMessageDialog(null, "It looks like you have finished playing all the levels",
							"Error", JOptionPane.ERROR_MESSAGE);
					return null; 
				} else if (level == 1)
					buffer = new BufferedReader(new FileReader("levels/level" + level + ".json")); // level 1
				else {
					try {
						level2Jar = new JarFile("levels/level2.jar");
						inputS = level2Jar.getInputStream(level2Jar.getEntry("level2.json")); // level 2
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Failed to read level2.json from level2.jar file",
								"File reading error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		
			
			// create JSON reader
			JsonReader jsonReader;
			if (buffer != null)
				jsonReader = Json.createReader(buffer);
			else
				jsonReader = Json.createReader(inputS);
			
			JsonObject jsonObject = jsonReader.readObject();
			
			
			// Extract header information - such as level, timer and number of chipsLeft
			levelNumber = Integer.parseInt(valueParser(jsonObject.get("level")));
			System.out.println("loaded level: " + levelNumber);
			chipsLeft = Integer.parseInt(valueParser(jsonObject.get("chipsLeft")));
			startingTime = Integer.parseInt(valueParser(jsonObject.get("timer")));
			
			
			// extract tile information
			JsonArray tileTypeArray = jsonObject.getJsonArray("tileInfo");
			for (JsonValue array : tileTypeArray) {
				JsonObject tileObject = array.asJsonObject();
				Tile tile = null;

				//get rows and columns
				String tileType = tileObject.getString("tileType");
				int row = Integer.parseInt(valueParser(tileObject.get("row")));
				int col = Integer.parseInt(valueParser(tileObject.get("col")));
				
				//get class name
				String className = tileType.substring(0, 1).toUpperCase() + tileType.substring(1) + "Tile"; 
				
				//construct right class type
				Class<?> TileClass = null;
				try {
					if (level == 1) 
						TileClass = Class.forName("nz.ac.vuw.ecs.swen225.gp20.maze." + className);
					 else 
						TileClass = loadClassesFromJarFile(className);
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Constructing Class for '" + className +"' failed", "Class construction failed", JOptionPane.ERROR_MESSAGE);
				}

				//Instantiate class constructor with proper data type of the class
				try { 
					Constructor<?> TileConstructor;

					if (tileObject.size() == 3) {
						TileConstructor = TileClass.getConstructor(Integer.TYPE, Integer.TYPE);
						tile = (Tile) TileConstructor.newInstance(row, col);
					} else {
						// get the third additional information for InfoFieldTile, LockedDoorTile and KeyTile
						String addtionalInfo = "";
						if (className.startsWith("InfoFieldTile")) {
							addtionalInfo = valueParser(tileObject.get("message"));
						} else
							addtionalInfo = valueParser(tileObject.get("color"));

						TileConstructor = TileClass.getConstructor(Integer.TYPE, Integer.TYPE, String.class);
						tile = (Tile) TileConstructor.newInstance(row, col, addtionalInfo);
					}

				} catch (Exception  e) {
					JOptionPane.showMessageDialog(null, "Error in instantiating '" + className +"' Constructor", "Class construction failed", JOptionPane.ERROR_MESSAGE);
				}
				
			  tiles[row][col] = tile; // update the tiles

			}
			jsonReader.close();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "failed to read the file for level " + level, "Error in reading file", JOptionPane.ERROR_MESSAGE);
		}
	  
	  return tiles; 
	}


	/**
	 * Remove double quote and return JSON objets's value as a string
	 * 
	 * @param object - the object containing JSON information
	 * @return - the value on </obj> as string
	 */
	private String valueParser(Object object) {
		String str = object.toString();
		return str.substring(1, str.length() - 1);
	}

	
	/**
	 * Load class from a jar file - plugin based
	 * 
	 * @param className - the name of the class we are interested in.
	 * @return - new class
	 */
	private Class<?> loadClassesFromJarFile(String className) {
		Class<?> clazz = null;

		try {
			level2Jar = new JarFile("levels/level2.jar");
			Enumeration<JarEntry> entries = level2Jar.entries();

			URL[] urls = { new URL("jar:file:" + "levels/level2.jar" + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);

			while (entries.hasMoreElements()) {
				JarEntry je = entries.nextElement();

				if (je.getName().startsWith(className)) {
					//remove .class from the class name
					String classNames = je.getName().substring(0, je.getName().length() - 6);
					clazz =  cl.loadClass("nz.ac.vuw.ecs.swen225.gp20.maze." + classNames);
				}
			}

		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error in reading  '" + className +"' class name from level.jar", "Reading JAR file failed", JOptionPane.ERROR_MESSAGE);
		}
		
	  return clazz;	
	}
	
	
	/**
	 * Loads recorded game movement from the json file
	 * 
	 * @param fileName - the name of the file containing recored game/movement.
	 * @return a map containing the times and the directions of player's movement.
	 * */
	public TreeMap<Long, String> loadPlayerMovement(File fileName){
		TreeMap<Long, String> playerMovement = new TreeMap<>();
		
		 try {
	            BufferedReader buffer = new BufferedReader(new FileReader(fileName));
	            JsonReader jsonreader = Json.createReader(buffer);
	            JsonObject jsonObject = jsonreader.readObject();
	            JsonArray movesArray = jsonObject.getJsonArray("moves");
	            
	            //check if there is a moves to load first
	            if(movesArray == null) {
	            	JOptionPane.showMessageDialog(null, "There is no player movement to load", "Error - No moves", JOptionPane.ERROR_MESSAGE);
	            	return new TreeMap<>();
	            }
		        
	            for (JsonValue move : movesArray) {
	                JsonObject moveObject = move.asJsonObject();
	                
	                String direction = valueParser(moveObject.get("direction"));
	                long time = Long.parseLong(valueParser(moveObject.get("time")));
	                playerMovement.put(time, direction);
	            }
	        } catch (FileNotFoundException e) {
	        	JOptionPane.showMessageDialog(null, "Error in reading  player movement ", "Reading JAR file failed", JOptionPane.ERROR_MESSAGE);
	        }
	    return playerMovement;
	}
	
	
	
	
	
  /**================================== SAVING THE GAME LEVELS AND MOVEMENTS TO A JSON FILE ==============================*/
	

	/**
	 * Save the current state of the game as a JSON file
	 * 
	 * @param fileName - the name of the file to write it to.
	 * @param append   - specify when to override or add to end of an existing file
	 * @param tiles    - a tiles defining current game state
	 */

	public void saveGame(String fileName, boolean append, Tile[][] tiles) {

		try {
			PrintStream stream = new PrintStream(fileName);
			StringBuilder sb = new StringBuilder();

			// get header information
			if (!testingMode) {
				sb.append("{\n\t\"level\": \"" + currentGame.getGameMaze().getLevel() + '"' 
						+ ",\n" + "\t\"timer\" : \""+ Integer.parseInt(currentGame.getGameMaze().getTimeAsString()) 
						+ "\",\n"+ "\t\"chipsLeft\": \"" + Integer.parseInt(currentGame.getGameMaze().getChipsAsString()) 
						+ '"'+ ",\n" + "\t\"tileInfo\" : [\n\t\t");

			} else {
				sb.append("{\n\t\"level\": \"" + levelNumber + '"' 
						+ ",\n" + "\t\"timer\" : \"" + 100 + "\",\n"
						+ "\t\"chipsLeft\": \"" + 11 + '"' 
						+ ",\n" + "\t\"tileInfo\" : [\n\t\t");
			}
			

			String tileInfo = null;
			for (int row = 0; row < tiles.length; row++) {
				for (int col = 0; col < tiles[row].length; col++) {

					// Get tile information
					Tile tile = tiles[row][col];
					tileInfo = buildTileInfoAsJSONFormat(tile, row, col);
					sb.append(tileInfo);
				}
				sb.append("\n\t\t");
			}

			sb.deleteCharAt(sb.toString().length()-5); //remove the last ','

			if(!append) sb.append("\n\t]}"); //write as a new file

			stream.print(sb.toString()); //write to the file
			stream.close();
		}catch (FileNotFoundException e) {
			throw new Error("saving the game as a JSON file was failed.");
		}
	}
	
	
	/**
	 * Used for recording the player's movement and times between each
	 * movements/actions
	 * 
	 * @param fileName  - the name of the file to write to
	 * @param direction - player's movement direction
	 * @param time      - the time between each player's movements
	 * @param isMoved   - monitor the player's first movement.
	 */

	public void savePlayerMovement (String fileName, String direction, long time, boolean isMoved){
		try {
			StringBuilder moveRecord = new StringBuilder();
			FileWriter fileWriter = new FileWriter(fileName, true);
			BufferedWriter outBuffer = new BufferedWriter(fileWriter);
			
			//start appending moves to the file
			if (isMoved) 
				moveRecord.append("],\n\t\"moves\" : [\n ");

			if (!isMoved) //continue appending the moves to the file on the new line
				moveRecord.append(",\n");
			
			moveRecord.append("\t\t\t{ \"type\": \""+ "playerMovement" +"\", " + "\"direction\" : \"" + direction + "\", " + "\"time\" : \"" + time + "\"}");

			outBuffer.append(moveRecord.toString());
			outBuffer.close();
			fileWriter.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build tile information/description as a JSON file format
	 * 
	 * @param tile - tile at the given position
	 * @param row  - the x position of the given </tile>
	 * @param col  - the y position of the given </tile>
	 * @return the tile information as a JSON file format
	 */
	private String buildTileInfoAsJSONFormat(Tile tile, int row, int col) {
		String additionalInfo = null;
		boolean isColor = false;
		
		//check if the tile as extra informations like color or message
		if (tile instanceof KeyTile) { additionalInfo = ((KeyTile) tile).getColour(); isColor = true;}
		else if (tile instanceof InfoFieldTile) additionalInfo = ((InfoFieldTile) tile).getMessage();
		else if (tile instanceof LockedDoorTile) { additionalInfo = ((LockedDoorTile) tile).getColour(); isColor = true;}

		if(additionalInfo != null && isColor)
			return  "{\"tileType\" : \"" +  tile.toString() + "\", \"row\" : \"" + row +  "\", \"col\" : \"" + col + "\", \"color\" : \"" + additionalInfo + "\"}, ";
		else if(additionalInfo != null && !isColor)
			return "{\"tileType\" : \"" +  tile.toString() + "\", \"row\" : \"" + row +  "\", \"col\" : \"" + col + "\", \"message\" : \"" + additionalInfo + "\"}, ";
		else return "{\"tileType\" : \"" +  tile.toString() + "\", \"row\" : \"" + row +  "\", \"col\" : \"" + col + "\"}, ";
	}

	
	
	/** ===============  FOR TESTING - GETTERS AND SETTERS ===================== */ 
	
	

	/**
	 * Sets </isTestinng> to true or false based on user's option
	 * 
	 * @param testing - users option - true or false
	 */
	public void setTesting(boolean testing) {
		testingMode = testing;
	}

	/**
	 * @return the tiles loaded from a file
	 */
	public Tile[][] getTiles() {
		return tiles;
	}

	/**
	 * @return - the starting time of the game
	 */
	public int getStartingTime() {
		return this.startingTime;
	}
	

	/**
	* @return Return true if the package is on testing , false otherwise
	 */
	public boolean getTesting() {
		return testingMode;
	}

	
	/**
	 * @return - return the number of chips left
	 */

	public int getChispsLeft() {
		return chipsLeft;
	}

	/**
	 * @return - current level number
	 */
	public int getLevelNumber() {
		return levelNumber;
	}
}
