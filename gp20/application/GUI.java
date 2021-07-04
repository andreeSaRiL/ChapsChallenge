package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.ChapTile;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.recordReplay.Record;
import nz.ac.vuw.ecs.swen225.gp20.recordReplay.Replay;
import nz.ac.vuw.ecs.swen225.gp20.render.Render;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//import nz.ac.vuw.ecs.swen225.gp20.persistence.Persistence;
//import nz.ac.vuw.ecs.swen225.gp20.persistence.Persistence;

/**
 * The game GUI class: provides a Graphical User Interface through which the
 * player can see the maze and interact with it through keystrokes. Also creates
 * the menu bar and load all elements and functionality. Class is also
 * responsible for the creation and renter of the information panel as well as
 * displaying the rendered board.
 * <p>
 * Elements interacted with: > Maze > Renderer > Record and Replay > Persistence
 * and levels
 *
 * @author zachkingsford
 */

/**
 * GUI glass contains sub classes and displays all the game elements such as board and info
 */
public class GUI extends Component implements KeyListener {

	// Set the size of the window using FINAL values
	private static final int WINDOW_INDENT = 100; // the indent from the edge of the display screen
	private static final int GAP_INDENT = 50; // the indent from the edge of the display screen

	// Store the width and height as a single object
	private Dimension currScrDim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width - WINDOW_INDENT,
			Toolkit.getDefaultToolkit().getScreenSize().height - WINDOW_INDENT);

	// Set the size of the other elements to be based off this screen size to ensure
	// a consistent ratio on all screens
	public int SCREEN_INDENT = currScrDim.height / 16; // the indent from the edge of the display screen

	// MENUBAR
	private int MENUBAR_WIDTH = currScrDim.width;
	private int MENUBAR_HEIGHT = currScrDim.height / 30;

	// SCREEN
	private int SCREEN_WIDTH = currScrDim.width;
	// private int SCREEN_HEIGHT = currScrDim.height - MENUBAR_HEIGHT;

	// BOARD
	public int BOARD_WIDTH = (currScrDim.width - (GAP_INDENT + (SCREEN_INDENT * 2))) / 2;
