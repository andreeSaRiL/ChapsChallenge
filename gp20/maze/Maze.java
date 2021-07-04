package nz.ac.vuw.ecs.swen225.gp20.maze;

//import com.google.common.base.Preconditions;
import nz.ac.vuw.ecs.swen225.gp20.persistence.Persistence;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Used to add functionality and record the current state of the game.
 *
 * @author Morgan Hucker - 300474945.
 */
public class Maze {

    //Fields
    private int size;
    private int startingTreasures;
    private int treasureFound;
    private int level;
    private ChapTile chap;
    private ArrayList<BombTile> bombTiles;
    private ArrayList<EnemyTile> enemyTiles;
    private String message;
    private boolean displayMessage;
    private Tile[][] maze;
    private int initialTime;
    private int timeRemaining;

    //Game status fields
    private boolean pause;
    private boolean won;
    private boolean lost;
    private boolean hasEnded;
    private Timer timer;
    private boolean needsRendering;
    private boolean replay;


    /**
     * Construct a Maze object using a Persistence Object.
     *
     * @param per - Persistence object to load the level from.
     */
    public Maze(Persistence per) {
//        Preconditions.checkArgument(per != null);
//        Preconditions.checkArgument(per.getTiles()[0][0] != null);

        replay = false;
        pause = false;
        bombTiles = new ArrayList<>();
        enemyTiles = new ArrayList<>();
        startingTreasures = 0;
        maze = per.getTiles();
        timeRemaining = per.getStartingTime();
        initialTime = per.getStartingTime();
        timer = new Timer();
        timer.schedule(countdown, 1000, 1000);
        size = maze.length;
        level = per.getLevelNumber();
        for (int x = 0; x < maze.length; x++) {
            for (int y = 0; y < maze[0].length; y++) {
                getTileAt(x, y).setPos(x, y);
                if (getTileAt(x, y) instanceof ChapTile)
                    this.chap = (ChapTile) getTileAt(x, y);
                if (getTileAt(x, y) instanceof TreasureTile)
                    startingTreasures++;
                if (getTileAt(x, y) instanceof BombTile)
                    bombTiles.add((BombTile) getTileAt(x, y));
                if (getTileAt(x, y) instanceof EnemyTile)
                    enemyTiles.add((EnemyTile) getTileAt(x, y));
                if (getTileAt(x, y) == null)
                    assertTrue(false);
            }
        }
        assertTrue(chap != null);
        needsRendering = true;
    }

    //Timer that runs 'run' every second to update the time.
    TimerTask countdown = new TimerTask() {
        @Override
        public void run() {
           // Preconditions.checkArgument(treasureFound >= 0 && startingTreasures >= 1);

            if (!hasEnded && !pause)
                timeAdjust();
        }
    };

    /**
     * Method to move chap.
     *
     * @param direction - Direction to move by one tile in. Must be "U"/"D"/"L"/"R".
     */
    public void move(String direction) {
//        Preconditions.checkArgument(chap != null);
//        Preconditions.checkArgument(direction.equals("U") || direction.equals("D") || direction.equals("L") || direction.equals("R"));

        if (hasEnded || !chap.isAlive())
            return;
        int nextX = chap.getX();
        int nextY = chap.getY();
        int prevX = chap.getX();
        int prevY = chap.getY();
        switch (direction) {
            case "U":
                nextY--;
                break;
            case "D":
                nextY++;
                break;
            case "L":
                nextX--;
                break;
            case "R":
                nextX++;
                break;
        }
        if (checkValidMove(nextX, nextY)) {
            Tile standingOn = chap.getStandingOnRemoveCons();
            chap.setStandingOn(getTileAt(nextX, nextY));
            chap.setPos(nextX, nextY);
            setTileAt(prevX, prevY, standingOn);
            if (!(getTileAt(nextX, nextY) instanceof BombTile || getTileAt(nextX, nextY) instanceof EnemyTile))
                setTileAt(nextX, nextY, chap);
            chap.setLastMove(direction);
            needsRendering = true;
        }
    }

