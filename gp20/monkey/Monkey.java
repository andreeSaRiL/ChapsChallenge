package nz.ac.vuw.ecs.swen225.gp20.monkey;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Game;
import nz.ac.vuw.ecs.swen225.gp20.maze.*;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Monkey class is used for generating random input,
 * triggering exceptions or errors.
 *
 * @author Royan Saril
 */
public class Monkey {

    Game game = new Game();
    Maze maze = game.getGameMaze();
    GUI gui = game.getGameGUI();
    ChapTile chap = new ChapTile(maze.getChapX(), maze.getChapY());
    Tile[][] tile = null;

    /****************** TESTS ******************/

    /**
     * Testing when chap moves on the board within Level 1.
     * Test completes once chap has covered the entire level, and restarts level 1.
     */
    @Test
    public void level1Move() {
       // Maze maze = level1();
        Random rand = new Random();
        int num = rand.nextInt((4 - 1) + 1) + 1;
        int max = 10000000;

        for (int i = 0; i < max; i++) {
            if (maze.hasEnded())
                //maze = level1();
                game.getPersistence().loadLevels(null, 1);

            if (num == 1) {
                maze.move("U");
            }
            if (num == 2) {
                maze.move("D");
            }
            if (num == 3) {
                maze.move("L");
            }
            if (num == 4) {
                maze.move("R");
            }
        }
    }

    /**
     * Testing when chap moves on the board within Level 2.
     * Test completes once chap has covered the entire level, and restarts level 2.
     */
    @Test
    public void level2Move() {
       // Maze maze = level2();
        Random rand = new Random();
        int num = rand.nextInt((4 - 1) + 1) + 1;
        int max = 10000000;

        for (int i = 0; i < max; i++) {
            if (maze.hasEnded())
                //maze = level2();
                game.getPersistence().loadLevels(null, 2);

            if (num == 1) {
                maze.move("U");
            }
            if (num == 2) {
                maze.move("D");
            }
            if (num == 3) {
                maze.move("L");
            }
            if (num == 4) {
                maze.move("R");
            }
        }
    }

    /**
     * Tests that the main Game runs with no errors.
     */
    @Test
    public void testGame(){
        Game initGame = new Game();
        initGame.setGameMaze(null);

        try{
            String[] args = new String[] {""};
            Game.main(args);
        } catch(Error e) {
            fail("");
        }
        assertTrue(true);
    }

//    /**
//     * Testing that each level loads correctly.
//     */
//    @Test
//    public void testLevels(){
//        assertTrue(maze.getLevel() != 0);
//        assertFalse( maze.getLevel() == 0);
//
//        game.setGameMaze(maze);
//    }



    /****************** EXCEPTION TESTS ******************/

    /**
     * Testing restart game level 1.
     * @throws InterruptedException from sleep.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testRestartL1() throws InterruptedException {
        Game game2 = new Game();

        assertEquals(1, game2.getLevel());
        game2.setLevel(1);
        assertEquals(1, game2.getLevel());

        Thread.sleep(60);
        game2.setLevel(1);
        Thread.sleep(60);

        assertEquals(1, game2.getLevel());
    }

    /**
     * Testing restart game level 2.
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testRestartL2() throws InterruptedException {
        Game game2 = new Game();

        assertEquals(1, game2.getLevel());
        game2.setLevel(2);
        assertEquals(2, game2.getLevel());

        Thread.sleep(60);
        game2.setLevel(2);
        Thread.sleep(60);

        assertEquals(2, game2.getLevel());
    }

    /**
     * Testing move UP.
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testMoveUp() throws InterruptedException {
        Game game1 = new Game();
        GUI gui = game1.getGameGUI();
        Tile start = game1.getGameMaze().getChap();

        Thread.sleep(60);
        gui.keyPressed(new KeyEvent(gui, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.VK_UNDEFINED, KeyEvent.VK_UP));
        game1.setGameGUI(gui);
        Tile end = game1.getGameMaze().getChap();

        assertNotEquals(start.getX(), end);
    }

    /**
     * Testing move DOWN.
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testMoveDown() throws InterruptedException {
        Game game1 = new Game();
        GUI gui = game1.getGameGUI();
        Tile start = game1.getGameMaze().getChap();

        Thread.sleep(60);
        gui.keyPressed(new KeyEvent(gui, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.VK_UNDEFINED, KeyEvent.VK_DOWN));
        game1.setGameGUI(gui);
        Tile end = game1.getGameMaze().getChap();

        assertNotEquals(start.getX(), end);
    }

    /**
     * Testing move to the LEFT.
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testMoveLeft() throws InterruptedException {
        Game game1 = new Game();
        GUI gui = game1.getGameGUI();
        Tile start = game1.getGameMaze().getChap();

        Thread.sleep(60);
        gui.keyPressed(new KeyEvent(gui, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.VK_UNDEFINED, KeyEvent.VK_LEFT));
        game1.setGameGUI(gui);
        Tile end = game1.getGameMaze().getChap();

        assertNotEquals(start.getX(), end);
    }

    /**
     * Testing move to the RIGHT.
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testMoveRight() throws InterruptedException {
        Game game1 = new Game();
        GUI gui = game1.getGameGUI();
        Tile start = game1.getGameMaze().getChap();

        Thread.sleep(60);
        gui.keyPressed(new KeyEvent(gui, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.VK_UNDEFINED, KeyEvent.VK_RIGHT));
        game1.setGameGUI(gui);
        Tile end = game1.getGameMaze().getChap();

        assertNotEquals(start.getX(), end);
    }



    /****************** MOVEMENT TESTS ******************/

