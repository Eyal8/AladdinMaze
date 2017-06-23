package Model;

import algorithms.mazeGenerators.Position;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by eyal8_000 on 16/06/2017.
 */
public interface IModel {
    public int[][] getBoard();
    public void generateMaze(int rows, int columns);
    public void KeyPressed(KeyCode keyCode);
    public void solveMaze(int rows, int columns);
    public void exit();
    public int getCharacterPositionRow();
    public int getCharacterPositionColumn();
    public Position getGoalPosition();
    public void save(File chosen);
    public void load(File chosen);
    public ArrayList<Position> getPath();

}
