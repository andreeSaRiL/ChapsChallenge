package nz.ac.vuw.ecs.swen225.gp20.maze;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

/**
 * This class is used to represent Chap as a tile in the maze.
 * This class is also used for chap shield functionality and key functionality for example.
 *
 * @author Morgan Hucker - 300474945.
 */
public class ChapTile extends Tile {

    //fields.
    private ArrayList<String> keys;
    private Tile standingOn;
    private int shieldTime;
    private boolean alive;
    private String lastMove;

    /**
     * Construct a chapTile.
     *
     * @param x - Horizontal index of the tile.
     * @param y - Vertical index of the tile.
     */
    public ChapTile(int x, int y) {
        super(x, y);
        this.keys = new ArrayList<>();
        this.standingOn = null;
        this.shieldTime = 0;
        this.alive = true;
        this.lastMove = "D";
    }

    /**
     * Removes chaps shield or if there is no shield active set alive to false.
     */
    public void killChap() {
       // Preconditions.checkArgument(alive);
        if (hasShield()) {
            setShieldRemaining(0);
            return;
        }
        alive = false;
    }

    /**
     * adds a key (string) to chaps inventory.
     *
     * @param key the colour of the key as a string.
     */
    public void addKey(String key) {
        keys.add(key);
    }

    /**
     * Check if chap has a certain coloured key.
     *
     * @param key A string that is the colour of the key.
     * @return - whether or not chap has the key.
     */
    public boolean hasKey(String key) {
        return keys.contains(key);
    }

    /**
     * Removes all consumable tiles that chap stands on by return a freeTile instead.
     *
     * @return The tile chap is standing on, or a new freeTile if the real tile is consumable.
     */
    public Tile getStandingOnRemoveCons() {
        if (standingOn == null || standingOn instanceof TreasureTile ||
                standingOn instanceof KeyTile || standingOn instanceof LockedDoorTile ||
                standingOn instanceof ShieldTile || standingOn instanceof ExitTile)
            return new FreeTile(getX(), getY());
        return standingOn;
    }

    /**
     * Gets the tile chap last stood on.
     *
     * @return The tile chap last stood on.
     */
    public Tile getStandingOn() {
        return standingOn;
    }

    /**
     * Used to check how many seconds of shield chap has.
     *
     * @return - An integer representing how many seconds of shield chap has.
     */
    public int shieldRemaining() {
        return shieldTime;
    }

    /**
     * Used to set the initial shield time chap will have if he gets a shield.
     *
     * @param time The initial shield time.
     */
    public void setShieldRemaining(int time) {
        this.shieldTime = time;
    }

    /**
     * Used to decrease chaps remaining shield time.
     */
    public void shieldTimeTick() {
        if (shieldTime != 0)
            shieldTime--;
    }

    /**
     * Used to find out if chap has a shield or not.
     *
     * @return true if chap has a shield false if not.
     */
    public boolean hasShield() {
        if (shieldRemaining() > 0)
            return true;
        return false;
    }

    /**
     * Used to find out if chap is alive.
     *
     * @return A boolean representing if chap is alive or not.
     */
    public boolean isAlive() {
        return alive;
    }


    /**
     * Used to set the tile chap is standing on.
     *
     * @param t the tile chap is standing on.
     */
    public void setStandingOn(Tile t) {
//        Preconditions.checkArgument(!(t instanceof LockedDoorTile) || keys.contains(((LockedDoorTile) t).getColour()));
//        Preconditions.checkArgument(!(t instanceof WallTile));
        this.standingOn = t;
    }

    @Override
    public String toString() {
        return "chap";
    }

    /**
     * Used to get the keys chap has picked up.
     *
     * @return an arraylist of the colours of the keys as strings.
     */
    public ArrayList<String> getKeys() {
        return keys;
    }

    /**
     * Set the direction chap last moved in.
     *
     * @param d The direction any of U D L R as a string.
     */
    public void setLastMove(String d) {
        this.lastMove = d;
    }

    /**
     * Get the last direction chap moved in.
     *
     * @return The direction any of U D L R as a string.
     */
    public String getLastMove() {
        return this.lastMove;
    }
}
