package nz.ac.vuw.ecs.swen225.gp20.recordReplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Game;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


import static nz.ac.vuw.ecs.swen225.gp20.application.GUI.isMovingRecorded;

/**
 * Records the player movement and the entire game and store them in the recordedGame.json file
 * @author Gelan Ejeta
 * @author Elgene Menon Leo Anthony
 */

public class Record {
    //stores the record process
    private Game game;
    private String recordFile; //stores the file
    private long finalTime;  //stores the last time when recording
    private boolean isRecording; //tracks the recording state when is playing

    /**
     * Constructor of the record
     * @param gameGui is the instance of game class
     */
    public Record(Game gameGui) {this.game = gameGui; }

    /**
     *The process of recording of player movement and save the entire game to a file
     */
    public void recording() {
        if (isRecording = true) { //when the game is in the recording state
            recordFile = "SavedGameFile/recordedGame.json";
            isMovingRecorded(true);   //tracks the player movement when recording
            game.getPersistence().saveGame(recordFile, true, game.getGameMaze().getMaze()); //Then save the record of whole game to the file
        }
    }

    /**
     * The process of stop recording
     * Overwrites the latest recoding to the same file
     */
    public void stopRecording() {
        //Writes to the file
        StringBuilder move;
        FileWriter fileWriter;
        BufferedWriter out;
        try {
            move = new StringBuilder();
            fileWriter = new FileWriter(recordFile, true); //overwrite the latest recoding each time when the recording stops
            out = new BufferedWriter(fileWriter);
            move.append("\n\t]}  "); //writes it to the end of the file
            out.append(move.toString());
            out.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the time when it is been recording
     * @param time take the final time when recording
     */
    public void setRecordTimer(long time) { finalTime = time; }

}





