package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This class is used to represent a bomb tile that will be part of the maze.
 *
 * @author Morgan Hucker - 300474945.
 */
public class BombTile extends Tile {

    /**
     * Contructs a BombTile and gives it a position.
     *
     * @param x Horizontal index.
     * @param y Vertical index.
     */
    public BombTile(int x, int y) {
        super(x, y);
        lastMoveDir = 'D';
    }

    /**
     * Used to find which way the bomb tile is facing.
     *
     * @return a char that is any of U D L R which is the last moved direction.
     */
    public char getLastMove() {
        return lastMoveDir;
    }


    @Override
    public String toString() {
        return "bomb";
    }

}
