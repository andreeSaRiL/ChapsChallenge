package nz.ac.vuw.ecs.swen225.gp20.render;

//import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
//import nz.ac.vuw.ecs.swen225.gp20.application.Game;
import nz.ac.vuw.ecs.swen225.gp20.maze.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * The Render class is responsible for the simple 2-dimensional view of the maze.
 * This class stores all the variables and methods needed for the drawing of the game
 * where it is then embedded in application.
 *
 * @author Gimani Weerasena
 */
public class Render {
    //Storing all the size variables
    int widHei = 10;
    int indent;
    public int imageWH;
    //Storing all the chap images
    public Image chap;
    public Image chap_back;
    public Image chap_front;
    public Image chap_left;
    public Image chap_right;
    //Storing all the key images
    public Image green_key;
    public Image orange_key;
    public Image pink_key;
    public Image blue_key;
    //Storing all the lock images
    public Image green_lock;
    public Image orange_lock;
    public Image pink_lock;
    public Image blue_lock;
    public Image exit_lock;
    //Storing all the misc images
    public Image floor;
    public Image wall;
    public Image chip;
    public Image info;
    public Image exit;
    public Image bomb;
    public Image frontMonster;
    public Image leftMonster;
    public Image rightMonster;
    public Image backMonster;
    //Storing all the shield images
    public Image shield;
    public Image shieldchapU;
    public Image shieldchapD;
    public Image shieldchapL;
    public Image shieldchapR;
    //Storing position of chap
    int chapX;
    int chapY;
    //Utilizing other modules
    Tile[][] maze;
    Maze currMaze;
    //Variables used in GUI for game dimensions
    private Dimension currScrDim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width - WINDOW_INDENT,
            Toolkit.getDefaultToolkit().getScreenSize().height - WINDOW_INDENT);
    public int SCREEN_INDENT = currScrDim.height / 16; // the indent from the edge of the display screen
    public int BOARD_WIDTH = (currScrDim.width - (GAP_INDENT + (SCREEN_INDENT * 2))) / 2;
    private static final int WINDOW_INDENT = 100; // the indent from the edge of the display screen
    private static final int GAP_INDENT = 50; // the indent from the edge of the display screen

    /**
     * Constructor for the render class where variables are initialised
     **/
    public Render(Maze maze) {
        this.currMaze = maze;
        getImages();
        getSounds();
        calculateBoardSize();
        resizeImages();
    }

    /**
     * Updates maze
     */
    private void updateMaze(){
        maze = currMaze.getMaze();
    }

    /**
     * The calculateBoardSize method is responsible for
     * getting the current game board size, to reuse when rending the game
     */
    public void calculateBoardSize() {
        indent = SCREEN_INDENT;
        imageWH = (BOARD_WIDTH / widHei);
    }

    /**
     * resizeImages method is responsible for setting all the images to
     * the new scaled sizes using the built in function .getScaledInstance.
     * These scaled verisons of the images are set to the image width and height (initialised at the top)
     * Utilizes SCALE_SMOOTH to prioritize smoothness over scaling speed using an image-scaling specified algorithm.
     */
    private void resizeImages() {
        //All images are resized for ease of use and to ensure all images are proportionate
        //Chap
        chap = chap.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        chap_back = chap_back.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        chap_front = chap_front.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        chap_left = chap_left.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        chap_right = chap_right.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        //Keys
        green_key = green_key.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        orange_key = orange_key.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        blue_key = blue_key.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        pink_key = pink_key.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        //Locks
        green_lock = green_lock.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        orange_lock = orange_lock.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        blue_lock = blue_lock.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        pink_lock = pink_lock.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        //Misc
        floor = floor.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        wall = wall.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        chip = chip.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        info = info.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        exit = exit.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        exit_lock = exit_lock.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        bomb = bomb.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        //enemy
        leftMonster = leftMonster.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        rightMonster = rightMonster.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        backMonster = backMonster.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        frontMonster = frontMonster.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        //Shields
        shield = shield.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        shieldchapU = shieldchapU.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        shieldchapD = shieldchapD.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        shieldchapL = shieldchapL.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
        shieldchapR = shieldchapR.getScaledInstance(imageWH, imageWH, java.awt.Image.SCALE_SMOOTH);
    }


    /**
     * Draw method is responsible for illustrating the specified images in the game
     * A focus region is implemented around Chap as per in the requirements as well
     * as rending the 2-dimensional view of the game using Graphics.
     *
     * Method loops through the width and height of the maze and moves on to
     * the focus region of 5 tiles around Chap, continuing on to then get each tile
     * and drawing in the correct allocated spaces using instanceOf
     *
     * @param g
     */
    public void draw(Graphics g) {
        updateMaze();
        findChap();
        Tile[][] playerView = new Tile[widHei][widHei];
        for (int x = 0; x < widHei; x++) {
            for (int y = 0; y < widHei; y++) {
                int xInd = chapX - widHei / 2 + x;
                int yInd = chapY - widHei / 2 + y;
                if (xInd > maze.length || xInd < 0 || yInd > maze[0].length || yInd < 0) {
                    playerView[x][y] = null;
                } else
                    playerView[x][y] = currMaze.getTileAt(xInd, yInd);
            }
        }
        for (int x = 0; x < widHei; x++) {
            for (int y = 0; y < widHei; y++) {
                Tile currentTile = playerView[x][y];
                if (currentTile == null) {
                    g.setColor(Color.red);
                    g.fillRect(indent + x * imageWH, indent + y * imageWH, imageWH, imageWH);
                } else if (currentTile instanceof FreeTile) {
                    g.drawImage(floor, indent + x * imageWH, indent + y * imageWH, null);
                } else if (currentTile instanceof WallTile) {
                    g.drawImage(wall, indent + x * imageWH, indent + y * imageWH, null);
                } else if (currentTile instanceof ChapTile) {
                    //Checking the direction of chap and shield deactivated
                    if (!currMaze.hasShield()) {
                        if (((ChapTile) currentTile).getLastMove().equals("U")) {
                            g.drawImage(chap_back, indent + x * imageWH, indent + y * imageWH, null);
                        } else if (((ChapTile) currentTile).getLastMove().equals("D")) {
                            g.drawImage(chap_front, indent + x * imageWH, indent + y * imageWH, null);
                        } else if (((ChapTile) currentTile).getLastMove().equals("L")) {
                            g.drawImage(chap_left, indent + x * imageWH, indent + y * imageWH, null);
                        } else {
                            g.drawImage(chap_right, indent + x * imageWH, indent + y * imageWH, null);
                        }
                    //Checking the direction of chap and shield activated
                    } else if (currMaze.hasShield()) {
                        if (((ChapTile) currentTile).getLastMove().equals("U")) {
                            g.drawImage(shieldchapU, indent + x * imageWH, indent + y * imageWH, null);
                        } else if (((ChapTile) currentTile).getLastMove().equals("D")) {
                            g.drawImage(shieldchapD, indent + x * imageWH, indent + y * imageWH, null);
                        } else if (((ChapTile) currentTile).getLastMove().equals("L")) {
                            g.drawImage(shieldchapL, indent + x * imageWH, indent + y * imageWH, null);
                        } else {
                            g.drawImage(shieldchapR, indent + x * imageWH, indent + y * imageWH, null);
                        }
                    }
                } else if (currentTile instanceof KeyTile) {
                    if (((KeyTile) currentTile).getColour().equals("green")) {
                        g.drawImage(green_key, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (((KeyTile) currentTile).getColour().equals("yellow")) {
                        g.drawImage(orange_key, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (((KeyTile) currentTile).getColour().equals("blue")) {
                        g.drawImage(blue_key, indent + x * imageWH, indent + y * imageWH, null);
                    } else {
                        g.drawImage(pink_key, indent + x * imageWH, indent + y * imageWH, null);
                    }
                } else if (currentTile instanceof LockedDoorTile) {
                    if (((LockedDoorTile) currentTile).getColour().equals("green")) {
                        g.drawImage(green_lock, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (((LockedDoorTile) currentTile).getColour().equals("yellow")) {
                        g.drawImage(orange_lock, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (((LockedDoorTile) currentTile).getColour().equals("blue")) {
                        g.drawImage(blue_lock, indent + x * imageWH, indent + y * imageWH, null);
                    } else {
                        g.drawImage(pink_lock, indent + x * imageWH, indent + y * imageWH, null);
                    }
                } else if (currentTile instanceof TreasureTile) {
                    g.drawImage(chip, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (currentTile instanceof InfoFieldTile) {
                    g.drawImage(info, indent + x * imageWH, indent + y * imageWH, null);
                } else if (currentTile instanceof ExitTile) {
                    g.drawImage(exit, indent + x * imageWH, indent + y * imageWH, null);
                } else if (currentTile instanceof ExitLockTile) {
                    g.drawImage(exit_lock, indent + x * imageWH, indent + y * imageWH, null);
                } else if (currentTile instanceof BombTile) {
                    g.drawImage(bomb, indent + x * imageWH, indent + y * imageWH, null);
                } else if (currentTile instanceof EnemyTile) {
                    if (((EnemyTile) currentTile).getLastMove() == 'U') {
                        g.drawImage(backMonster, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (((EnemyTile) currentTile).getLastMove() == 'D') {
                        g.drawImage(frontMonster, indent + x * imageWH, indent + y * imageWH, null);
                    } else if (((EnemyTile) currentTile).getLastMove() == 'L') {
                        g.drawImage(leftMonster, indent + x * imageWH, indent + y * imageWH, null);
                    } else {
                        g.drawImage(rightMonster, indent + x * imageWH, indent + y * imageWH, null);
                    }
                }
                 else if (currentTile instanceof ShieldTile) {
                    g.drawImage(shield, indent + x * imageWH, indent + y * imageWH, null);
                }
            }
        }
    }

    /**
     * This method gets the current location of chap
     */
    private void findChap() {
        chapX = currMaze.getChapX();
        chapY = currMaze.getChapY();
    }

    /**
     * Loading all the images with getImages() method
     * using the path to locate the images needed and initialises them to their
     * respective variable for later use during rendering the game
     */
    public void getImages() {
        try {
            //Try loading the images needed, catching any failed attempts
            String directory = "assets/";
            //Chap
            chap = ImageIO.read(new File(directory +"newChap.png"));
            chap_back = ImageIO.read(new File(directory + "newChapBackImg.png"));
            chap_front = ImageIO.read(new File(directory + "newChapImg.png"));
            chap_left = ImageIO.read(new File(directory + "newChapLeftImg.png"));
            chap_right = ImageIO.read(new File(directory + "newChapRightImg.png"));
            //Keys
            green_key = ImageIO.read(new File(directory + "newGreenKeyImg.png"));
            orange_key = ImageIO.read(new File(directory + "newOrangeKeyImg.png"));
            pink_key = ImageIO.read(new File(directory + "newPinkKeyImg.png"));
            blue_key = ImageIO.read(new File(directory + "newBlueKeyImg.png"));
            //Locks
            green_lock = ImageIO.read(new File(directory + "newGreenLockImg.png"));
            orange_lock = ImageIO.read(new File(directory + "newOrangeLockImg.png"));
            pink_lock = ImageIO.read(new File(directory + "newPinkLockImg.png"));
            blue_lock = ImageIO.read(new File(directory + "newBlueLockImg.png"));
            exit_lock = ImageIO.read(new File(directory + "newExitLockImgg.png"));
            //Misc
            floor = ImageIO.read(new File(directory + "newFloorImg.png"));
            wall = ImageIO.read(new File(directory + "WALLF.png"));
            chip = ImageIO.read(new File(directory + "newChipImg.png"));
            info = ImageIO.read(new File(directory + "newInfoImg.png"));
            exit = ImageIO.read(new File(directory + "exitImg.png"));
            bomb = ImageIO.read(new File(directory + "newBombImg.png"));
            frontMonster = ImageIO.read(new File(directory + "newFrontMonsterImg.png"));
            leftMonster = ImageIO.read(new File(directory + "newLeftMonsterImg.png"));
            rightMonster = ImageIO.read(new File(directory + "newRightMonsterImg.png"));
            backMonster = ImageIO.read(new File(directory + "newBackMonsterImg.png"));
            //Shields
            shield = ImageIO.read(new File(directory + "newShieldImg.png"));
            shieldchapL = ImageIO.read(new File(directory + "newChapShieldLeftImg.png"));
            shieldchapR = ImageIO.read(new File(directory + "newChapShieldRightImg.png"));
            shieldchapU = ImageIO.read(new File(directory + "newChapShieldBackImg.png"));
            shieldchapD = ImageIO.read(new File(directory + "newChapShieldFrontImg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The getSounds() method is used to add sound to the game
     * Background music is added by utilizing the built-in Clip method
     */
    public void getSounds() {
        try {
            File file = new File("assets/music/bgMusic.au");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            FloatControl volume= (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            //decrease volume by 40 decibels
            volume.setValue(-40.0f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(IOException | UnsupportedAudioFileException | LineUnavailableException e){
            e.printStackTrace();
        }
    }



    /**
     * The current maze is updated to the new maze
     * @param newMaze
     */
    public void updateMaze (Maze newMaze){
        currMaze=newMaze;
    }

}