    /**
     * Checks a move is valid and interacts with tiles, e.g pick up keys.
     *
     * @param nextX - Tile x index to try move to.
     * @param nextY - Tile y index to try move to.
     * @return - True if the move is valid, false if not.
     */
    public boolean checkValidMove(int nextX, int nextY) {
        displayMessage = false;

        //Make sure next move is inbounds
        if (nextX >= size || nextX < 0 || nextY >= size || nextY < 0) {
            return false;
        }
        Tile destination = maze[nextX][nextY];
        if (destination instanceof WallTile || (destination instanceof ExitLockTile && timeRemaining != 0)) {
            return false;
        }
        if (destination instanceof KeyTile) {
            chap.addKey(((KeyTile) destination).getColour());
            return true;
        }
        if (destination instanceof LockedDoorTile) {
            String keyColour = ((LockedDoorTile) destination).getColour();
            if (chap.hasKey(keyColour)) {
                return true;
            }
            return false;
        }
        if (destination instanceof TreasureTile) {
            foundTreasure();
            return true;
        }
        if (destination instanceof FreeTile) {
            return true;
        }
        if (destination instanceof InfoFieldTile) {
            message = ((InfoFieldTile) destination).getMessage();
            displayMessage = true;
            return true;
        }
        if (destination instanceof ExitTile) {
            chap.setStandingOn(getTileAt(nextX, nextY));
            gameOverWin();
            return true;
        }
        if (destination instanceof ShieldTile) {
            chap.setShieldRemaining(10);
            return true;
        }
        if (destination instanceof BombTile || destination instanceof EnemyTile) {
            chap.killChap();
            return true;
        }

        assertTrue(false);
        return false;
    }

    //called when chap goes over a treasure tile.
    private void foundTreasure() {
       // Preconditions.checkArgument(treasureFound < startingTreasures);

        treasureFound++;
        if (treasureFound == startingTreasures) {
            removeExitLock();
        }
        assert treasureFound != 0 && treasureFound <= startingTreasures;

    }

    //called to set the game status to lost.
    private void gameOverWin() {
//        Preconditions.checkArgument(treasureFound == startingTreasures);
//        Preconditions.checkArgument(chap.getStandingOn() instanceof ExitTile);
//        Preconditions.checkArgument(!hasEnded());

        won = true;
        hasEnded = true;
    }

    //called to set the game status to win.
    private void gameOverLose() {
//        Preconditions.checkArgument(!hasEnded());
//        Preconditions.checkArgument(!chap.isAlive() || timeRemaining <= 0);
        lost = true;
        hasEnded = true;
    }

