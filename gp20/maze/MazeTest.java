package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.persistence.Persistence;
import org.junit.jupiter.api.Test;
import java.util.Random;

/**
 * This class is used to test the maze package.
 */
public class MazeTest {

    public Maze newLevelOne() {
        Persistence per = new Persistence(null);
        per.loadLevels(null, 1);
        return new Maze(per);
    }

    public Maze newLevelTwo() {
        Persistence per = new Persistence(null);
        per.loadLevels(null, 2);
        return new Maze(per);
    }

    @Test
    /**
     * Test lots of random movement on level 2 to try trigger an error.
     */
    public void testMoveTwo() {
        try {
            Maze maze = newLevelTwo();
            System.out.println("Can take a couple minutes.");
            for (int i = 0; i < 500000000; i++) {
                if (maze.hasEnded())
                    maze = newLevelTwo();
                Random rand = new Random();
                int randomNum = rand.nextInt((4 - 1) + 1) + 1;
                if (randomNum == 1) {
                    maze.move("U");
                } else if (randomNum == 2) {
                    maze.move("D");
                } else if (randomNum == 3) {
                    maze.move("L");
                } else {
                    maze.move("R");
                }
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    /**
     * Test lots of random movement on level 1 to try trigger an error.
     */
    public void testMoveOne() {
        try {
            Maze maze = newLevelOne();
            System.out.println("Can take a couple minutes.");
            for (int i = 0; i < 500000; i++) {
                if (maze.hasEnded())
                    maze = newLevelOne();
                Random rand = new Random();
                int randomNum = rand.nextInt((4 - 1) + 1) + 1;
                if (randomNum == 1) {
                    maze.move("U");
                } else if (randomNum == 2) {
                    maze.move("D");
                } else if (randomNum == 3) {
                    maze.move("L");
                } else {
                    maze.move("R");
                }

            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    /**
     * Test losing by timer running out.
     */
    public void testLoseByTime() {
        Maze maze = newLevelOne();
        for (int i = 0; i < 200; i++) {
            if (maze.getRemainingTime() == 0) {
                assert maze.hasEnded();
                assert maze.hasLost();
                assert !maze.hasWon();
                break;
            }
            maze.timeAdjust();
        }
    }

    @Test
    /**
     * test the monsters movement by calling the update time method to try cause an
     * error.
     */
    public void testMonsterMovement() {
        try {
            Maze maze = newLevelTwo();
            for (int i = 0; i < 1000; i++) {
                if (maze.hasEnded())
                    maze = newLevelTwo();
                maze.timeAdjust();
            }
            for (int j = 0; j < 10; j++) {
                maze = newLevelTwo();
                maze.move("U");
                maze.move("U");
                maze.move("U");
                maze.move("L");
                maze.move("L");
                maze.move("U");
                maze.move("U");
                maze.move("U");
                maze.move("U");
                assert maze.needsRendering();
                assert !maze.needsRendering();
                for (int i = 0; i < 100; i++) {
                    if (maze.hasEnded())
                        break;
                    maze.timeAdjust();
                }
            }
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    /**
     * checks info tiles function correctly.
     */
    public void testInfoTile() {
        Maze maze = newLevelOne();
        assert !maze.isDisplayMessage();
        maze.move("L");
        assert !maze.isDisplayMessage();
        maze.move("L");
        assert maze.isDisplayMessage();
        assert maze.getMessage().equals(
                "Hint: collect all the treasures and keys lying around and come through the exitLock and exit door to move to the next level");
    }

    @Test
    /**
     * Tests treasure can be picked up.
     */
    public void testTreasure() {
        Maze maze = newLevelTwo();
        int initialChips = maze.getChipsRemaining();
        maze.move("U");
        maze.move("U");
        assert maze.getChipsRemaining() == initialChips - 1;
    }

    @Test
    /**
     * tests timer functionality.
     */
    public void testTimer() {
        Maze maze = newLevelOne();
        maze.pauseTime();
        int time = maze.getRemainingTime();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert maze.getRemainingTime() == time;
        maze.resumeTime();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert maze.getRemainingTime() != time;
    }

    @Test
    /**
     * Tests the majority of setters and getters.
     */
    public void testSettersAndGetters() {
        Maze maze = newLevelOne();
        assert maze.getChap().getX() == maze.getChapX();
        assert maze.getChap().getY() == maze.getChapY();
        assert maze.getLevel() == 1;
        assert maze.getInitialTime() == 100;
        assert maze.getLevelAsString().equals("001");
        assert maze.getChipsAsString().contentEquals("010");
        assert maze.getTimeAsString().equals("100");
        maze = newLevelTwo();
        Persistence per = new Persistence(null);
        per.loadLevels(null, 2);
        maze = newLevelTwo();

        for (int x = 0; x < per.getTiles().length; x++) {
            for (int y = 0; y < per.getTiles().length; y++) {
                assert maze.getTileAt(x, y).toString().equals(per.getTiles()[x][y].toString());
            }
        }
        for (BombTile bt : maze.getBombTiles()) {
            assert bt.getLastMove() == 'D';
        }
        for (EnemyTile et : maze.getEnemyTiles()) {
            assert et.getLastMove() == 'D';
        }
        assert maze.hasShield() == false;
        maze = newLevelTwo();
        maze.move("U");
        maze.move("U");
        maze.move("U");
        maze.move("L");
        maze.move("L");
        maze.move("U");
        maze.move("U");
        maze.move("U");
        maze.move("U");
        maze.move("D");
        maze.move("D");
        maze.move("D");
        maze.move("D");
        maze.move("D");
        for (int i = 0; i < 10; i++)
            maze.move("R");
        assert maze.hasShield();
    }

    @Test
    /**
     * Tests that the maze constructor does not accept a null persistence object.
     */
    public void testNullPers() {
        try {
            Maze m = new Maze(null);
            assert false;
        } catch (Exception e) {
            assert true;
        }

    }

    @Test
    /**
     * test shield functionality
     */
    public void testShield() {
        Maze maze = newLevelOne();
        maze.setTileAt(maze.getChapX(), maze.getChapY() - 1, new ShieldTile(maze.getChapX(), maze.getChapY() - 1));
        maze.move("U");
        maze.move("U");
        maze.getChap().killChap();
        assert !maze.hasShield();
        assert !maze.hasLost();
        assert !maze.hasEnded();
    }

    @Test
    /**
     * Test that the replay boolean works correctly
     */
    public void testReplayBool(){
        Maze m = newLevelTwo();
        m.setReplay(true);
        m.needsRendering();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert!m.needsRendering();
    }

    @Test
    /**
     * Test that the needsRendering() boolean works correctly.
     */
    public void testRenderIndicator(){
        Maze m = newLevelOne();
        m.move("U");
        assert(m.needsRendering());
        assert(!m.needsRendering());
    }
}
