package Model;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Created by eyal8_000 on 16/06/2017.
 */
public interface IModel {
    public int[][] getBoard();
    public void generateMaze(int rows, int columns);
    public void KeyPressed(KeyCode keyCode);
//    public void load();
  //  public void save();
    public int getCharacterPositionRow();
    public int getCharacterPositionColumn();


    }
