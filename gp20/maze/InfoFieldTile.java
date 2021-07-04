package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This class is used to represent a Info field tile in the maze.
 *
 * @author Morgan Hucker - 300474945.
 */
public class InfoFieldTile extends Tile {
    private String message;

    /**
     * Contructs a InfoFieldTile and gives it a position.
     *
     * @param x       Horizontal index.
     * @param y       Vertical index.
     * @param message the message to be displayed.
     */
    public InfoFieldTile(int x, int y, String message) {
        super(x, y);
        this.message = message;
    }

    /**
     * gets the message that is related to this tile.
     *
     * @return the message as a string.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "infoField";
    }
}