package nz.ac.vuw.ecs.swen225.gp20.maze;

//import com.google.common.base.Preconditions;
import java.util.Random;

/**
 * This class is used as a base class for all other tile classes.
 *
 * @author Morgan Hucker - 300474945.
 */
public abstract class Tile {

    //Fields for position of the tile in the maze.
    private int x;
    private int y;
    char lastMoveDir;


    /**
     * Construct a tile at a given position.
     *
     * @param x - Horizontal index.
     * @param y - vertical index.
     */
    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Used for tiles that move by themselves to move randomly or lock on to chap if they are close enough.
     *
     * @param tiles the current arrangement of tiles.
     * @param chap  the chap tile in the maze.
     * @return an updated arrangement of tiles.
     */
    public Tile[][] move(Tile[][] tiles, ChapTile chap) {
        //Preconditions.checkArgument(this instanceof EnemyTile || this instanceof BombTile);

        if (distanceToChap(getX(), getY(), chap) < 3) {
            return LockedOnMove(tiles, chap);
        } else {
            return randomMove(tiles);
        }
    }

    private Tile[][] LockedOnMove(Tile[][] tiles, ChapTile chap) {
       // Preconditions.checkArgument(this instanceof EnemyTile || this instanceof BombTile);

        double distance = distanceToChap(getX(), getY(), chap);
        if (distanceToChap(getX() + 1, getY(), chap) < distance && (tiles[getX() + 1][getY()] instanceof FreeTile || tiles[getX() + 1][getY()] instanceof ChapTile)) {
            if (tiles[getX() + 1][getY()] instanceof ChapTile) {
                chap.killChap();
            }
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX() + 1, getY());
            tiles[getX()][getY()] = this;
            lastMoveDir = 'R';
            return tiles;
        }
        if (distanceToChap(getX() - 1, getY(), chap) < distance && (tiles[getX() - 1][getY()] instanceof FreeTile || tiles[getX() - 1][getY()] instanceof ChapTile)) {
            if (tiles[getX() - 1][getY()] instanceof ChapTile) {
                chap.killChap();
            }
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX() - 1, getY());
            tiles[getX()][getY()] = this;
            lastMoveDir = 'L';
            return tiles;
        }
        if (distanceToChap(getX(), getY() + 1, chap) < distance && (tiles[getX()][getY() + 1] instanceof FreeTile || tiles[getX()][getY() + 1] instanceof ChapTile)) {
            if (tiles[getX()][getY() + 1] instanceof ChapTile) {
                chap.killChap();
            }
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX(), getY() + 1);
            tiles[getX()][getY()] = this;
            lastMoveDir = 'D';
            return tiles;
        }
        if (distanceToChap(getX(), getY() - 1, chap) < distance && (tiles[getX()][getY() - 1] instanceof FreeTile || tiles[getX()][getY() - 1] instanceof ChapTile)) {
            if (tiles[getX()][getY() - 1] instanceof ChapTile) {
                chap.killChap();
            }
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX(), getY() - 1);
            tiles[getX()][getY()] = this;
            lastMoveDir = 'U';
            return tiles;
        }
        return tiles;
    }


    private Tile[][] randomMove(Tile[][] tiles) {
       // Preconditions.checkArgument(this instanceof EnemyTile || this instanceof BombTile);


        double upChance = 1;
        double downChance = 1;
        double leftChance = 1;
        double rightChance = 1;
        if (!(tiles[getX()][getY() - 1] instanceof FreeTile)) {
            upChance = 0;
        }
        if (!(tiles[getX() + 1][getY()] instanceof FreeTile)) {
            rightChance = 0;
        }
        if (!(tiles[getX() - 1][getY()] instanceof FreeTile)) {
            leftChance = 0;
        }
        if (!(tiles[getX()][getY() + 1] instanceof FreeTile)) {
            downChance = 0;
        }

        if (lastMoveDir == 'U' && upChance != 0) {
            upChance = 7;
        } else if (lastMoveDir == 'D' && downChance != 0) {
            downChance = 7;
        } else if (lastMoveDir == 'L' && leftChance != 0) {
            leftChance = 7;
        } else if (lastMoveDir == 'R' && rightChance != 0) {
            rightChance = 7;
        }
        Random rand = new Random();
        int randomNum = rand.nextInt(((int) (leftChance + rightChance + upChance + downChance) - 1) + 1) + 1;
        int combinedChances = 0;
        combinedChances += upChance;
        if (combinedChances >= randomNum) {
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX(), getY() - 1);
            tiles[getX()][getY()] = this;
            lastMoveDir = 'U';
            return tiles;
        }
        combinedChances += downChance;
        if (combinedChances >= randomNum) {
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX(), getY() + 1);
            tiles[getX()][getY()] = this;
            lastMoveDir = 'D';
            return tiles;
        }
        combinedChances += leftChance;
        if (combinedChances >= randomNum) {
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX() - 1, getY());
            tiles[getX()][getY()] = this;
            lastMoveDir = 'L';
            return tiles;
        }
        combinedChances += rightChance;
        if (combinedChances >= randomNum) {
            tiles[getX()][getY()] = new FreeTile(getX(), getY());
            this.setPos(getX() + 1, getY());
            tiles[getX()][getY()] = this;
            lastMoveDir = 'R';
            return tiles;
        }
        return tiles;
    }

    //Find the distance between a given coordinate and chap.
    private double distanceToChap(int x, int y, ChapTile chap) {
        return Math.sqrt((chap.getX() - x) * (chap.getX() - x) +
                (chap.getY() - y) * (chap.getY() - y));
    }

    /**
     * @return - The horizontal index of the tile.
     */
    public int getX() {
        return x;
    }

    /**
     * @return - The vertical index of the tile.
     */
    public int getY() {
        return y;
    }

    /**
     * Set a tiles position.
     *
     * @param x - Horizontal index that the tile will be set to.
     * @param y - Vertical index the tile will be set to.
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }


}