    /**
     * Tests legal move UP.
     */
    @Test
    public void legalmoveUP(){
        maze.setChap(chap);

        Tile start = maze.getTileAt(maze.getChapX(), maze.getChapY());
        maze.move("U");
        Tile end = maze.getTileAt(maze.getChapX(), maze.getChapY());

        assertEquals(start.getX(), end.getX());
    }

    /**
     *
     * Tests legal move DOWN.
     */
    @Test
    public void legalmoveDOWN(){
        maze.setChap(chap);

        Tile start = maze.getTileAt(maze.getChapX(), maze.getChapY());
        maze.move("D");
        Tile end = maze.getTileAt(maze.getChapX(), maze.getChapY());

        assertEquals(start.getX(), end.getX());
    }

    /**
     * Tests legal move LEFT.
     */
    @Test
    public void legalmoveLEFT(){
        maze.setChap(chap);

        Tile start = maze.getTileAt(maze.getChapX(), maze.getChapY());
        maze.move("L");
        Tile end = maze.getTileAt(maze.getChapX(), maze.getChapY());

        assertNotEquals(start.getX(), end.getX());
    }

    /**
     * Tests legal move RIGHT.
     */
    @Test
    public void legalmoveRIGHT(){
        maze.setChap(chap);

        Tile start = maze.getTileAt(maze.getChapX(), maze.getChapY());
        maze.move("R");
        Tile end = maze.getTileAt(maze.getChapX(), maze.getChapY());

        assertEquals(start.getY(), end.getY());
    }



    /****************** TILE TESTS ******************/

    /**
     * Tests legal door locked.
     * To unlock door.
     */
    @Test
    public void legalLockedDoor(){
        LockedDoorTile locked = new LockedDoorTile(0,0, "colour");

        maze.setChap(chap);
        assertFalse(locked.getX() == maze.getChapX() && locked.getY() == maze.getChapY());

        LockedDoorTile locked2 = new LockedDoorTile(0,0, "colour");
        assertTrue(locked2 != locked);
    }

    /**
     * Tests invalid key colour.
     */
    @Test
    public void invalidKeyCol(){
        boolean invalid = false;
        try{
            invalid = true;
            new KeyTile(0,0, null);
        }
        catch(AssertionError e){
            assertTrue(invalid);
        }
    }

//    /**
//     * Tests invalid door colour.
//     */
//    @Test
//    public void invalidDoorCol(){
//        boolean invalid = false;
//        try{
//            invalid = true;
//            new LockedDoorTile(0,0, null);
//        }
//        catch(AssertionError e){
//            assertTrue(invalid);
//        }
//    }

    /**
     * Tests Free Tile.
     */
    @Test
    public void freeTile(){
        FreeTile free = new FreeTile(0,0);
        free.setPos(0,0);

        assertFalse(free.getX() == maze.getChapX());
        assertFalse(free.getY() == maze.getChapY());

        FreeTile free2 = new FreeTile(0,0);
        assertTrue(free2 != free);
    }


    /**
     * Tests Exit Tile.
     */
    @Test
    public void exit(){
        ExitTile tile = new ExitTile(0,0);
        tile.setPos(maze.getChapX(), maze.getChapY());

        maze.setChap(chap);
        assertTrue(tile.getX() == maze.getChapX() && tile.getY() == maze.getChapY());

        ExitTile tile2 = new ExitTile(0,0);
        assertTrue(tile2 != tile);
    }

    /**
     * Tests exit with treasure.
     */
    @Test
    public void exitTreasure(){
        ExitTile exit = new ExitTile(0,0);
        TreasureTile treas = new TreasureTile(0,0);

        maze.setChap(chap);
        assertFalse(treas.getX() == maze.getChapX() && treas.getY() == maze.getChapY());
        assertFalse(exit.getX() == maze.getChapX() && exit.getY() == maze.getChapY());
        assertFalse(maze.hasLost());
    }

    /**
     * Tests exit withOUT treasure.
     */
    @Test
    public void exitNoTreasure(){
        ExitTile exit = new ExitTile(0,0);
        TreasureTile treas = new TreasureTile(0,0);

        maze.setChap(chap);
        assertTrue(treas.getX() != maze.getChapX() && treas.getY() != maze.getChapY());
        assertFalse(exit.getX() == maze.getChapX() && exit.getY() == maze.getChapY());
        assertFalse(maze.hasWon());
    }

    /**
     * Tests bomb on chap and kills chap.
     */
    @Test
    public void testBomb() {
        BombTile bomb = new BombTile(0,0);

        maze.setChap(chap);
        assertFalse(bomb.getX() == maze.getChapX() && bomb.getY() == maze.getChapY());
        chap.killChap();
    }
}