    //Remove the exit lock tile once all the treasures are collected.
    private void removeExitLock() {
//        Preconditions.checkArgument(treasureFound == startingTreasures);
//        Preconditions.checkArgument(!hasEnded);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (maze[x][y] instanceof ExitLockTile) {
                    setTileAtToFree(x, y);
                }
            }
        }

    }

    //call the move methods for all enemy and bomb tiles.
    private void moveEnemies() {
        if (replay)
            return;
        for (BombTile bt : bombTiles) {
            maze = bt.move(maze, chap);
            needsRendering = true;
        }
        for (EnemyTile et : enemyTiles) {
            maze = et.move(maze, chap);
            needsRendering = true;
        }
    }

    /**
     * Called to decrease time remaining,
     * calls game over when time runs out.
     */
    public void timeAdjust() {
//        Preconditions.checkArgument(!hasEnded);

        if (!chap.isAlive()) {
            gameOverLose();
        }

        moveEnemies();
        chap.shieldTimeTick();
        timeRemaining--;
        if (timeRemaining == 0) {
            gameOverLose();
        }
        assertTrue(timeRemaining < initialTime);
    }

    /**
     * Pause the timer.
     */
    public void pauseTime() {
        pause = true;
    }

    /**
     * Resume the timer.
     */
    public void resumeTime() {
        pause = false;
    }


    //===================================================
    // Setters and getters.
    //===================================================


    //Gets fields that need to be displayed on the panel to the right of the board and formats them.
    public String getTimeAsString() {
        String value = String.format("%03d", timeRemaining);
        return value;
    }

    public String getChipsAsString() {
        String value = String.format("%03d", getChipsRemaining());
        return value;
    }

    public String getLevelAsString() {
        String value = String.format("%03d", level);
        return value;
    }


    /**
     * Method to indicate if there are any changes in the maze that will need rendering.
     *
     * @return true if changes were made that need rendering, false if not.
     */
    public boolean needsRendering() {
        if (needsRendering) {
            needsRendering = false;
            return true;
        }
        return false;
    }

    /**
     * Set a given location to a given tile.
     *
     * @param x    - The horizontal index of the tile to be set.
     * @param y    - The vertical index of the tile to be set.
     * @param tile - The tile to be put at the x and y index in the 2D tile Array.
     */
    public void setTileAt(int x, int y, Tile tile) {
        maze[x][y] = tile;
        tile.setPos(x, y);
    }

    /**
     * Set a given location to a free tile.
     *
     * @param x - The horizontal index of the tile to be set.
     * @param y - The vertical index of the tile to be set.
     */
    public void setTileAtToFree(int x, int y) {
        maze[x][y] = new FreeTile(x, y);
    }

    /**
     * Get the tile at a given location.
     *
     * @param x - The horizontal index.
     * @param y - The vertical index.
     * @return The tile at the given.
     */
    public Tile getTileAt(int x, int y) {
        return maze[x][y];
    }

    /**
     * Get the chap tile for the level.
     *
     * @return returns the chap tile.
     */
    public ChapTile getChap() {
        return chap;
    }

    /**
     * Method to get the mze.
     *
     * @return a 2D array of all the tiles.
     */
    public Tile[][] getMaze() {
        return maze;
    }

    /**
     * get the current level number.
     *
     * @return an integer that represents the current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns a boolean that indicates if a message from an info
     * tile needs to be displayed.
     *
     * @return - a boolean to indicate whether a message should be displayed.
     */
    public boolean displayMessage() {
        return displayMessage;
    }

    /**
     * @return The message from the last info tile stood on by chap.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return - An integer that represents chaps horizontal index.
     */
    public int getChapX() {
        return chap.getX();
    }

    /**
     * @return - An integer that represents chaps vertical index.
     */
    public int getChapY() {
        return chap.getY();
    }

    /**
     * sets the chap Tile.
     *
     * @param chap - Chap tile to be set.
     */
    public void setChap(ChapTile chap) {
        this.chap = chap;
    }

    /**
     * Used to determine when a message needs to be displayed.
     *
     * @return - A boolean that is true when the message needs to be displayed, false if it does not need to be displayed.
     *
     */
    public boolean isDisplayMessage() {
        if (displayMessage) {
            displayMessage = false;
            return true;
        }

        return displayMessage;
    }


    /**
     * @return - A boolean that represents if the game has finished.
     */
    public boolean hasEnded() {
        return hasEnded;
    }

    /**
     * @return - A boolean that represent if chap won.
     */
    public boolean hasWon() {
        return won;
    }

    /**
     * Used to find out if a player has lost.
     *
     * @return - A boolean value representing whether or not
     * the player has lost this level.
     */
    public boolean hasLost() {
        return lost;
    }

    /**
     * Used to see how much time was given for this level.
     *
     * @return - An integer that represents how much time was given for the level.
     */
    public int getInitialTime() {
        return initialTime;
    }

    /**
     * Used to see how much time is remaining.
     *
     * @return - An integer that represent the time in seconds.
     * remaining for the current level.
     */
    public int getRemainingTime() {
        return timeRemaining;
    }

    /**
     * Gets the number of treasure tiles remaining on the board.
     *
     * @return - The number of treasure tiles remaining on the board.
     */
    public int getChipsRemaining() {
        return startingTreasures - treasureFound;
    }

    /**
     * Used to determine whether chap's shield needs to be rendered.
     *
     * @return true if the shield needs rendering, false if not.
     */
    public boolean hasShield() {
        return chap.hasShield();
    }

    /**
     * Get all bomb tiles in the level.
     *
     * @return An ArrayList of BombTiles
     */
    public ArrayList<BombTile> getBombTiles() {
        return bombTiles;
    }

    public void setReplay(boolean replay) {
        this.replay = replay;
    }

    /**
     * Get all EnemyTiles in the level.
     *
     * @return An ArrayList of EnemyTiles.
     */
    public ArrayList<EnemyTile> getEnemyTiles() {
        return enemyTiles;
    }
}
