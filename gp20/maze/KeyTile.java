package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This class is used to represent a key tile in the maze.
 *
 * @author Morgan Hucker - 300474945.
 */
public class KeyTile extends Tile {
    private String colour;

    /**
     * Contructs a KeyTile and gives it a position.
     *
     * @param x Horizontal index.
     * @param y Vertical index.
     * @param colour the colour of the key as a string.
     */
    public KeyTile(int x, int y, String colour) {
        super(x, y);
        this.colour = colour;
    }

    /**
     * Gets the colour of they key
     *
     * @return - a string that describes the colour of the key.
     */
    public String getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return "key";
    }
}