//    private int BOARD_HEIGHT = currScrDim.height - (2*SCREEN_INDENT+MENUBAR_HEIGHT);

	// INFO
	private int INFO_WIDTH = (currScrDim.width - (GAP_INDENT + (SCREEN_INDENT * 2))) / 4;
	private int INFO_HEIGHT = currScrDim.height - (2 * SCREEN_INDENT + MENUBAR_HEIGHT);

	// FRAME,PANEL,BUTTONS etc.
	protected JFrame outer_frame;
	protected JMenuBar menuBar;
	// public static JPanel information;

	// ADD THE TWO DISPLAYS
	public boardDisplay bD;
	public infoDisplay iD;

	// store the current direction
	public String direction = "";

	// store the game object
	Game currentGame;

	private HashSet<Integer> pressedKeys;

	// Save the file

	// map for chaps backpack
	Map<String, Integer> backpackMap;

	// store the variables to store the number of keys picked up
	int blueCount = 0;
	int yellowCount = 0;
	int redCount = 0;
	int greenCount = 0;

	// Record
	public static boolean isMovingRecorded = false; // Checks when the latest game is recording
	protected boolean isPlayerMoving = false; // Checks the player movement when recoding
	protected Record currRecord;
	protected long recordTimer;

	// Replay
	public Replay replay;
	private double replaySpeed; // Stores the speed for auto replay mode
	public String replayMode; // Stores the auto replay or step by step replay mode
	private boolean isReplaying; // Tracks the game when is replaying state

	// Store the current Render object so that bD (board display) can be rendered
	Render boardRender;

	/**
	 * Constructor for the application GUI object
	 *
	 * @param currentGame - A game object created in the game class
	 */
	public GUI(Game currentGame) {
		// set the game
		setCurrentGame(currentGame);
		this.currentGame.setGameGUI(this);

		// setup the record and replay components
		currRecord = new Record(currentGame);
		replay = new Replay(currentGame);
		isReplaying = false;

		// setup the outer JFRAME
		outer_frame = new JFrame("Chaps Challenge by TreyWay");
		outer_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		outer_frame.setSize(currScrDim);
		// outer_frame.setBackground(Color.black);
		// add the two display views
		iD = new infoDisplay();
		bD = new boardDisplay();
		outer_frame.add(bD);
		outer_frame.add(iD);

		// Initialise and setup the render object
		boardRender = new Render(currentGame.getGameMaze());

		// split the pane into two sides board and information
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bD, iD);
		splitPane.setDividerLocation(SCREEN_WIDTH / 2);

		// init inner panels
		initMenuBar();

		// store the pressed keys
		pressedKeys = new HashSet<>();

		// store the key listerner
		outer_frame.addKeyListener(this);

		iD.setBackground(Color.black);
		bD.setBackground(Color.black);
		// make the frame visible
		outer_frame.setJMenuBar(menuBar);
		outer_frame.add(splitPane);
		outer_frame.revalidate();
		outer_frame.repaint();
		outer_frame.setVisible(true);

	}

	/**
	 * Setup and layout the elements of the menubar.
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void initMenuBar() {

		// Initialise and create the java swing menubar
		menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(MENUBAR_WIDTH, MENUBAR_HEIGHT));
		menuBar.setLayout(new GridBagLayout());

		// setup the buttons to be added to the menu bar
		JMenuItem GameBtn = new JMenu("Game");
		JMenuItem newGameBtn = new JMenuItem("New Game");
		JMenuItem loadGameBtn = new JMenuItem("Load Game");
		JMenuItem saveBtn = new JMenuItem("Save Game");
		JMenuItem RecordBtn = new JMenuItem("Record");
		JMenuItem autoPlayBtn = new JMenuItem("Auto Play");
		JMenuItem nextStepBtn = new JMenuItem("Next Step Play");
		JMenuItem stopReplayBtn = new JMenuItem("Stop replay");
		JMenuItem helpBtn = new JMenuItem("Help");
		JMenuItem pauseBtn = new JMenuItem("Pause");
		JMenuItem resumeBtn = new JMenuItem("Resume");
		JMenuItem exitBtn = new JMenuItem("Exit");

		// setup the action listeners
		newGameBtn.addActionListener(actionEvent -> {
			// default game
			initGame();
		});

		loadGameBtn.addActionListener(actionEvent -> {
			// select a file to load
			loadDialog();
		});

		saveBtn.addActionListener(actionEvent -> {
			// save game
			initSaveDialog();
		});

		helpBtn.addActionListener(e -> {
			// display help dialog box
			displayHelpDialog();
		});

		pauseBtn.addActionListener(actionEvent -> {
			// pause the game
			displayPauseDialog();
		});

		resumeBtn.addActionListener(actionEvent -> {
			// resume the game
			resumeGame();
		});

		exitBtn.addActionListener(actionEvent -> {
			// open dialog to confirm game exit
			initExitGame();
		});

		// Action listeners defined for recording the game (Start,Stop,Save)
		RecordBtn.addActionListener(actionEvent -> {
			if (RecordBtn.getText().equals("Record")) {
				int confirm = JOptionPane.showConfirmDialog(null, "Record this game??", "Record game",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirm == JOptionPane.YES_OPTION) {
					RecordBtn.setText("Click here to stop recording");
					// Records the latest game and start the recoding then tracks the player movement and records the time
					setRecord(currRecord);
					isMovingRecorded(true); // Checks when the game is recoding
					setRecordingTime(System.nanoTime());
					currRecord.recording();
					isPlayerMoving = true; // Checks the player when moving
				}
			} else if (RecordBtn.getText().equals("Click here to stop recording")) {
				int confirmStop = JOptionPane.showConfirmDialog(null, "Stop recording??", "Stop Recording",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirmStop == JOptionPane.YES_OPTION) {
					RecordBtn.setText("Save Record");
					JOptionPane.showMessageDialog(null, "Saved in the recordedGame.json", "Saved recoded game",
							JOptionPane.INFORMATION_MESSAGE);

					// Tracks the recording until it stops recoding then save it to the file and reset the timer
					isMovingRecorded(false);
					currRecord.stopRecording();
					getCurrentRecord().setRecordTimer(System.nanoTime() - getRecordingTime());
					RecordBtn.setText("Record");
				}
			}
		});

		// Action listeners defined for replaying the game (auto replay,step by step,speed and stop replay)
		autoPlayBtn.addActionListener(actionEvent -> {
			 if (!isReplaying) {
	                JOptionPane.showMessageDialog(null, "Auto replay mode has been selected-[click STOP REPLAY button to end this mode]", "Auto Replay",
	                        JOptionPane.PLAIN_MESSAGE);
			 }
			
			// Select a speed for auto and replay without any interruption from keyListener
            String[] speedArray = { "0.5", "1.0", "1.5", "2.0" };
            int speedMode = JOptionPane.showOptionDialog(null, "Select preferred replay mode to play", "Replay Mode",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, speedArray, speedArray[0]);
            switch (speedMode) {
            case 0:
                replaySpeed = 0.00000000125;
                break;
            case 1:
                replaySpeed = 0.000000005;
                break;
            case 2:
                replaySpeed = 0.000000065;
                break;
            case 3:
                replaySpeed = 0.0000001;
                break;
            default:
                replaySpeed = 0.0000000005;
            }
            replayMode = "Auto play";

           

            // load moves history
            replay.replay();
            isReplaying = true;
            currentGame.getGameMaze().setReplay(true);//disable the enemy when playing auto mode for level 2
            iD.repaint();
		});

		nextStepBtn.addActionListener(actionEvent -> {
			// Replays one step at a step when replay state is active
			replayMode = "step by step replay";
			if (!isReplaying) {
				JOptionPane.showMessageDialog(null, "Step by step replay mode has been selected- [click STOP REPLAY button to end this mode]", "Next Step Replay",
						JOptionPane.PLAIN_MESSAGE);
			
				replay.replay();
                replay.makeStepByStepMove();
                currentGame.getGameMaze().setReplay(true);//disable the enemy when playing step by step mode for level 2
                bD.repaint();
                isReplaying = true;
            }
            replay.makeStepByStepMove();
			
		});
	
		//When the replay is force to stop, continue the game as normal
        stopReplayBtn.addActionListener(actionEvent -> {
        	//When the replay is force to stop, continue the game as normal
            if (isReplaying) {
                isReplaying = false;
               JOptionPane.showMessageDialog(null, "Replaying has been stopped, you can continue playing", "Stop Replay", JOptionPane.PLAIN_MESSAGE);
             }

            if(replayMode != null && currentGame.getPersistence().getLevelNumber() == 2) //activate the enemy
                reloadLevel();

        });

		// add all the buttons to the menu bar
		menuBar.add(GameBtn);
		GameBtn.add(newGameBtn);
		GameBtn.add(loadGameBtn);
		GameBtn.add(saveBtn);
		menuBar.add(RecordBtn);
		menuBar.add(autoPlayBtn);
		menuBar.add(nextStepBtn);
		menuBar.add(stopReplayBtn);
		menuBar.add(pauseBtn);
		menuBar.add(resumeBtn);
		menuBar.add(helpBtn);
		menuBar.add(exitBtn);
	}
	
	/**
	 * Reload level 2 to activate the enemy
	 */
	private void reloadLevel() {
        currentGame.getPersistence().loadLevels(null, 2);
        Maze level2 = new Maze((currentGame.getPersistence()));
        currentGame.setGameMaze(level2);
        boardRender.updateMaze(level2);
        bD.repaint();
        iD.repaint();
    }

	/**
	 * Init the current game by setting up needed parameters (Maze,level etc.)
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void initGame() {
		// new game and display a warning message
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?",
				"Start new game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.YES_OPTION) {
			// reload the current level
			resetCurrentLevel();
		}
	}

	/**
	 * Method to initiate the level loader dialog popup and re-render the game once
	 * a level is selected
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void loadDialog() {
		String[] levelNum = { "Level 1", "Level 2", " Saved Game" };
		int levelOption = JOptionPane.showOptionDialog(null, "which file?", "Select a button",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, levelNum, levelNum[0]);
		// load selected level
		// Load level 1:
		if (levelOption == 0) {
			currentGame.getPersistence().loadLevels(null, 1);
			Maze level1 = new Maze((currentGame.getPersistence()));
			currentGame.setGameMaze(level1);
			boardRender.updateMaze(level1);
			isReplaying = false;
			bD.repaint();
			iD.repaint();
		}

		// load level 2:
		if (levelOption == 1) {
			currentGame.getPersistence().loadLevels(null, 2);
			Maze level2 = new Maze((currentGame.getPersistence()));
			currentGame.setGameMaze(level2);
			boardRender.updateMaze(level2);
			isReplaying = false;
			bD.repaint();
			iD.repaint();
		}
		// load savedGame.json:
		if (levelOption == 2) {
			currentGame.getPersistence().loadLevels(new File("SavedGameFile/saveGame.json"), 1);
			Maze level2 = new Maze((currentGame.getPersistence()));
			currentGame.setGameMaze(level2);
			boardRender.updateMaze(level2);
			isReplaying = false;
			bD.repaint();
			iD.repaint();
		}
	}

	/**
	 * method to setup the save dialog box , saves game via persistence and then
	 * confirms the save via dialog box
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void initSaveDialog() {
		currentGame.getPersistence().saveGame("SavedGameFile/saveGame.json", false,
				currentGame.getGameMaze().getMaze());
		JOptionPane.showMessageDialog(null, "This game has been saved in the saveGame.json", "Saved game",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// ***********need to change this*****************\\

	/**
	 * Method to resume the game will get called if a player wants to resume after
	 * stopping the game
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void resumeGame() {
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to resume this level?", "Resume Level",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.YES_OPTION) {
			// resume the level: use persistent
		} else {
		}
	}

	/**
	 * Method to display dialog box for exiting the game
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void initExitGame() {
		// exit game
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit the game?",
				"Exit Chaps Challenge by TreyWay", dialogButton);
		if (dialogResult == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	/**
	 * Method to display dialog box when the game is in a "GAME OVER" state
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	public void displayGameOver() {
		// exit game
		if (currentGame.getGameMaze().hasEnded()) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(null, "Game Over :) Would you like to play again?",
					"Exit Chaps Challenge by TreyWay", dialogButton);
			if (dialogResult == JOptionPane.NO_OPTION) {
				System.exit(0);
			} else {
				loadDialog();
			}
		}
	}

	/**
	 * Method to display dialog box when the game is in a "GAME OVER" state
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	public void resetCurrentLevel() {
		// reset level
		currentGame.getPersistence().loadLevels(null, getLevel());
		Maze level = new Maze((currentGame.getPersistence()));
		currentGame.setGameMaze(level);
		boardRender.updateMaze(level);
		bD.repaint();
		iD.repaint();
	}

	/**
	 * control game aspects to pause the time and displayed the paused state dialog
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void displayPauseDialog() {
		currentGame.getGameMaze().pauseTime();
		Object[] options = { "Resume", "Quit" };
		Object defaultOption = options[0];
		int pauseGame = JOptionPane.showOptionDialog(null,
				new JLabel("<html> Your game has been paused!" + "<br/>Click resume to keep playing,", JLabel.CENTER),
				"Pause Game!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, defaultOption);
		if (pauseGame == JOptionPane.YES_OPTION) {
			currentGame.getGameMaze().resumeTime();
		} else if (pauseGame == JOptionPane.NO_OPTION) {
			initExitGame();
		} else {
			currentGame.getGameMaze().resumeTime();
		}
	}

	/**
	 * Method to display the help dialog text uses a custom image icon for increased
	 * aesthetic
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	private void displayHelpDialog() {
		JDialog helpDialog = new JDialog();
		Image image;
		Image resize;
		Icon icon = null;
		try {
			image = ImageIO.read(new File("assets/controls.jpg"));
			resize = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			icon = new ImageIcon(resize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(helpDialog,
				"CTRL-X  - exit the game, the current game state will be lost, the next time the game is started, it will resume from\n"
						+ "the last unfinished level\n"
						+ "CTRL-S  - exit the game, saves the game state, game will resume next time the application will be started\n"
						+ "CTRL-R  - resume a saved game\n"
						+ "CTRL-P  - start a new game at the last unfinished level\n"
						+ "CTRL-1 - start a new game at level 1\n" + "CTRL-2 - start a new game at level 1\n"
						+ "SPACE - pause the game and display a game is paused dialog\n"
						+ "ESC - close the game is paused dialog and resume the game\n"
						+ "UP, DOWN, LEFT, RIGHT ARROWS -- move Chap within the maze",
				"Game Controls", JOptionPane.INFORMATION_MESSAGE, icon);
	}

	/**
	 * method used when a player walks on the info tile to display the message that
	 * corresponds to that tile
	 * <p>
	 * Method does not take any variables and does not return any information
	 */
	public void displayHintDialog() {
		JDialog helpDialog = new JDialog();
		Image image;
		Image resize;
		Icon icon = null;
		try {
			image = ImageIO.read(new File("assets/controls.jpg"));
			resize = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
			icon = new ImageIcon(resize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(helpDialog, currentGame.getGameMaze().getMessage(), "Game Controls",
				JOptionPane.INFORMATION_MESSAGE, icon);
	}

	/**
	 * key typed method is not used was implements so that other aspects of key
	 * typed could be overwritten
	 *
	 * @param e - key event of the key that was typed
	 */

	@Override
	public void keyTyped(KeyEvent e) {
		/* NOT UTILISED */
	}

	/**
	 * key pressed to call different method depending on what key was pressed
	 *
	 * @param e - a key event that is used to get the keycode so the program knows
	 *          what key was pressed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (!isReplaying) {

			// Add the key pressed to the current list of pressed keys
			pressedKeys.add(e.getKeyCode());

			// CTRL + X
			if (pressedKeys.contains(KeyEvent.VK_CONTROL) && pressedKeys.contains(KeyEvent.VK_X)
					&& pressedKeys.size() == 2) {
				initExitGame();
				pressedKeys.clear();
			}

			// CTRL + S
			if (pressedKeys.contains(KeyEvent.VK_CONTROL) && pressedKeys.contains(KeyEvent.VK_S)
					&& pressedKeys.size() == 2) {
				initSaveDialog();
				pressedKeys.clear();
			}

			// CTRL + P
			if (pressedKeys.contains(KeyEvent.VK_CONTROL) && pressedKeys.contains(KeyEvent.VK_P)
					&& pressedKeys.size() == 2) {
				currentGame.getPersistence().loadLevels(null, getLevel());
				Maze level = new Maze((currentGame.getPersistence()));
				currentGame.setGameMaze(level);
				boardRender.updateMaze(level);
				bD.repaint();
				iD.repaint();
			}
			// CTRL + Number
			if (pressedKeys.contains(KeyEvent.VK_CONTROL) && pressedKeys.size() == 2) {
				for (int i = 0; i != 10; ++i) {
					if (pressedKeys.contains(KeyEvent.VK_1 + i)) {
						currentGame.getPersistence().loadLevels(null, i + 1);
						Maze level = new Maze((currentGame.getPersistence()));
						currentGame.setGameMaze(level);
						boardRender.updateMaze(level);
						bD.repaint();
						iD.repaint();
					}
				}
			}
			// SPACE
			if (pressedKeys.contains(KeyEvent.VK_SPACE) && pressedKeys.size() == 1) {
				displayPauseDialog();
				pressedKeys.clear();
			}
			// CTRL + R
			if (pressedKeys.contains(KeyEvent.VK_CONTROL) && pressedKeys.contains(KeyEvent.VK_R)
					&& pressedKeys.size() == 2) {
				resumeGame();
				pressedKeys.clear();
			}
			// ESC
			if (pressedKeys.contains(KeyEvent.VK_ESCAPE) && pressedKeys.size() == 1) {
				pressedKeys.clear();
				currentGame.getGameMaze().resumeTime();
			}
			/*
			 * PLAYER CONTROLS
			 */
			// Move Up
			if ((pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W))
					&& pressedKeys.size() == 1) {
				direction = "U";
				runMove();
			}
			// Move Down
			if ((pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S))
					&& pressedKeys.size() == 1) {
				direction = "D";
				runMove();
			}
			// Move Left
			if ((pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A))
					&& pressedKeys.size() == 1) {
				direction = "L";
				runMove();
			}
			// Move Right
			if ((pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D))
					&& pressedKeys.size() == 1) {
				direction = "R";
				runMove();
			}
			bD.repaint();
		}
	}

	/**
	 * reset the list of pressed keys when a key is released
	 *
	 * @param e - a key event to tell if a key has been released
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		pressedKeys.clear();
	}

	/**
	 * Method to display the board section of the This method extends JPanel and
	 * introduces java swing graphics
	 */
	class boardDisplay extends JPanel {

		public boardDisplay() {
		}

		/**
		 * Method to draw the board
		 *
		 * @param g - takes java graphics as a parameter so it can be passed to the
		 *          renderer object
		 */
		private void drawBoard(Graphics g) {
			boardRender.draw(g);
		}

		/**
		 * Draw all game elements
		 *
		 * @param g - takes java swing graphics as a parameter
		 */
		public void paint(Graphics g) {
			super.paint(g);
			drawBoard(g);
		}
	}

	/**
	 * process a movement being made, called after a movement key is pressed all
	 * movements are passed to the maze component so that the move can be
	 * implemented Method does not take any variables and does not return any
	 * information
	 */
	private void runMove() {

		// Record initial step
		if (isPlayerMoving) {
			String fileName = "SavedGameFile/recordedGame.json";
			long time = System.nanoTime() - recordTimer;
			currentGame.persistence.savePlayerMovement(fileName, direction, time, true);
			isPlayerMoving = false;
		}

		// Go UP
		if (direction.equals("U")) {
			currentGame.getGameMaze().move("U");
		}
		// Go DOWN
		if (direction.equals("D")) {
			currentGame.getGameMaze().move("D");
		}
		// Go LEFT
		if (direction.equals("L")) {
			currentGame.getGameMaze().move("L");
		}
		// Go RIGHT
		if (direction.equals("R")) {
			currentGame.getGameMaze().move("R");
		}

		// when the latest game is recoding
		if (isMovingRecorded) {
			String fileName = "SavedGameFile/recordedGame.json";
			long time = System.nanoTime() - recordTimer;
			currentGame.persistence.savePlayerMovement(fileName, direction, time, false);

		}
	}

	/**
	 * Draw and display all the variables in the information panel This method
	 * extends JPanel as it is the right hand component in the split frame Method
	 * does not take any variables and does not return any information
	 */
	class infoDisplay extends JPanel {

		// set the component colours .
		private final Color TEXT_COLOUR = new Color(0, 0, 0);
		private final Color ACCENT_COLOUR = new Color(0, 255, 0);

		// colour will fade to red as the time decreases
		Color countdowClr = Color.decode("#FD1C03");

		// Arraylist to store all the keys collected so they can be displayed.
		private ArrayList<String> keys;

		// set the component background image
		public Image BACKGROUND_IMAGE;
		Font title;

		// load the font
		{
			try {
				title = Font.createFont(Font.TRUETYPE_FONT, new File("assets/alarm.ttf"));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * constructor for the class gets ready by loading the image used to display the
		 * info panel
		 */
		public infoDisplay() {
			loadAsset();
		}

		private void loadAsset() {
			try {
				BACKGROUND_IMAGE = ImageIO.read(new File("assets/infopanel.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Draw all game elements.
		 *
		 * @param g - java graphics used to draw images and text
		 */
		public void paint(Graphics g) {
			Image background = BACKGROUND_IMAGE.getScaledInstance(INFO_WIDTH, INFO_HEIGHT, java.awt.Image.SCALE_SMOOTH);
			g.drawImage(background, GAP_INDENT, SCREEN_INDENT, null);
			// updateColour();
			g.setColor(countdowClr);
			int fontsize = (INFO_HEIGHT / 16) * 2;
			g.setFont(title.deriveFont((float) fontsize));
			if(!isReplaying) {
				g.drawString(currentGame.getGameMaze().getLevelAsString(), GAP_INDENT + (int) (0.25 * INFO_WIDTH),
						SCREEN_INDENT + (int) (0.25 * INFO_HEIGHT));
				g.drawString(currentGame.getGameMaze().getTimeAsString(), GAP_INDENT + (int) (0.25 * INFO_WIDTH),
						SCREEN_INDENT + (int) (0.44 * INFO_HEIGHT));
				g.drawString(currentGame.getGameMaze().getChipsAsString(), GAP_INDENT + (int) (0.25 * INFO_WIDTH),
						SCREEN_INDENT + (int) (0.65 * INFO_HEIGHT));
				// call chaps backpack method to render the keys in his backpack
				drawChapsBackpack(g);
			}
			else{
				g.drawString("REC", GAP_INDENT + (int) (0.25 * INFO_WIDTH),
						SCREEN_INDENT + (int) (0.25 * INFO_HEIGHT));
				g.drawString("REC", GAP_INDENT + (int) (0.25 * INFO_WIDTH),
						SCREEN_INDENT + (int) (0.44 * INFO_HEIGHT));
				g.drawString("REC", GAP_INDENT + (int) (0.25 * INFO_WIDTH),
						SCREEN_INDENT + (int) (0.65 * INFO_HEIGHT));
			}
		}

		/**
		 * Method to render the keys as they are picked up by chap and display them in
		 * his backpack
		 *
		 * @param g - java graphics parameter so that images and text strings can be
		 *          drawn
		 */
		private void drawChapsBackpack(Graphics g) {
			// store all the variables
			blueCount = 0;
			yellowCount = 0;
			redCount = 0;
			greenCount = 0;
			int fontsize = 15;
			g.setFont(title.deriveFont((float) fontsize));
			ChapTile chapInfo = currentGame.getGameMaze().getChap();
			// arraylist to store all the current keys collected
			keys = chapInfo.getKeys();
			// store the inital x and y so that the counter string are displayed in the
			// correct position
			int initalX = (GAP_INDENT + (INFO_WIDTH - (boardRender.imageWH) * 4) / 2);
			int imageWidth = boardRender.imageWH;
			// hashmap to store the back pack information in their pairs (Key- STRING,count
			// - INT)
			backpackMap = new HashMap<>();
			// Iterate through the keys and store them in the map
			for (String k : keys) {
				// BLUE KEY
				if (k.equals("blue")) {
				
					blueCount++;
					backpackMap.put(k, blueCount);
				}
				// YELLOW KEY
				else if (k.equals("yellow")) {
					yellowCount++;
					backpackMap.put(k, yellowCount);
				}
				// RED KEY
				else if (k.equals("red")) {
					redCount++;
					backpackMap.put(k, redCount);
				}
				// GREEN KEY
				else {
					greenCount++;
					backpackMap.put(k, greenCount);
				}
			}
			// once all the variables are loaded into the map iterate through the map to
			// render the key and the counter
			int i = 0;
			for (String s : backpackMap.keySet()) {
				int x = initalX + imageWidth * i;
				int y = SCREEN_INDENT + (INFO_HEIGHT / 16) * 11;
				if (s.equals("blue")) {
					g.setColor(Color.gray);
					g.drawImage(boardRender.blue_key, x, y, null);
					g.setColor(Color.blue);
					g.drawString(Integer.toString(backpackMap.get(s)), x, y);
				} else if (s.equals("yellow")) {
					g.setColor(Color.gray);
					g.drawImage(boardRender.orange_key, x, y, null);
					g.setColor(Color.yellow);
					g.drawString(Integer.toString(backpackMap.get(s)), x, y);

				} else if (s.equals("red")) {
					g.setColor(Color.gray);
					g.drawImage(boardRender.pink_key, x, y, null);
					g.setColor(Color.red);
					g.drawString(Integer.toString(backpackMap.get(s)), x, y);

				} else {
					g.setColor(Color.gray);
					g.drawImage(boardRender.green_key, x, y, null);
					g.setColor(Color.green);
					g.drawString(Integer.toString(backpackMap.get(s)), x, y);
				}
				i++;
			}
		}
	}

	// ****************record************\\

	/**
	 * sets the current record object
	 *
	 * @param record - takes a record object as a parameter
	 */
	public void setRecord(Record record) {
		currRecord = record;
	}

	/**
	 * return the current record object
	 *
	 * @return - a record object that relates to the one currently being used by the
	 *         program
	 */
	public Record getCurrentRecord() {
		return currRecord;
	}

	/**
	 * set the time to be recorded
	 *
	 * @param nanoTime - long variable that corresponds to the time in nano seconds
	 */
	public void setRecordingTime(long nanoTime) {
		recordTimer = nanoTime;
	}

	/**
	 * return the current recording time
	 *
	 * @return - returns a long variable
	 */
	public long getRecordingTime() {
		return recordTimer;
	}

	/**
	 * boolean variable to check if the move is being recorded
	 *
	 * @param record - takes a record object
	 */
	public static void isMovingRecorded(boolean record) {
		isMovingRecorded = record;
	}

	// Record player's movement NOT USED
	public void setRecordPlayerMove(boolean move) {
		isPlayerMoving = move;
	}

	// Get's the player's initial movement NOT USED
	public boolean getRecordPlayerMove() {
		return isPlayerMoving;
	}

	// *********************** GETTERS and SETTERS *****************************\\

	/**
	 * get the current game that relates to this GUI object
	 *
	 * @return - return the Game object
	 */
	public Game getCurrentGame() {
		return currentGame;
	}

	/**
	 * get the dimension related to the width of the board
	 *
	 * @return - return an interger width
	 */
	public int getBOARD_WIDTH() {
		return BOARD_WIDTH;
	}

	/**
	 * set the board with
	 *
	 * @param BOARD_WIDTH - integer that updates the width of the board
	 */
	public void setBOARD_WIDTH(int BOARD_WIDTH) {
		this.BOARD_WIDTH = BOARD_WIDTH;
	}

	/**
	 * set the current game object so that it can be used within the GUI
	 *
	 * @param currentGame - a Game object stored in the current game field
	 */
	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	/**
	 * get the current level
	 *
	 * @return - integer that is the current level being played
	 */
	public int getLevel() {
		return currentGame.getGameMaze().getLevel();
	}

	/**
	 * set the current level - updates by calling a current game method
	 *
	 * @param newLevel - integer that represents the new level
	 */
	public void setLevel(int newLevel) {
		currentGame.setLevel(newLevel);
	}

	/**
	 * get the type of replay taking place
	 *
	 * @return - a string that is the replay mode
	 */
	public String getReplayMode() {
		return replayMode;
	}

	/**
	 * return the replay speed
	 *
	 * @return - a double that outlines the replay speed
	 */
	public double getReplaySpeed() {
		return replaySpeed;
	}

    /**
     * Checking is the replaying is active
     * @return
     */
	public boolean isReplaying() {
		return isReplaying;
	}

    /**
     * Setting the replaying
     * @param value is the true or false state
     */
	public void setIsPlaying(boolean value) {
		isReplaying = value;
	}

    /**
     * Setting the direction of the player
     * @param direction is the player movement
     */
	public void setDirection(String direction) {
		this.direction = direction;
	}
}
