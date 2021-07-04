package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This class is used to represent a locked door tile in the maze.
 *
 * @author Morgan Hucker - 300474945.
 */
public class LockedDoorTile extends Tile {
    private String colour;

    /**
     * Contructs a LockedDoorTile and gives it a position.
     *
     * @param x Horizontal index.
     * @param y Vertical index.
     * @param colour the colour of the lcoked door.
     */
    public LockedDoorTile(int x, int y, String colour) {
        super(x, y);
        this.colour = colour;
    }

    /**
     * Get the colour of the key required to
     * move through this locked door.
     *
     * @return - A string that describes the colour of the door.
     */
    public String getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return "lockedDoor";
    }
}
