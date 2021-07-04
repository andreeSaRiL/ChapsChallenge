package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This class is used to represent an exit lock in the maze.
 *
 * @author Morgan Hucker - 300474945.
 */
public class ExitLockTile extends Tile {

    /**
     * Construct an exit lock tile and give it a position.
     *
     * @param x horizontal index.
     * @param y vertical index.
     */
    public ExitLockTile(int x, int y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "exitLock";
    }
}