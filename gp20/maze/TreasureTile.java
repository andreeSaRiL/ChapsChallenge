package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This class is used to represent a treasure tile in the maze.
 *
 * @author Morgan Hucker - 300474945.
 */
public class TreasureTile extends Tile {

    /**
     * Contructs a TreausreTile and gives it a position.
     *
     * @param x Horizontal index.
     * @param y Vertical index.
     */
    public TreasureTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "treasure";
    }
}
